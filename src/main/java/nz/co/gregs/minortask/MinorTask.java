/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.databases.DBDatabaseCluster;
import nz.co.gregs.dbvolution.databases.DBDatabaseClusterWithConfigFile;
import nz.co.gregs.dbvolution.databases.H2MemoryDB;
import nz.co.gregs.dbvolution.databases.SQLiteDB;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.components.*;
import nz.co.gregs.minortask.datamodel.*;

/**
 *
 * @author gregorygraham
 */
public class MinorTask implements Serializable {

	private long userID = 0;
	private Long currentTaskID;
	boolean notLoggedIn = true;
	public String username = "";
	private VaadinSession sess;
	private final MinorTaskUI ui;

	public MinorTask(MinorTaskUI ui) {
		this.ui = ui;
		setupDatabase();
	}

	static DBDatabase database;

	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static String asDateString(Date value, UI ui) {
		return MinorTask.asLocalDate(value).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(ui.getLocale()));
	}

	public static String shorten(String value, int i) {
		return value.substring(0, value.length() < i ? value.length() : i);
	}

	public final void warning(final String topic, final String warning) {
		Notification note = new Notification(topic, warning, Notification.Type.WARNING_MESSAGE);
		note.show(Page.getCurrent());
	}

	public final void error(final String topic, final String error) {
		Notification note = new Notification(topic, error, Notification.Type.ERROR_MESSAGE);
		note.show(Page.getCurrent());
	}

	public final void chat(String string) {
		new Notification(string, Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
	}

	public final void sqlerror(Exception exp) {
		Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, exp);
		final String localizedMessage = exp.getLocalizedMessage();
		System.err.println(""+localizedMessage);
		Notification note = new Notification("SQL ERROR", localizedMessage, Notification.Type.ERROR_MESSAGE);
		note.show(Page.getCurrent());
//		final StackTraceElement[] stackTraceArray = exp.getStackTrace();
//		for (StackTraceElement stackTraceElement : stackTraceArray) {
//			if (stackTraceElement.toString().contains("minortask")) {
//				note = new Notification("SQL ERROR", stackTraceElement.toString(), Notification.Type.ERROR_MESSAGE);
//				note.show(Page.getCurrent());
//				break;
//			}
//		}
	}

	public Task getTask(Long taskID, final long userID) {
		Task returnTask = null;
		final Task task = getTaskExample(taskID, userID);
		task.taskID.permittedValues(taskID);
		try {
			returnTask = getDatabase().getDBTable(task).getOnlyRow();
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
			sqlerror(ex);
		}
		return returnTask;
	}

	public List<Task> getActiveSubtasks(Long taskID, final long userID) {
		ArrayList<Task> arrayList = new ArrayList<Task>();
		final Task example = getProjectExample(taskID, userID);
		example.completionDate.permittedValues((Date) null);
		try {
			List<Task> allRows = getDatabase().getDBTable(example).getAllRows();
			return allRows;
		} catch (SQLException ex) {
			sqlerror(ex);
		}

		return arrayList;
	}

	public final synchronized void setupDatabase() {
		if (database == null) {
			final String configFile = "MinorTaskDatabaseConfig.yml";
			try {
				database = new DBDatabaseClusterWithConfigFile(configFile);
			} catch (SQLException ex) {
				Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, ex);
				new Notification("Unable to find database " + configFile, Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
//				sqlerror(ex);
				try {
					final DBDatabaseCluster dbDatabaseCluster = new DBDatabaseCluster();
					database = dbDatabaseCluster;
					dbDatabaseCluster.addDatabaseAndWait(new SQLiteDB(new File("MinorTask-default.sqlite"), "admin", "admin"));
				} catch (SQLException ex1) {
					Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex1);
					sqlerror(ex);
				} catch (IOException ex1) {
					Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex1);
				}
			}
		}
		try {
			new Notification("Currently serving " + database.getDBTable(new User()).setBlankQueryAllowed(true).count() + " users and " + database.getDBTable(new Task()).setBlankQueryAllowed(true).count() + " tasks", Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
		} catch (SQLException ex) {
			Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, ex);
			sqlerror(ex);
		}
	}

	public synchronized DBDatabase getDatabase() {
		if (database == null) {
			setupDatabase();
		}
		return database;
	}

	public List<Task> getProjectPathTasks(Long taskID, final long userID) {
		try {
			final Task task = getTaskExample(taskID, userID);
			DBQuery query = getDatabase().getDBQuery(task);
			DBRecursiveQuery<Task> recurse = new DBRecursiveQuery<Task>(query, task.column(task.projectID));
			List<Task> ancestors = recurse.getAncestors();
			return ancestors;
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		return new ArrayList<>();
	}

	public static Task getTaskExample(final Long taskID, final long userID) {
		Task task;
		task = new Task();
		task.taskID.permittedValues(taskID);
		task.userID.permittedValues(userID);
		return task;
	}

	public static Task getProjectExample(Long taskID, long userID) {
		Task task;
		task = new Task();
		task.projectID.permittedValues(taskID);
		task.userID.permittedValues(userID);
		return task;
	}

	public void showLogin() {
		showPublicContent(new LoginComponent(this));
	}

	public void showLogin(String username, String password) {
		showPublicContent(new LoginComponent(this, username, password));
	}

	public void showTopLevelTasks() {
		showTask(null);
	}

	public void showCurrentTask() {
		showTask(currentTaskID);
	}

	public void showTask(Long taskID) {
		TaskEditor taskComponent = new TaskEditor(this, taskID);
		showAuthorisedContent(taskID, taskComponent);
	}

	public void showTaskCreation(Long taskID) {
		showAuthorisedContent(taskID, new TaskCreator(this, taskID));
	}

	public Long getCurrentTaskID() {
		return currentTaskID;
	}

	public Task.WithSortColumns getTaskWithSortColumnsExampleForTaskID(Long taskID) {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(getUserID());
		example.projectID.permittedValues(taskID);
		return example;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(long userID) {
		this.userID = userID;
		User user = new User();
		user.queryUserID().permittedValues(userID);
		try {
			User onlyRow = getDatabase().get(1L, user).get(0);
			username = onlyRow.getUsername();
		} catch (SQLException ex) {
			error("SQL ERROR", ex.getLocalizedMessage());
		} catch (UnexpectedNumberOfRowsException ex) {
			error("MULTIPLE USER ERROR", "Oops! This should not have happened.\n Please contact MinorTask to get it fixed.");
		}
	}

	public void loginAs(Long userID) {
		this.notLoggedIn = false;
		this.userID = userID;
		showTask(null);
	}

	void setupSession(VaadinRequest vaadinRequest) {
		sess = VaadinSession.getCurrent();
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	private void showAuthorisedContent(Long taskID, Component component) {
		if (notLoggedIn) {
			showLogin();
		} else {
			setCurrentTaskID(taskID);
			VerticalLayout display = new VerticalLayout();
			display.addComponent(new BannerMenu(this, taskID));
			display.addComponent(component);
			display.addComponent(new FooterMenu(this, taskID));
			ui.setContent(display);
			ui.setScrollTop(0);
		}
	}

	public void logout() {
		ui.setContent(new LoggedoutComponent(this));
		notLoggedIn = true;
		sess.close();
	}

	public void showPublicContent(Component component) {
		ui.setContent(component);
	}

	public boolean getNotLoggedIn() {
		return notLoggedIn;
	}

	public void showSignUp(String username, String password) {
		showPublicContent(new SignupComponent(this, username, password));
	}

	private void setCurrentTaskID(Long newTaskID) {
		currentTaskID = newTaskID;
	}

	/**
	 * @return the userID
	 */
	public long getUserID() {
		return userID;
	}
}
