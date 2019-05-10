/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
//import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.dbvolution.actions.DBActionList;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.databases.DBDatabaseCluster;
import nz.co.gregs.dbvolution.databases.DatabaseConnectionSettings;
import nz.co.gregs.dbvolution.databases.SQLiteDB;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.dbvolution.exceptions.NoAvailableDatabaseException;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.dbvolution.query.TreeNode;
import nz.co.gregs.dbvolution.utility.RegularProcess;
import nz.co.gregs.minortask.datamodel.RememberedLogin;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.components.upload.Document;
import nz.co.gregs.minortask.components.colleagues.Colleagues;
import nz.co.gregs.minortask.pages.MinorTaskLayout;
import nz.co.gregs.minortask.pages.LoginPage;
import nz.co.gregs.minortask.pages.LostPasswordLayout;
import nz.co.gregs.minortask.pages.SignUpLayout;

/**
 *
 * @author gregorygraham
 */
public class Globals {

	protected static final int REMEMBER_ME_COOKIE_SECONDS_OFFSET = 60 * 60 * 24 * 30;
	protected static final String MINORTASK_LASTUSERNAME_COOKIE_KEY = "MinorTaskLastUser";
	protected static final String MINORTASK_MEMORY_KEY = "MinorTaskMemoryKey";
	protected static final String MINORTASK_DATABASE_ATTRIBUTE_NAME = "minortask_database";
	public static final String EMAIL_CONFIG_CONTEXT_VAR = "MinorTaskEmailConfigFilename";
	private static DBDatabase database = null;
	private static boolean databaseSetup = false;
	private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(3);

	public static Date asDate(LocalDate localDate) {
		return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
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

	public static String getApplicationURL() {
		String url = "http://localhost:8080/minortask";
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			url = (String) envCtx.lookup("MinorTaskURL");
			if (url == null || url.isEmpty()) {
			}
		} catch (NamingException ex) {
			Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
		}
		return url;
	}

	public static void showLostPassword(String username) {
		UI.getCurrent().navigate(LostPasswordLayout.class, username);
	}

	public static MimeMessage getEmailMessageToSend() throws MessagingException {
		Session session = setupEmailSession();
		if (session != null) {
			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setFrom(new InternetAddress(getApplicationName() + "minortask.alerts@gmail.com"));
			return mimeMessage;
		} else {
			return null;
		}
	}

	public static Location getCurrentLocation() {
		RouteData route = UI.getCurrent().getRouter().getRoutes().get(0);
		String url = route.getUrl();
		Location location = new Location(url);
		return location;
	}

