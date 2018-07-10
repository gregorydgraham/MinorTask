/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
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
import nz.co.gregs.dbvolution.databases.SQLiteDB;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.dbvolution.query.TreeNode;
import nz.co.gregs.minortask.components.*;
import nz.co.gregs.minortask.datamodel.*;
import nz.co.gregs.minortask.pages.LoginLayout;
import nz.co.gregs.minortask.pages.LogoutLayout;
import nz.co.gregs.minortask.pages.SignUpLayout;
import nz.co.gregs.minortask.pages.TaskCreatorLayout;
import nz.co.gregs.minortask.pages.TaskEditorLayout;

/**
 *
 * @author gregorygraham
 */
public class MinorTask implements Serializable {

	private long userID = 0;
	private Long currentTaskID;
	boolean notLoggedIn = true;
	public String username = "";
//	private VaadinSession sess;
//	private final MinorTaskUI ui;

	public MinorTask() {
//		this.ui = null;
		setupDatabase();
	}

	public MinorTask(MinorTaskUI ui) {
//		this.ui = ui;
		setupDatabase();
	}

	static DBDatabaseCluster database;

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
		Notification note = new Notification(new Label(topic), new Label(warning));
		note.setPosition(Notification.Position.MIDDLE);
		note.open();
	}

	public final void error(final String topic, final String error) {
		Notification note = new Notification(new Label(topic), new Label(error));
		note.setPosition(Notification.Position.TOP_CENTER);
		note.open();
	}

	public final void chat(String string) {
		Notification note = new Notification(string, 5000);
		note.setPosition(Notification.Position.BOTTOM_END);
		note.open();
	}

	public final void sqlerror(Exception exp) {
		Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, exp);
		final String localizedMessage = exp.getLocalizedMessage();
		System.err.println("" + localizedMessage);
		Notification note = new Notification(new Label("SQL ERROR"), new Label(localizedMessage));
		note.setPosition(Notification.Position.MIDDLE);
		note.open();
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
				final DBDatabaseClusterWithConfigFile dbDatabaseClusterWithConfigFile = new DBDatabaseClusterWithConfigFile(configFile);
				if (dbDatabaseClusterWithConfigFile.getReadyDatabase() != null) {
					database = dbDatabaseClusterWithConfigFile;
				}
			} catch (Exception ex) {
				Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, ex);
				final String error = "Unable to find database " + configFile;
				System.err.println("" + error);
				sqlerror(ex);
				try {
					database = new DBDatabaseCluster(new SQLiteDB(new File("MinorTask-default.sqlite"), "admin", "admin"));
				} catch (SQLException | IOException ex1) {
					Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex1);
					System.err.println("" + ex.getLocalizedMessage());
					sqlerror(ex);
				}
			}
		}
		try {
			chat("Currently serving " + database.getDBTable(new User()).setBlankQueryAllowed(true).count() + " users and " + database.getDBTable(new Task()).setBlankQueryAllowed(true).count() + " tasks");
		} catch (SQLException ex) {
			Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, ex);
			sqlerror(ex);
		}
	}

	public synchronized DBDatabaseCluster getDatabase() {
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

	public List<TreeNode<Task>> getProjectTreeTasks(Long taskID, final long userID) {
		try {
			final Task task = getTaskExample(taskID, userID);
			DBQuery query = getDatabase().getDBQuery(task);
			DBRecursiveQuery<Task> recurse = new DBRecursiveQuery<Task>(query, task.column(task.projectID));
			List<TreeNode<Task>> descendants = recurse.getTrees();
			return descendants;
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

//		showPublicContent(new LoginComponent(this));
	}

	public void showLogin(String username, String password) {
		UI.getCurrent().navigate(LoginLayout.class, username);
//		showPublicContent(new LoginComponent(this, username, password));
	}

	public void showTopLevelTasks() {
		showTask(null);
	}

	public void showCurrentTask() {
		showTask(currentTaskID);
	}

	public void showTask(Long taskID) {
		UI.getCurrent().navigate(TaskEditorLayout.class, taskID);
//		TaskEditor taskComponent = new TaskEditor(taskID);
//		showAuthorisedContent(taskID, taskComponent);
	}

	public void showTaskCreation(Long taskID) {
		UI.getCurrent().navigate(TaskCreatorLayout.class, taskID);
//		showAuthorisedContent(taskID, new TaskCreator(taskID));
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

//	void setupSession(VaadinRequest vaadinRequest) {
//		sess = VaadinSession.getCurrent();
//	}
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
			display.add(new BannerMenu(taskID));
			display.add(component);
			display.add(new FooterMenu(taskID));
//			ui.setContent(display);
//			ui.setScrollTop(0);
		}
	}

	public void logout() {
		UI.getCurrent().navigate(LogoutLayout.class);
//		ui.setContent(new LoggedoutComponent(this));
		notLoggedIn = true;
		VaadinSession.getCurrent().close();
	}

	public void showPublicContent(Component component) {
//		ui.setContent(component);
	}

	public boolean getNotLoggedIn() {
		return notLoggedIn;
	}

	public void showSignUp(String username, String password) {
//		showPublicContent(new SignupComponent(this, username, password));
		UI.getCurrent().navigate(
				SignUpLayout.class,
				username
		);
	}

	public void setCurrentTaskID(Long newTaskID) {
		currentTaskID = newTaskID;
	}

	/**
	 * @return the userID
	 */
	public long getUserID() {
		return userID;
	}

	public void enforceDateConstraintsOnTaskTree(Task task) {
		List<TreeNode<Task>> projectTreeTasks = this.getProjectTreeTasks(task.taskID.getValue(), task.userID.getValue());
		for (TreeNode<Task> projectTreeTaskNode : projectTreeTasks) {
			enforceDateConstraintsOnTaskTree(task, projectTreeTaskNode);
		}
	}

	private void enforceDateConstraintsOnTaskTree(Task task, TreeNode<Task> node) {
		Task subtask = node.getData();
		final Date taskStart = task.startDate.getValue();
		final Date taskDeadline = task.finalDate.getValue();
		if (taskDeadline.before(subtask.finalDate.getValue())) {
			subtask.finalDate.setValue(taskDeadline);
			try {
				getDatabase().update(subtask);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
		final Date subtaskStart = subtask.startDate.getValue();
		if (subtaskStart.before(taskStart)) {
			task.startDate.setValue(subtaskStart);
			try {
				getDatabase().update(task);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
	}

	public Task.Project getProject() {
		if (getCurrentTaskID() == null) {
			return null;
		} else {
			final Task.Project project = new Task.Project();
			try {
				List<Task.Project> projects = getDatabase().getDBQuery(getTask(), project).getAllInstancesOf(project);
				if (projects.isEmpty()) {
					return null;
				} else {
					return projects.get(0);
				}
			} catch (SQLException ex) {
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}

	public Task getTask() {
		return getTask(getCurrentTaskID(), getUserID());
	}
}
