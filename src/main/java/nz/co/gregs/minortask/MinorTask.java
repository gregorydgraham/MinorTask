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
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.dbvolution.databases.DBDatabase;
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
import nz.co.gregs.minortask.pages.ProjectsLayout;
import nz.co.gregs.minortask.pages.SignUpLayout;
import nz.co.gregs.minortask.pages.TaskCreatorLayout;
import nz.co.gregs.minortask.pages.TaskEditorLayout;
import nz.co.gregs.minortask.pages.TodaysTaskLayout;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.pages.LostPasswordLayout;
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
		setDatabase(setupDatabase());
	}

	private static DBDatabase database = null;

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

	public final synchronized DBDatabase setupDatabase() {

		if (database == null) {
			String configFile = "MinorTaskDatabaseConfig.yml";
			try {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				configFile = (String) envCtx.lookup("MinorTaskDatabaseConfigFilename");
				final DBDatabaseClusterWithConfigFile dbDatabaseClusterWithConfigFile = new DBDatabaseClusterWithConfigFile(new File(configFile));
				if (dbDatabaseClusterWithConfigFile.getReadyDatabase() != null) {
					database = dbDatabaseClusterWithConfigFile;
					debug("Database created from \"" + configFile + "\" in " + (new File(configFile).getAbsolutePath()));

				}
			} catch (DBDatabaseClusterWithConfigFile.NoDatabaseConfigurationFound | DBDatabaseClusterWithConfigFile.UnableToCreateDatabaseCluster | NoAvailableDatabaseException ex) {
				warning("Configuration Missing", "We were unable to find the database configuration \"" + configFile + "\" in " + (new File(configFile).getAbsolutePath()));
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
				final String error = "Unable to find database " + configFile;
				System.err.println("" + error);
			} catch (NamingException ex) {
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		if (database == null) {
			try {
				database = getEmergencyDatabase();
				warning("Emergency Database", "We were unable to find the database and are now running on an empty database");
			} catch (IOException | SQLException ex1) {
				error("No Database", "We were unable to find the database nor create an empty database, everything is cack.");
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex1);
				System.err.println("" + ex1.getLocalizedMessage());
				sqlerror(ex1);
			}
		}
		return database;
	}

	public final String EMAIL_CONFIG_CONTEXT_VAR = "MinorTaskEmailConfigFilename";

	public final synchronized Session setupEmailSession() {
		Session emailSession = null;
		if (emailSession == null) {
			String configFilename = "MinorTaskEmailConfig.yml";
			try {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				configFilename = (String) envCtx.lookup(EMAIL_CONFIG_CONTEXT_VAR);
				File configFile = new File(configFilename);
				final Session emailSessionFromConfigFile = new EmailSessionFromConfigFile(configFile).getSession();
				emailSession = emailSessionFromConfigFile;
				debug("Email session created from \"" + configFilename + "\" in " + (configFile.getAbsolutePath()));
			} catch (IOException ex) {
				error("IO Exception", "We were unable to read the email configuration in " + (new File(configFilename).getAbsolutePath()));
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
				final String error = "Unable to read email " + configFilename;
				System.err.println("" + error);
			} catch (NamingException ex) {
				error("Configuration Missing", "No such value \"" + configFilename + "\" in " + (new File(configFilename).getAbsolutePath()));
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
				final String error = "Unable to find email " + configFilename;
				System.err.println("" + error);
			} catch (EmailSessionFromConfigFile.NoEmailConfigurationFound ex) {
				error("Configuration Missing", "We were unable to find the email configuration \"" + configFilename + "\" in " + (new File(configFilename).getAbsolutePath()));
				Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
				final String error = "Unable to find email " + configFilename;
				System.err.println("" + error);
			}
		}
		if (emailSession == null) {
			error("No Email Server", "We were unable to create an email session.");
		}
		return emailSession;
	}

	public void chatAboutUsers() {
		try {
			String message
					= "Currently serving " + getDatabase().getDBTable(new User()).setBlankQueryAllowed(true).count() + " users "
					+ "and " + getDatabase().getDBTable(new Task()).setBlankQueryAllowed(true).count() + " tasks";
			chat(message);

		} catch (SQLException ex) {
			Logger.getLogger(MinorTask.class
					.getName()).log(Level.SEVERE, null, ex);
		}
	}

	protected DBDatabaseCluster getEmergencyDatabase() throws IOException, SQLException {
		warning("No Database Configured", "No database configuration was found, MinorTask is now running on the temporary database.");
		return new DBDatabaseCluster(new SQLiteDB(new File("MinorTask-default.sqlite"), "admin", "admin"));
	}

	private void setDatabase(DBDatabase db) {
		setSessionAttribute(MINORTASK_DATABASE_ATTRIBUTE_NAME, db);
	}
	private static final String MINORTASK_DATABASE_ATTRIBUTE_NAME = "minortask_database";

	private static final String MINORTASK_EMAILSESSION = "minortask_emailsession";

	private void setSessionAttribute(String sessionAttributeName, Object obj) {
		VaadinSession sess = VaadinSession.getCurrent();
		Lock lockInstance = sess.getLockInstance();
		try {
			lockInstance.lock();
			sess.setAttribute(sessionAttributeName, obj);
		} finally {
			lockInstance.unlock();
		}
	}

	public synchronized DBDatabase getDatabase() {
		DBDatabase db = (DBDatabase) UI.getCurrent().getSession().getAttribute(MINORTASK_DATABASE_ATTRIBUTE_NAME);
		if (db == null) {
			setDatabase(setupDatabase());
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
		UI.getCurrent().navigate(LoginPage.class
		);
	}

	public void showLogin(String username, String password) {
		UI.getCurrent().navigate(LoginPage.class,
				username);
	}

	public void showOpeningPage() {
		System.out.println("SHOW OPENING PAGE");
		showTodaysTasks();
	}

	public void showProjects() {
		UI.getCurrent().navigate(ProjectsLayout.class);
	}

	public void showTodaysTasks() {
		UI.getCurrent().navigate(TodaysTaskLayout.class);
	}

	public void showTask(Long taskID) {
		if (taskID == null) {
			showProjects();
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

	public synchronized void loginAs(User user, String password, Boolean rememberMe) throws UnknownUserException, TooManyUsersException, SQLException, IncorrectPasswordException {
		DBPasswordHash queryPassword = user.queryPassword();
		String oldHash = queryPassword.getValue();
		queryPassword.checkPasswordAndUpdateHash(password);
		if (oldHash == null ? queryPassword.getValue() != null : !oldHash.equals(queryPassword.getValue())) {
			getDatabase().update(user);
		}
		doLogin(user, rememberMe, null);
	}

	private void doLogin(User user, boolean rememberUser, String cookieValue) throws TooManyUsersException, UnknownUserException {
		this.notLoggedIn = false;
		this.setUserID(user.getUserID());
		if (rememberUser) {
			try {
				setRememberMeCookie(user, cookieValue);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
		showLoginDestination();
	}
	private static final String MINORTASK_MEMORY_KEY = "MinorTaskMemoryKey";

	public static String getRandomID() {
		return new BigInteger(130, new SecureRandom()).toString(32);
	}

	private void setRememberMeCookie(User user, String cookieValue) throws SQLException {
		RememberedLogin.cleanUpTable(getDatabase());
		String identifier = cookieValue;
		if (identifier == null) {
			identifier = getRandomID();
		}
		setCookie(MINORTASK_MEMORY_KEY, identifier);

		RememberedLogin example = new RememberedLogin();
		example.expires.permittedRange(new Date(), null);
		example.userid.permittedValues(user.getUserID());
		example.rememberCode.permittedValues(identifier);
		List<RememberedLogin> rows = getDatabase().get(example);
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(GregorianCalendar.SECOND, REMEMBER_ME_COOKIE_SECONDS_OFFSET);
		Date expiryDate = cal.getTime();
		if (rows.size() > 0) {
			rows.forEach((row) -> {
				row.expires.setValue(expiryDate);
			});
			getDatabase().update(rows);
		} else {
			RememberedLogin mem = new RememberedLogin(user.getUserID(), identifier, expiryDate);
			getDatabase().insert(mem);
		}
	}

	private void setCookie(String cookieName, String cookieValue) {
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(REMEMBER_ME_COOKIE_SECONDS_OFFSET);
		cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
		System.out.println("SET COOKIE: " + cookie.getName() + ":" + cookie.getValue());
		VaadinService.getCurrentResponse().addCookie(cookie);
	}
	private static final int REMEMBER_ME_COOKIE_SECONDS_OFFSET = 60 * 60 * 24 * 30;

	private void removeRememberMeCookieValue() {
		Optional<Cookie> existingCookie = getRememberMeCookieValue();
		if(existingCookie.isPresent()){
			String cookieValue = existingCookie.get().getValue();
			if (cookieValue!=null){
				RememberedLogin mem = new RememberedLogin();
				mem.userid.permittedValues(getUserID());
				mem.rememberCode.permittedValues(cookieValue);
				try {
					getDatabase().delete(mem);
				} catch (SQLException ex) {
					sqlerror(ex);
				}
			}
		}
		Cookie cookie = new Cookie(MINORTASK_MEMORY_KEY, "");
		cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
		cookie.setMaxAge(0);
		VaadinService.getCurrentResponse().addCookie(cookie);
	}

	private Optional<Cookie> getRememberMeCookieValue() {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		if (cookies != null) {
			return Arrays.stream(cookies).filter(c -> c.getName().equals(MINORTASK_MEMORY_KEY)).findFirst();
		} else {
			return Optional.empty();
		}
	}

	private User getRememberedUser(Optional<Cookie> rememberMeCookieValue) throws UnknownUserException, TooManyUsersException {
		if (rememberMeCookieValue.isPresent()) {
			String value = rememberMeCookieValue.get().getValue();
			if (!value.isEmpty()) {
				RememberedLogin example = new RememberedLogin();
				example.rememberCode.permittedValues(value);
				example.expires.permittedRange(new Date(), null);
				try {
					User onlyRow = getDatabase().getDBQuery(example, new User()).getOnlyInstanceOf(new User());
					return onlyRow;
				} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
					sqlerror(ex);
				} catch (UnexpectedNumberOfRowsException ex) {
					if (ex.getActualRows() == 0) {
						throw new UnknownUserException();
					} else {
						throw new TooManyUsersException();
					}
				}
			}
		}
		throw new UnknownUserException();
	}

	private boolean canLoginRememberedUser() {
		try {
			Optional<Cookie> rememberMeCookieValue = getRememberMeCookieValue();
			User user = getRememberedUser(rememberMeCookieValue);
			doLogin(user, true, rememberMeCookieValue.isPresent()?rememberMeCookieValue.get().getValue():null);
			return true;
		} catch (UnknownUserException | TooManyUsersException ex) {
			return false;
		}
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	public void logout() {
		removeRememberMeCookieValue();
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
		projectTreeTasks.forEach((projectTreeTaskNode) -> {
			enforceDateConstraintsOnTaskTree(task, projectTreeTaskNode);
		});
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
				Logger.getLogger(MinorTask.class
						.getName()).log(Level.SEVERE, null, ex);
			} catch (UnexpectedNumberOfRowsException ex) {
				throw new InaccessibleTaskException(taskID);
			}
		}
		return new Task.TaskAndProject(null, null);
	}

	public boolean isLoggedIn() {
		boolean loggedIn = this.userID > 0 && VaadinSession.getCurrent().getState().equals(VaadinSessionState.OPEN);
		if (!loggedIn) {
			return canLoginRememberedUser();
		}
		return loggedIn;
	}

	public String getApplicationName() {
		String name = "MinorTask";
		try {
			//"http://101.100.138.79/minortask"
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			name = (String) envCtx.lookup("MinorTaskApplicationName");
			if (name == null || name.isEmpty()) {
			}
		} catch (NamingException ex) {
			Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
		}
		return name;
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
				showOpeningPage();
			} else {
				System.out.println("NAVIGATE WITH PATH");
				UI.getCurrent().navigate(pathWithQueryParameters);
			}
		} else {
			showOpeningPage();
		}
	}

	public Task getSafeTaskExample(MinorTaskComponent component) {
		Task example = new Task();
		example.userID.permittedValues(component.getUserID());
		return example;
	}

	public MimeMessage getEmailMessageToSend() throws MessagingException {
		MimeMessage mimeMessage = new MimeMessage(setupEmailSession());
		mimeMessage.setFrom(
				new InternetAddress(
						getApplicationName()
						+ "minortask.alerts@gmail.com"
				)
		);
		return mimeMessage;
	}

	public void showLostPassword(String username) {
		UI.getCurrent().navigate(
				LostPasswordLayout.class,
				username
		);
	}

	public String getApplicationURL() {
		String url = "http://localhost:8080/minortask";
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			url = (String) envCtx.lookup("MinorTaskURL");
			if (url == null || url.isEmpty()) {
			}
		} catch (NamingException ex) {
			Logger.getLogger(MinorTask.class.getName()).log(Level.SEVERE, null, ex);
		}
		return url;
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