	public static String getApplicationName() {
		String name = "MinorTask";
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			String found = (String) envCtx.lookup("MinorTaskApplicationName");
			if (found == null || found.isEmpty()) {
			} else {
				name = found;
			}
		} catch (NamingException ex) {
			Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
		}
		return name;
	}

	protected static void enforceDateConstraintsOnTaskTree(Task task, TreeNode<Task> node) {
		Task subtask = node.getData();
		final Date taskStart = task.startDate.getValue();
		final Date taskDeadline = task.finalDate.getValue();
		if (taskDeadline != null && subtask.finalDate != null && taskDeadline.before(subtask.finalDate.getValue())) {
			subtask.finalDate.setValue(taskDeadline);
			try {
				getDatabase().update(subtask);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
		final Date subtaskStart = subtask.startDate.getValue();
		if (subtaskStart != null && taskStart != null && subtaskStart.before(taskStart)) {
			task.startDate.setValue(subtaskStart);
			try {
				getDatabase().update(task);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
	}

	public static void enforceDateConstraintsOnTaskTree(Task task) {
		List<TreeNode<Task>> projectTreeTasks = getProjectTreeTasks(task.taskID.getValue(), task.userID.getValue());
		projectTreeTasks.forEach((TreeNode<Task> projectTreeTaskNode) -> {
			enforceDateConstraintsOnTaskTree(task, projectTreeTaskNode);
		});
	}

	public static void showSignUp(String username, String password) {
		UI.getCurrent().navigate(SignUpLayout.class, username);
	}

	protected static synchronized User getRememberedUser(Optional<Cookie> rememberMeCookieValue) throws UnknownUserException, TooManyUsersException {
		if (rememberMeCookieValue.isPresent()) {
			String value = rememberMeCookieValue.get().getValue();
			if (!value.isEmpty()) {
				System.out.println("REMEMBERED COOKIE VALUE: " + value);
				RememberedLogin example = new RememberedLogin();
				example.rememberCode.permittedValues(value);
				example.expires.permittedRange(new Date(), null);
				try {
					final DBDatabase db = getDatabase();
					//db.setPrintSQLBeforeExecuting(true);
					User onlyRow = db.getDBQuery(example, new User()).addOptional(new Document()).getOnlyInstanceOf(new User());
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
			} else {
				System.out.println("REMEMBERED COOKIE VALUE IS EMPTY.");
			}
		} else {
			System.out.println("NO REMEMBERED COOKIE FOUND.");
		}
		throw new UnknownUserException();
	}

	protected static Optional<Cookie> getRememberMeCookieValue() {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		if (cookies != null) {
			return Arrays.stream(cookies).filter((Cookie c) -> c.getName().equals(MINORTASK_MEMORY_KEY)).findFirst();
		} else {
			return Optional.empty();
		}
	}

	public static Optional<Cookie> getLastUsernameCookieValue() {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		if (cookies != null) {
			return Arrays.stream(cookies).filter((Cookie c) -> c.getName().equals(MINORTASK_LASTUSERNAME_COOKIE_KEY)).findFirst();
		} else {
			return Optional.empty();
		}
	}

	protected static void setCookie(String cookieName, String cookieValue, int secondsOffset) {
		Cookie cookie = new Cookie(cookieName, cookieValue);
		if (secondsOffset > 0) {
			cookie.setMaxAge(secondsOffset);
		}

		cookie.setHttpOnly(true);
		cookie.setDomain(getApplicationURL().replaceAll("http[s]*://", "").replaceAll(":[0-9]*/*.*", ""));
		System.out.println("SET COOKIE: " + cookie.getName() + ":" + cookie.getValue() + " - " + cookie.getDomain());
		VaadinService.getCurrentResponse().addCookie(cookie);
	}

	protected static void setRememberMeCookie(User user, String cookieValue) throws SQLException {
		setCookie(MINORTASK_LASTUSERNAME_COOKIE_KEY, user.getUsername(), REMEMBER_ME_COOKIE_SECONDS_OFFSET);
		Optional<Cookie> cookie = getRememberMeCookieValue();
		if (cookie.isPresent()) {
		} else {
			String identifier = cookieValue;
			if (identifier == null) {
				identifier = getRandomID();
			}
			setCookie(MINORTASK_MEMORY_KEY, identifier, REMEMBER_ME_COOKIE_SECONDS_OFFSET);
			RememberedLogin example = new RememberedLogin();
			example.expires.permittedRange(new Date(), null);
			example.userid.permittedValues(user.getUserID());
			example.rememberCode.permittedValues(identifier);
			List<RememberedLogin> rows = getDatabase().get(example);
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(GregorianCalendar.SECOND, REMEMBER_ME_COOKIE_SECONDS_OFFSET);
			Date expiryDate = cal.getTime();
			if (rows.size() > 0) {
				rows.forEach((RememberedLogin row) -> {
					row.expires.setValue(expiryDate);
				});
				getDatabase().update(rows);
			} else {
				RememberedLogin mem = new RememberedLogin(user.getUserID(), identifier, expiryDate);
				getDatabase().insert(mem);
			}
		}
	}

	public static String getRandomID() {
		return new BigInteger(130, new SecureRandom()).toString(32);
	}

	public static void showLocation(Location dest) {
		if (dest != null) {
			System.out.println("nz.co.gregs.minortask.Globals.showLocation(): " + dest);
			String pathWithQueryParameters = dest.getPathWithQueryParameters();
			if (pathWithQueryParameters.isEmpty()) {
				showOpeningPage();
			} else {
				UI.getCurrent().navigate(pathWithQueryParameters);
			}
		}
	}

	public static void showLogin() {
		UI.getCurrent().navigate(LoginPage.class);
	}

	public static void showLogin(String username, String password) {
		UI.getCurrent().navigate(LoginPage.class, username);
	}

	public static void showOpeningPage() {
		UI.getCurrent().navigate(MinorTaskLayout.class);
	}

	public static Task getProjectExample(Long taskID, long userID) {
		Task task;
		task = new Task();
		task.projectID.permittedValues(taskID);
		task.userID.permittedValues(userID);
		return task;
	}

	public static List<Task> getProjectPathTasks(Long taskID, final long userID) {
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

	public static List<TreeNode<Task>> getProjectTreeTasks(Long taskID, final long userID) {
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

	public static synchronized DBDatabase getDatabase() {
		if (databaseSetup == false) {
			setDatabase(setupDatabase());
		}
		return database;
	}

	public static final void chat(String string) {
		Notification note = new Notification(new Label(string));
		note.setDuration(3000);
		note.setPosition(Notification.Position.BOTTOM_END);
		note.open();
	}

	public static final void notice(String string) {
		Icon image = new Icon(VaadinIcon.ALARM);
		animatedNotice(image, string);
	}

	public static final void animatedNotice(Component image, String string) {
		if (image instanceof HasStyle) {
			((HasStyle) image).addClassName("celebration-spin");
		}
		final Label label = new Label(string);
		label.add(image);
		label.addClassName("notice-label");

		Notification note = new Notification(label);
		note.setDuration(3000);
		note.setPosition(Notification.Position.TOP_CENTER);
		note.open();
	}

	public static final void congratulate(Component bling, String string) {
		animatedNotice(bling, string);
	}

	public static final void congratulate(String string) {
		Image image = new Image("images/star-small.png", "STAR");
		congratulate(image, string);
	}

	public static final void savedNotice() {
		Globals.animatedNotice(new Icon(VaadinIcon.SAFE), "Saved.");
	}

	public static void chatAboutUsers() {
		try {
			String message = "Currently serving " + getDatabase().getDBTable(new User()).setBlankQueryAllowed(true).count() + " users " + "and " + getDatabase().getDBTable(new Task()).setBlankQueryAllowed(true).count() + " tasks";
			chat(message);
		} catch (SQLException ex) {
			Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	protected static DBDatabaseCluster getEmergencyDatabase() throws IOException, SQLException {
		warning("No Database Configured", "No database configuration was found, " + getApplicationName() + " is now running on the temporary database.");
		return new DBDatabaseCluster(getApplicationName(), new SQLiteDB(new File(getApplicationName() + "-default.sqlite"), "admin", "admin"));
	}

	private static void setDatabase(DBDatabase db) {
		setSessionAttribute(MINORTASK_DATABASE_ATTRIBUTE_NAME, db);
	}

	protected static void setSessionAttribute(String sessionAttributeName, DBDatabase obj) {
		VaadinSession sess = VaadinSession.getCurrent();
		Lock lockInstance = sess.getLockInstance();
		try {
			lockInstance.lock();
			sess.setAttribute(sessionAttributeName, obj);
		} finally {
			lockInstance.unlock();
		}
	}

	public static final synchronized Session setupEmailSession() {
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
				Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
				final String error = "Unable to read email " + configFilename;
				System.err.println("" + error);
			} catch (NamingException ex) {
				error("Configuration Missing", "No such value \"" + configFilename + "\" in " + (new File(configFilename).getAbsolutePath()));
				Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
				final String error = "Unable to find email " + configFilename;
				System.err.println("" + error);
			} catch (EmailSessionFromConfigFile.NoEmailConfigurationFound ex) {
				error("Configuration Missing", "We were unable to find the email configuration \"" + configFilename + "\" in " + (new File(configFilename).getAbsolutePath()));
				Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
				final String error = "Unable to find email " + configFilename;
				System.err.println("" + error);
			}
		}
		return emailSession;
	}

	public static List<Task> getActiveSubtasks(Long taskID, final Long userID) {
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

	public static List<Task> getActiveSubtasks(Task task, final User user) {
		ArrayList<Task> arrayList = new ArrayList<Task>();
		final Task example = getProjectExample(task.taskID.getValue(), user.getUserID());
		example.completionDate.permittedValues((Date) null);
		try {
			List<Task> allRows = getDatabase().getDBTable(example).getAllRows();
			return allRows;
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		return arrayList;
	}

	public static Long getActiveSubtaskCount(Task task, final User user) {
		ArrayList<Task> arrayList = new ArrayList<Task>();
		final Task example = getProjectExample(task.taskID.getValue(), user.getUserID());
		example.completionDate.permittedValues((Date) null);
		try {
			Long allRows = getDatabase().getDBTable(example).count();
			return allRows;
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		return 0l;
	}

	private static synchronized DBDatabase setupDatabase() {
		if (Globals.databaseSetup == false) {
			try {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				DBDatabaseCluster cluster = (DBDatabaseCluster) envCtx.lookup("DBDatabaseCluster");
				System.out.println("CLUSTER: " + cluster);
				DBDatabase readyDatabase = null;
				try {
					readyDatabase = cluster.getReadyDatabase();
				} catch (NoAvailableDatabaseException ex) {
				}
				if (readyDatabase == null) {
					String dcsFactory = "bean/DatabaseConnectionSettings";
					int index = 0;
					String thisFactory = dcsFactory;
					DatabaseConnectionSettings settings;
					try {
						do {
							settings = (DatabaseConnectionSettings) envCtx.lookup(thisFactory);
							if (settings.getLabel().isEmpty()) {
								settings.setLabel(thisFactory);
							}
							System.out.println(thisFactory + ": " + settings);
							try {
								final DBDatabase newDB = settings.createDBDatabase();
								cluster.addDatabase(newDB);
							} catch (SQLException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
								quietError("Unable to create database: " + settings.toString(), ex);
							}
							index++;
							thisFactory = dcsFactory + index;
						} while (settings != null);
					} catch (NullPointerException | NameNotFoundException ex) {
						debug("Stopped at index " + index);
					} catch (NamingException ex) {
						System.out.println("" + ex.getClass().getSimpleName() + ": " + ex.getMessage());
//						ex.printStackTrace();
						debug("Stopped at index " + index);
					}
					debug(cluster.getClusterStatus());
					if (cluster.getReadyDatabase() != null) {
						cluster.addRegularProcess(new DatabaseBackupProcess());
						cluster.addRegularProcess(new CleanupDatabaseProcess());
						Globals.database = cluster;
						databaseSetup = true;
						debug("Database created from context based configuration");
					} else {
						debug("Configuration failed to create database");
					}
				} else {
					System.out.println("Database already configured");
				}
			} catch (NoAvailableDatabaseException | NullPointerException ex) {
				warning("No Database", "Unavailable to access the database");
			} catch (NamingException ex) {
				error("Naming Error", ex.getExplanation());
				Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			System.out.println("Using existing database");
		}
		if (Globals.database == null) {
			try {
				Globals.database = getEmergencyDatabase();
				warning("Emergency Database", "We were unable to find the database and are now running on an empty database");
			} catch (IOException | SQLException ex1) {
				error("No Database", "We were unable to find the database nor create an empty database, everything is cack.");
				Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex1);
				System.err.println("" + ex1.getLocalizedMessage());
				sqlerror(ex1);
			}
		}
		return Globals.database;
	}

	public static final void debug(String string) {
		System.out.println("DEBUG: " + string);
	}

	public static final void error(final String topic, final String error) {
		System.out.println("ERROR: " + topic + " - " + error);
		Button closeButton = new Button("Oops");
		VerticalLayout layout = new VerticalLayout(new Label(topic), new Label(error), closeButton);
		Notification note = new Notification(layout);
		closeButton.addClickListener((ClickEvent<Button> event) -> {
			note.close();
		});
		note.setPosition(Notification.Position.TOP_CENTER);
		UI.getCurrent().access(() -> note.open());
	}

	public static final void error(final String topic, final Exception error) {
		quietError(topic, error);
		Button closeButton = new Button("Oops");
		VerticalLayout layout = new VerticalLayout(new Label(topic), new Label(error.getMessage()), closeButton);
		Notification note = new Notification(layout);
		closeButton.addClickListener((ClickEvent<Button> event) -> {
			note.close();
		});
		note.setPosition(Notification.Position.TOP_CENTER);
		note.open();
	}

	public static final void quietError(final String topic, final Exception error) {
		System.out.println("ERROR: " + topic + " - " + error.getClass().getSimpleName() + ": " + error.getMessage());
		System.err.println("ERROR: " + topic + " - " + error.getClass().getSimpleName() + ": " + error.getMessage());
		error.printStackTrace();
	}

	public static String shorten(String value, int i) {
		return value == null ? null : value.substring(0, value.length() < i ? value.length() : i);
	}

	public static final void sqlerror(Exception exp) {
		Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, exp);
		final String localizedMessage = exp.getMessage();
		System.err.println("" + localizedMessage);
		Button closeButton = new Button("Darn it!");
		Notification note = new Notification(new Label("SQL ERROR"), new Label(localizedMessage), closeButton);
		closeButton.addClickListener((ClickEvent<Button> event) -> {
			note.close();
		});
		note.getElement().setAttribute("theme", "error");
		note.setPosition(Notification.Position.TOP_CENTER);
		note.open();
	}

	public static final void warning(final String topic, final String warning) {
		System.out.println("WARNING: " + topic + " - " + warning);
		Button closeButton = new Button("Nevermind");
		VerticalLayout layout = new VerticalLayout(new Label(topic), new Label(warning), closeButton);
		Notification note = new Notification(layout);
		closeButton.addClickListener((ClickEvent<Button> event) -> {
			note.close();
		});
		note.setPosition(Notification.Position.MIDDLE);
		note.setDuration(5000);
		note.open();
	}

	public static Component getSpacer() {
		Label spacer = new Label("");
		spacer.setHeight("1em");
		spacer.addClassName("minortask-spacer");
		spacer.getStyle().set("display", "block");
		return spacer;
	}

	public static String getURL(Class<? extends Component> aClass) {
		return UI.getCurrent().getRouter().getUrl(aClass);
	}

	public static Location getLocation(Class<? extends Component> aClass) {
		return new Location(getURL(aClass));
	}

//	public static void showRecentsPage() {
//		showPage(RecentTasksPage.class);
//	}
//
//	public static void showFavouritesPage() {
//		showPage(FavouriteTasksPage.class);
//	}
//	public static void showTask(Task task) {
//		if (task == null) {
//			showProjects();
//		} else {
//			showTask(task.taskID.getValue());
//		}
//	}
	protected static ScheduledThreadPoolExecutor getExecutor() {
		return EXECUTOR;
	}

	public static Location getOpeningLocation() {
		return new Location("today");
	}

	public static class InaccessibleTaskException extends Exception {

		public InaccessibleTaskException(Long taskID) {
			super();
		}
	}

	public static class TooManyUsersException extends Exception {

		public TooManyUsersException() {
			super("More Than One User Was Found While Expecting Only One");
		}
	}

	public static class UnknownUserException extends Exception {

		public UnknownUserException() {
			super("No Such User Found");
		}
	}

	public Globals() {
		setDatabase(setupDatabase());
	}

	private static class DatabaseBackupProcess extends RegularProcess {

		public DatabaseBackupProcess() {
			this.setTimeOffset(GregorianCalendar.MINUTE, 30);
		}

		@Override
		public synchronized String process() {
			String result = "PREPARING BACKUP\n";
			System.out.println("PREPARING TO BACKUP...\n");

			try {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				String dcsFactory = "bean/BackupDatabase";
				DatabaseConnectionSettings settings = (DatabaseConnectionSettings) envCtx.lookup(dcsFactory);
				final DBDatabase backupDB = settings.createDBDatabase();
				getDatabase().backupToDBDatabase(backupDB);
				result += "BACKED UP TO " + backupDB + "\n";
				backupDB.stop();
			} catch (SQLException | NamingException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				System.out.println("nz.co.gregs.minortask.Globals.DatabaseBackupProcess.process(): " + ex.getMessage());
				Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
				result += "EXCEPTION: " + ex.getMessage();
			}

			System.out.println("FINISHED BACKUP.");
			result += "FINISHED BACKUP\n";
			return result;
		}
	}

	private static class CleanupDatabaseProcess extends RegularProcess {

		@Override
		public String process() {
			return cleanupRememberedLogins();
		}

		private String cleanupRememberedLogins() {
			String str = "CLEANING UP THE REMEMBERED LOGINS...";
			System.out.println("CLEANING UP THE REMEMBERED LOGINS...\n");
			try {
				final DBActionList cleanUpActions = RememberedLogin.cleanUpTable(this.getDatabase());
				str += cleanUpActions.getSQL(database);
			} catch (SQLException ex) {
				Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
			}

			System.out.println("CLEANED UP THE REMEMBERED LOGINS");
			str += "\nCLEANED UP THE REMEMBERED LOGINS";
			return str;
		}

		private String removeOldColleagues() {
			String str = "REMOVING COLLEAGUES...";
			System.out.println(str + "\n");
			try {
				Colleagues example = new Colleagues();
				getDatabase().print(getDatabase().getDBTable(example).setBlankQueryAllowed(databaseSetup).getAllRows());
				final DBActionList cleanUpActions = getDatabase().deleteAll(example);
				str += cleanUpActions.getSQL(database);
			} catch (SQLException ex) {
				Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
			}

			System.out.println("REMOVED COLLEAGUES");
			str += "\nREMOVED COLLEAGUES";
			return str;
		}
	}

}
