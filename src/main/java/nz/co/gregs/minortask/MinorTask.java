/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.NavigationTrigger;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.datatypes.DBPasswordHash;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.dbvolution.exceptions.IncorrectPasswordException;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.datamodel.*;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.pages.UserProfilePage;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gregorygraham
 */
public class MinorTask extends Globals implements Serializable {

	private long userID = 0;
	boolean notLoggedIn = true;
	public String username = "";
	private Location loginDestination = null;

	static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MinorTask.class);
	private User user;

	public MinorTask() {
		super();
	}

	public Task getTask(Long taskID) throws InaccessibleTaskException {
		return getTask(taskID, getUserID());
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
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			User onlyRow = getDatabase().get(1L, example).get(0);
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
		this.user = user;
		if (rememberUser) {
			try {
				setRememberMeCookie(user, cookieValue);
			} catch (SQLException ex) {
				ex.printStackTrace();
				sqlerror(ex);
			}
		}
//		showLoginDestination();
	}

	private void removeRememberMeCookieValue() {
		Optional<Cookie> existingCookie = getRememberMeCookieValue();
		if (existingCookie.isPresent()) {
			String cookieValue = existingCookie.get().getValue();
			if (cookieValue != null) {
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

//	private void removeLastUsernameCookieValue() {
//		Cookie cookie = new Cookie(MINORTASK_LASTUSERNAME_COOKIE_KEY, "");
//		cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
//		cookie.setMaxAge(0);
//		VaadinService.getCurrentResponse().addCookie(cookie);
//	}
	public boolean loginAsRememberedUser() {
		try {
			Optional<Cookie> rememberMeCookieValue = getRememberMeCookieValue();
			User rememberedUser = getRememberedUser(rememberMeCookieValue);
			doLogin(rememberedUser, true, rememberMeCookieValue.isPresent() ? rememberMeCookieValue.get().getValue() : null);
			return true;
		} catch (UnknownUserException | TooManyUsersException ex) {
			System.out.println("CANT LOGIN AS REMEMBERED USER: "+ex.getMessage());
			System.out.println("RETURN: false");
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
		this.clearLoginDestination();
		this.userID = 0;
		this.username = null;
		notLoggedIn = true;
		UI current = UI.getCurrent();
		current.getRouter().navigate(
				current,
				getCurrentLocation(),
				NavigationTrigger.PROGRAMMATIC);
	}

	public boolean isLoggedIn() {
		boolean loggedIn = this.userID > 0 && VaadinSession.getCurrent().getState().equals(VaadinSessionState.OPEN);
		if (!loggedIn) {
			return loginAsRememberedUser();
		} else {
			return loggedIn;
		}
	}

	/**
	 * @return the userID
	 */
	public long getUserID() {
		return userID;
	}

	public User getUser() {
		if (isLoggedIn()) {
			if (user == null) {
				User user = new User();
				user.queryUserID().permittedValues(getUserID());
				try {
					List<User> got = getDatabase().get(user);
					if (got.size() != 1) {
						warning("User Issue", "There is an issue with your account, please contact MinorTask to correct it.");
					} else {
						return got.get(0);
					}
				} catch (SQLException ex) {
					sqlerror(ex);
				}
			} else {
				return user;
			}
		}
		return null;
	}

	public void clearLoginDestination() {
		this.loginDestination = null;
	}

	public void setLoginDestination(Location location) {
		this.loginDestination = location;
	}

	public void setLoginDestination(Class<? extends Component> aClass) {
		setLoginDestination(getLocation(aClass));
	}

	public Task.TaskAndProject getTaskAndProject(Long taskID) throws InaccessibleTaskException {
		if (taskID != null) {
			final Task example = new Task();
			example.taskID.permittedValues(taskID);
			example.userID.permittedValues(getUserID());
			final Task.Project projectExample = new Task.Project();
			DBQuery dbQuery = getDatabase().getDBQuery(example).addOptional(projectExample);
			try {
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

	public Task.Project getNullProject() {
		Task.Project project = new Task.Project();
		project.taskID.setValue(-1);
		project.userID.setValue(getUserID());
		project.name.setValue("Projects");
		return project;
	}

	private void showLoginDestination() {
		Location dest = getLoginDestination();
		if (dest != null) {
			String pathWithQueryParameters = dest.getPathWithQueryParameters();
			if (pathWithQueryParameters.isEmpty()) {
				showOpeningPage();
			} else {
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

	/**
	 * @return the loginDestination
	 */
	public Location getLoginDestination() {
		return loginDestination;
	}

	public void showProfile() {
		UI.getCurrent().navigate(UserProfilePage.class);
	}
}
