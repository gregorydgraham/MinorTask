package nz.co.gregs.minortask;

import nz.co.gregs.minortask.components.LoginComponent;
import nz.co.gregs.minortask.components.LoggedoutComponent;
import com.vaadin.annotations.PreserveOnRefresh;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.components.BannerMenu;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.SignupComponent;
import nz.co.gregs.minortask.components.TaskCreationComponent;
import nz.co.gregs.minortask.components.TaskEditorComponent;
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

	private VaadinSession sess;
	private boolean notLoggedIn = true;
	public String username = "";
	private long userID = 0;
	private Long currentTaskID;

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		setupSession(vaadinRequest);

		Helper.setupDatabase();

		if (notLoggedIn) {
			showLogin();
		} else {
			showTask(null);
		}
	}

	private void setupSession(VaadinRequest vaadinRequest) {
		sess = VaadinSession.getCurrent();
	}


	public boolean getNotLoggedIn() {
		return notLoggedIn;
	}

	public void loginAs(Long userID) {
		this.notLoggedIn = false;
		this.userID = userID;	
		showTask(null);
	}

	public TaskWithSortColumns getTaskExampleForTaskID(Long taskID) {
		TaskWithSortColumns example = new TaskWithSortColumns();
		example.userID.permittedValues(getUserID());
		example.projectID.permittedValues(taskID);
		return example;
	}

	public Long getCurrentTaskID() {
		return currentTaskID;
	}

	private void setCurrentTaskID(Long newTaskID) {
		currentTaskID = newTaskID;
	}

	public void showTask() {
		showTask(null);
	}

	public void showTask(Long taskID) {
//		TaskListComponent taskListComponent = new TaskListComponent(this, taskID);
		TaskEditorComponent taskComponent = new TaskEditorComponent(this, taskID);
		showAuthorisedContent(taskID, taskComponent);
	}

	public void showTaskCreation(Long taskID) {
		showAuthorisedContent(taskID, new TaskCreationComponent(this, taskID));

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
			this.setContent(display);
		}
	}

	public void showLogin() {
		showPublicContent(new LoginComponent(this));
	}

	public void showLogin(String username, String password) {
		showPublicContent(new LoginComponent(this, username, password));
	}

	public void logout() {
		this.setContent(new LoggedoutComponent(this));
		notLoggedIn = true;

		sess.close();
	}

	public void showSignUp(String username, String password) {
		showPublicContent(new SignupComponent(this, username, password));
	}

	public void showPublicContent(Component component) {
		this.setContent(component);
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
			User onlyRow = Helper.database.get(1l, user).get(0);
			username = onlyRow.getUsername();
		} catch (SQLException ex) {
			Helper.error("SQL ERROR", ex.getLocalizedMessage());
		} catch (UnexpectedNumberOfRowsException ex) {
			Helper.error("MULTIPLE USER ERROR", "Oops! This should not have happened.\n Please contact MinorTask to get it fixed.");
		}

	}
}
