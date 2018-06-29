package nz.co.gregs.minortask;

import nz.co.gregs.minortask.components.TasksComponent;
import nz.co.gregs.minortask.components.LoginComponent;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.components.LoggedoutComponent;
import com.vaadin.annotations.PreserveOnRefresh;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.databases.*;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.components.TaskListComponent;
import nz.co.gregs.minortask.datamodel.TaskWithSortColumns;
import nz.co.gregs.minortask.datamodel.*;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("minortasktheme")
@PreserveOnRefresh
public class MinorTaskUI extends UI {

	private static DBDatabase database;

//	public final LoginComponent LOGIN = new LoginComponent(this);
//	public final LoggedoutComponent LOGGEDOUT = new LoggedoutComponent(this);
//	public final SignupPage SIGNUP = new SignupPage(this);
//	public final TasksComponent TASKS = new TasksComponent(this);
	public final TextField USERNAME_FIELD = new TextField("Your Name");
	public final Button LOGOUT_BUTTON = new Button("Log Out");
	public final PasswordField PASSWORD_FIELD = new PasswordField("Password");
	private VaadinSession sess;
	private boolean notLoggedIn = true;
	public String username = "";
	private long userID = 0;
	public MinorTaskComponent currentPage = null;

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		setupSession(vaadinRequest);

		setupDatabase();

		LOGOUT_BUTTON.addClickListener((Button.ClickEvent event) -> {
			handleLogoutRequest();
		});

		if (notLoggedIn) {
			new LoginComponent(this).show();
		} else {
			new TasksComponent(this).show();
		}
	}

	void handleLogoutRequest() {
		USERNAME_FIELD.clear();
		PASSWORD_FIELD.clear();
		notLoggedIn = true;
		new LoggedoutComponent(this).show();

		sess.close();
	}

	public synchronized void setupDatabase() {
		if (database == null) {
			final String basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
			final File sqliteFile = new File(basePath + "/WEB-INF/MinorTask.sqlite");

			try {
				database
						= new DBDatabaseCluster(
								new SQLiteDB(sqliteFile, "admin", "admin"),
								new H2MemoryDB("MinorTask.h2", "admin", "admin", true));
				database.setPrintSQLBeforeExecuting(true);
			} catch (IOException | SQLException ex) {
				Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, ex);
				new Notification("NO DATABASE: " + ex.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			}
		}
		try {
			new Notification("Currently serving "
					+ database.getDBTable(new User()).setBlankQueryAllowed(true).count() + " users and "
					+ database.getDBTable(new Task()).setBlankQueryAllowed(true).count() + " tasks", Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
		} catch (SQLException ex) {
			Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, ex);
			new Notification("NO DATABASE CONNECTION: " + ex.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
		}
	}

	private void setupSession(VaadinRequest vaadinRequest) {
		sess = VaadinSession.getCurrent();
		if (!USERNAME_FIELD.isEmpty()) {
			username = USERNAME_FIELD.getValue();
		}
	}

	public DBDatabase getDatabase() {
		if (database==null){
			setupDatabase();
		}
		return database;
	}

	public boolean getNotLoggedIn() {
		return notLoggedIn;
	}

	public void loginAs(Long userID) {
		this.notLoggedIn=false;
		this.userID = userID;
	}

	public TaskWithSortColumns getTaskExampleForTaskID(Long taskID) {
		TaskWithSortColumns example = new TaskWithSortColumns();
		example.userID.permittedValues(getUserID());
		example.projectID.permittedValues(taskID);
		return example;
	}

	@WebServlet(urlPatterns = "/*", name = "MinorTaskUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MinorTaskUI.class, productionMode = false)
	public static class MinorTaskUIServlet extends VaadinServlet {
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the userID
	 */
	public long getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(long userID) {
		this.userID = userID;
		User user = new User();
		user.queryUserID().permittedValues(userID);
		try {
			User onlyRow = database.get(1l, user).get(0);
			username = onlyRow.getUsername();
		} catch (SQLException ex) {
			currentPage.error("SQL ERROR", ex.getLocalizedMessage());
		} catch (UnexpectedNumberOfRowsException ex) {
			currentPage.error("MULTIPLE USER ERROR", "Oops! This should not have happened.\n Please contact MinorTask to get it fixed.");
		}

	}
}
