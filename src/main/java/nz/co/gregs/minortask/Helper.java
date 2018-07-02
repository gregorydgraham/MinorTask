/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import java.io.File;
import java.io.IOException;
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
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.databases.DBDatabaseCluster;
import nz.co.gregs.dbvolution.databases.DBDatabaseClusterWithConfigFile;
import nz.co.gregs.dbvolution.databases.H2MemoryDB;
import nz.co.gregs.dbvolution.databases.SQLiteDB;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class Helper {

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
		return Helper.asLocalDate(value).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(ui.getLocale()));
	}

	public static String shorten(String value, int i) {
		return value.substring(0, value.length()<i?value.length():i);
	}

	public static final void warning(final String topic, final String warning) {
		Notification note = new Notification(topic, warning, Notification.Type.WARNING_MESSAGE);
		note.show(Page.getCurrent());
	}

	public static final void error(final String topic, final String error) {
		Notification note = new Notification(topic, error, Notification.Type.ERROR_MESSAGE);
		note.show(Page.getCurrent());
	}

	public static final void chat(String string) {
		new Notification(string, Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
	}

	public static final void sqlerror(Exception exp) {
		Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, exp);
		Notification note = new Notification("SQL ERROR", exp.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
		note.show(Page.getCurrent());
	}
//	public static final Button LOGOUT_BUTTON = new Button("Log Out");
//	public static final TextField USERNAME_FIELD = new TextField("Your Name");
//	public static final PasswordField PASSWORD_FIELD = new PasswordField("Password");

	public static Task getTask(Long taskID) {
		Task returnTask = null;
		final Task task = new Task();
		task.taskID.permittedValues(taskID);
		try {
			returnTask = getDatabase().getDBTable(task).getOnlyRow();
		} catch (UnexpectedNumberOfRowsException|SQLException ex) {
			Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
			sqlerror(ex);
		} 
		return returnTask;
	}

	public static List<Task> getSubTasks(Long taskID) {
		ArrayList<Task> arrayList = new ArrayList<Task>();
		final Task example = new Task();
		example.projectID.permittedValues(taskID);
		try {
			List<Task> allRows = getDatabase().getDBTable(example).getAllRows();
			return allRows;
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		
		return arrayList;
	}

	private Helper() {
	}

	public static synchronized void setupDatabase() {
		if (Helper.database == null) {
			try{
				database = new DBDatabaseClusterWithConfigFile("MinorTaskDatabaseConfig.yml");
//			final String basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
//			final File sqliteFile = new File(basePath + "/WEB-INF/MinorTask.sqlite");
//			try {
//				Helper.database = new DBDatabaseCluster(new SQLiteDB(sqliteFile, "admin", "admin"), new H2MemoryDB("MinorTask.h2", "admin", "admin", true));
//				Helper.database.setPrintSQLBeforeExecuting(true);
			} catch (SQLException ex) {
				Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, ex);
//				new Notification("NO DATABASE: " + ex.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
				sqlerror(ex);
			}
		}
		try {
			new Notification("Currently serving " + Helper.database.getDBTable(new User()).setBlankQueryAllowed(true).count() + " users and " + Helper.database.getDBTable(new Task()).setBlankQueryAllowed(true).count() + " tasks", Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
		} catch (SQLException ex) {
			Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, ex);
//			new Notification("NO DATABASE CONNECTION: " + ex.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			sqlerror(ex);
		}
	}

	public static DBDatabase getDatabase() {
		if (Helper.database == null) {
			setupDatabase();
		}
		return Helper.database;
	}
}
