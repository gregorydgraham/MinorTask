/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.NavigationTrigger;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.dbvolution.databases.DBDatabaseCluster;
import nz.co.gregs.dbvolution.databases.DBDatabaseClusterWithConfigFile;
import nz.co.gregs.dbvolution.databases.SQLiteDB;
import nz.co.gregs.dbvolution.datatypes.DBPasswordHash;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.dbvolution.exceptions.IncorrectPasswordException;
import nz.co.gregs.dbvolution.exceptions.NoAvailableDatabaseException;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.dbvolution.query.TreeNode;
import nz.co.gregs.minortask.datamodel.*;
import nz.co.gregs.minortask.pages.LoginPage;
import nz.co.gregs.minortask.pages.LoggedOutPage;
import nz.co.gregs.minortask.pages.ProjectsLayout;
import nz.co.gregs.minortask.pages.SignUpLayout;
import nz.co.gregs.minortask.pages.TaskCreatorLayout;
import nz.co.gregs.minortask.pages.TaskEditorLayout;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gregorygraham
 */
public class MinorTask implements Serializable {

	private long userID = 0;
	boolean notLoggedIn = true;
	public String username = "";
	private Location loginDestination = null;

	static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MinorTask.class);

	public MinorTask() {
		setupDatabase();
	}

	static DBDatabaseCluster database;

	public static Date asDate(LocalDate localDate) {
		return localDate == null ? null : Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date) {
		return date == null ? null : Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return date == null ? null : Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static String shorten(String value, int i) {
		return value == null ? null : value.substring(0, value.length() < i ? value.length() : i);
	}

	public final void warning(final String topic, final String warning) {
		Button closeButton = new Button("Nevermind");
		VerticalLayout layout = new VerticalLayout(new Label(topic), new Label(warning), closeButton);
		Notification note = new Notification(layout);
		closeButton.addClickListener((event) -> {
			note.close();
		});
		note.setPosition(Notification.Position.MIDDLE);
		note.setDuration(5000);
		note.open();
	}

	public final void error(final String topic, final String error) {
		Button closeButton = new Button("Oops");
		VerticalLayout layout = new VerticalLayout(new Label(topic), new Label(error), closeButton);
		Notification note = new Notification(layout);
		closeButton.addClickListener((event) -> {
			note.close();
		});
		note.setPosition(Notification.Position.TOP_CENTER);
		note.open();
	}

	public final void chat(String string) {
		Notification note = new Notification(string, 3000);
		note.setPosition(Notification.Position.BOTTOM_END);
		note.open();
	}

	public final void debug(String string) {
		Notification note = new Notification(string, 3000);
		note.setPosition(Notification.Position.BOTTOM_END);
		note.open();
	}

	public final void sqlerror(Exception exp) {
		Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, exp);
		final String localizedMessage = exp.getMessage();
		System.err.println("" + localizedMessage);
		Button closeButton = new Button("Darn it!");
		Notification note = new Notification(new Label("SQL ERROR"), new Label(localizedMessage), closeButton);
		closeButton.addClickListener((event) -> {
			note.close();
		});
		note.getElement().setAttribute("theme", "error");
		note.setPosition(Notification.Position.TOP_CENTER);
		note.open();
	}

	public Task getTask(Long taskID) throws InaccessibleTaskException {
		return getTask(taskID, getUserID());
	}

	private Task getTask(Long taskID, final Long userID) throws InaccessibleTaskException {
		System.out.println("nz.co.gregs.minortask.MinorTask.getTask() TASKID:" + taskID);
		System.out.println("nz.co.gregs.minortask.MinorTask.getTask() USERID:" + userID);

		Task returnTask = null;
		if (taskID == null) {
			return returnTask;
		}
		final Task example = new Task();
		example.taskID.permittedValues(taskID);
		example.userID.permittedValues(userID);
		try {
			return getDatabase().getDBTable(example).getOnlyRow();
		} catch (UnexpectedNumberOfRowsException ex) {
//			warning("Incorrect Number Of Rows", "" + ex.getActualRows() + " <> " + ex.getExpectedRows());
			throw new InaccessibleTaskException(taskID);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		return returnTask;
	}

	public List<Task> getActiveSubtasks(Long taskID, final Long userID) {
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
					debug("Database created from \"" + configFile + "\" in " + (new File(configFile).getAbsolutePath()));

				}
			} catch (DBDatabaseClusterWithConfigFile.NoDatabaseConfigurationFound | DBDatabaseClusterWithConfigFile.UnableToCreateDatabaseCluster | NoAvailableDatabaseException ex) {
				warning("Configuration Missing", "We were unable to find the database configuration \"" + configFile + "\" in " + (new File(configFile).getAbsolutePath()));
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
				final String error = "Unable to find database " + configFile;
				System.err.println("" + error);
			}
		}
		if (database == null) {
			try {
				database = getEmergencyDatabase();
				warning("Emergency Database", "We were unable to find the database and are now running on an empty database");
			} catch (Exception ex1) {
				error("No Database", "We were unable to find the database nor create an empty database, everything is cack.");
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex1);
				System.err.println("" + ex1.getLocalizedMessage());
				sqlerror(ex1);
			}
		}
	}

	public void chatAboutUsers() {
		try {
			String message
					= "Currently serving " + database.getDBTable(new User()).setBlankQueryAllowed(true).count() + " users "
					+ "and " + database.getDBTable(new Task()).setBlankQueryAllowed(true).count() + " tasks";
			chat(message);
		} catch (SQLException ex) {
			Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	protected DBDatabaseCluster getEmergencyDatabase() throws IOException, SQLException {
		warning("No Database Configured", "No database configuration was found, MinorTask is now running on the temporary database.");
		return new DBDatabaseCluster(new SQLiteDB(new File("MinorTask-default.sqlite"), "admin", "admin"));
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

	public static Task getTaskExample(final Long taskID, final Long userID) {
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
		UI.getCurrent().navigate(LoginPage.class);
	}

	public void showLogin(String username, String password) {
		UI.getCurrent().navigate(LoginPage.class, username);
	}

	public void showTopLevelTasks() {
		UI.getCurrent().navigate(ProjectsLayout.class, null);
	}

	public void showTask(Long taskID) {
		if (taskID == null) {
			showTopLevelTasks();
		} else {
			UI.getCurrent().navigate(TaskEditorLayout.class, taskID);
		}
	}

	public void showTaskCreation(Long taskID) {
		UI.getCurrent().navigate(TaskCreatorLayout.class, taskID);
	}

	public Task.WithSortColumns getTaskWithSortColumnsExampleForTaskID(Long taskID) {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(getUserID());
		example.projectID.permittedValues(taskID);
		return example;
	}

	/**
	 * @param userID the userID to set
	 * @throws nz.co.gregs.minortask.MinorTask.UnknownUserException
	 * @throws nz.co.gregs.minortask.MinorTask.TooManyUsersException
	 */
	public void setUserID(long userID) throws UnknownUserException, TooManyUsersException {
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
			if (ex.getActualRows() > ex.getExpectedRows()) {
				throw new TooManyUsersException();
			} else {
				throw new UnknownUserException();
			}
		}
	}

	public synchronized void loginAs(User user, String password) throws UnknownUserException, TooManyUsersException, SQLException, IncorrectPasswordException {
		Long userID = user.getUserID();
		DBPasswordHash queryPassword = user.queryPassword();
		String oldHash = queryPassword.getValue();
		queryPassword.checkPasswordAndUpdateHash(password);
		if (oldHash == null ? queryPassword.getValue() != null : !oldHash.equals(queryPassword.getValue())) {
			database.update(user);
		}
		this.notLoggedIn = false;
		this.setUserID(userID);
		showLoginDestination();
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	public void logout() {
		this.setLoginDestination(null);
		this.userID = 0;
		this.username = null;
		notLoggedIn = true;
		UI current = UI.getCurrent();
		current.getRouter().navigate(
				current,
				getCurrentLocation(),
				NavigationTrigger.PROGRAMMATIC);
	}

	public boolean getNotLoggedIn() {
		final VaadinSession currentSession = VaadinSession.getCurrent();
		return notLoggedIn
				|| userID == 0
				|| !currentSession.getState().equals(VaadinSessionState.OPEN);
	}

	public boolean getLoggedIn() {
		return !getNotLoggedIn();
	}

	public void showSignUp(String username, String password) {
		UI.getCurrent().navigate(
				SignUpLayout.class,
				username
		);
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

	public void setLoginDestination(Location location) {
		this.loginDestination = location;
	}

	public Task.TaskAndProject getTaskAndProject(Long taskID) throws InaccessibleTaskException {
		if (taskID != null) {
			final Task example = new Task();
			example.taskID.permittedValues(taskID);
			example.userID.permittedValues(getUserID());
			final Task.Project projectExample = new Task.Project();
			DBQuery dbQuery = getDatabase().getDBQuery(example).addOptional(projectExample);
			try {
				System.out.println(dbQuery.getSQLForQuery());
				List<DBQueryRow> allRows = dbQuery.getAllRows(1);
				final DBQueryRow onlyRow = allRows.get(0);
				return new Task.TaskAndProject(onlyRow.get(example), onlyRow.get(projectExample));
			} catch (SQLException | AccidentalBlankQueryException | AccidentalCartesianJoinException ex) {
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
			} catch (UnexpectedNumberOfRowsException ex) {
				throw new InaccessibleTaskException(taskID);
			}
		}
		return new Task.TaskAndProject(null, null);
	}

	public boolean isLoggedIn() {
		return this.userID > 0 && VaadinSession.getCurrent().getState().equals(VaadinSessionState.OPEN);
	}

	public String getApplicationName() {
		return "MinorTask";
	}

	public Task.Project getNullProject() {
		Task.Project project = new Task.Project();
		project.taskID.setValue(-1);
		project.userID.setValue(getUserID());
		project.name.setValue("Projects");
		return project;
	}

	public Location getCurrentLocation() {
		RouteData route = UI.getCurrent().getRouter().getRoutes().get(0);
		String url = route.getUrl();
		Location location = new Location(url);
		return location;
	}

	private void showLoginDestination() {
		Location dest = getLoginDestination();
		if (dest != null) {
			String pathWithQueryParameters = dest.getPathWithQueryParameters();
			if (pathWithQueryParameters.isEmpty()) {
				showTopLevelTasks();
			} else {
				UI.getCurrent().navigate(pathWithQueryParameters);
			}
		} else {
			showTopLevelTasks();
		}
	}

	public static class InaccessibleTaskException extends Exception {

		public InaccessibleTaskException(Long taskID) {
		}
	}

	public static class TooManyUsersException extends Exception {

		public TooManyUsersException() {
		}
	}

	public static class UnknownUserException extends Exception {

		public UnknownUserException() {
		}
	}

	/**
	 * @return the loginDestination
	 */
	public Location getLoginDestination() {
		System.out.println("LOGIN DESTINATION: "
				+ (loginDestination == null
						? "NULL"
						: loginDestination.getPathWithQueryParameters())
		);
		return loginDestination;
	}
}
