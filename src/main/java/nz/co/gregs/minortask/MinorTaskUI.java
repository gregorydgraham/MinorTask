package nz.co.gregs.minortask;

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
import nz.co.gregs.minortask.datamodel.*;
import nz.co.gregs.minortask.pages.*;

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

	public static DBDatabase database;

	public final LoginPage LOGIN = new LoginPage(this);
	public final LoggedoutPage LOGGEDOUT = new LoggedoutPage(this);
	public final SignupPage SIGNUP = new SignupPage(this);
	public final TasksPage TASKS = new TasksPage(this);
	public final TextField USERNAME_FIELD = new TextField("Your Name");
	public final Button LOGOUT_BUTTON = new Button("Log Out");
	public final PasswordField PASSWORD_FIELD = new PasswordField("Password");
	private VaadinSession sess;
	public boolean notLoggedIn = true;
	public String username = "";
	private long userID = 0;
	public MinorTaskPage currentPage = null;

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		setupSession(vaadinRequest);

		setupDatabase();

		LOGOUT_BUTTON.addClickListener((Button.ClickEvent event) -> {
			handleLogoutRequest();
		});

		if (notLoggedIn) {
			LOGIN.show();
		} else {
			TASKS.show();
		}
	}

	void handleLogoutRequest() {
		USERNAME_FIELD.clear();
		PASSWORD_FIELD.clear();
		notLoggedIn = true;
		LOGGEDOUT.show();

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
		user.userID.permittedValues(userID);
		try {
			User onlyRow = database.get(1l, user).get(0);
			username = onlyRow.username.getValue();
		} catch (SQLException ex) {
			currentPage.error("SQL ERROR", ex.getLocalizedMessage());
		} catch (UnexpectedNumberOfRowsException ex) {
			currentPage.error("MULTIPLE USER ERROR", "Oops! This should not have happened.\n Please contact MinorTask to get it fixed.");
		}

	}
}
