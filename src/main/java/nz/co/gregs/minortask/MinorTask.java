/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.NavigationTrigger;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinSessionState;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.xml.bind.DatatypeConverter;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.actions.DBActionList;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.datatypes.DBPasswordHash;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.dbvolution.exceptions.IncorrectPasswordException;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import static nz.co.gregs.minortask.Globals.getDatabase;
import static nz.co.gregs.minortask.Globals.sqlerror;
import nz.co.gregs.minortask.components.EditTask;
import nz.co.gregs.minortask.datamodel.*;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.components.upload.Document;
import nz.co.gregs.minortask.components.images.SizedImageDocumentStreamFactory;
import nz.co.gregs.minortask.components.images.ThumbnailImageDocumentStreamFactory;
import nz.co.gregs.minortask.pages.UserProfilePage;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gregorygraham
 */
public class MinorTask extends Globals implements Serializable {

	public static MinorTask getMinorTask() {
		final VaadinSession session = VaadinSession.getCurrent();
		MinorTask minortask = session.getAttribute(MinorTask.class);
		if (minortask == null) {
			session.setAttribute(MinorTask.class, new MinorTask());
			minortask = session.getAttribute(MinorTask.class);
		}
		return minortask;
	}

	private long userID = 0;
	boolean notLoggedIn = true;
	public String username = "";
	private Location loginDestination = null;

	static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MinorTask.class);
	private User user;

	private MinorTask() {
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

	public boolean loginAsRememberedUser() {
		try {
			Optional<Cookie> rememberMeCookieValue = getRememberMeCookieValue();
			User rememberedUser = getRememberedUser(rememberMeCookieValue);
			doLogin(rememberedUser, true, rememberMeCookieValue.isPresent() ? rememberMeCookieValue.get().getValue() : null);
			return true;
		} catch (UnknownUserException | TooManyUsersException ex) {
			System.out.println("CANT LOGIN AS REMEMBERED USER: " + ex.getMessage());
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
				User example = new User();
				example.queryUserID().permittedValues(getUserID());
				try {
					List<User> got = getDatabase().getDBQuery(example).addOptional(new Document()).getAllInstancesOf(example);
					if (got.size() != 1) {
						warning("User Issue", "There is an issue with your account, please contact MinorTask to correct it.");
					} else {
						user = got.get(0);
						setProfileImage(user);
						return user;
					}
				} catch (SQLException ex) {
					sqlerror(ex);
				}
			} else {
				setProfileImage(user);
				return user;
			}
		}
		return null;
	}

	private void setProfileImage(User user) {
		if (user.profileImage == null) {
			if (user.getProfileImageID() != null) {
				try {
					List<Document> docs = getDatabase().getDBQuery(user, new Document()).getAllInstancesOf(new Document());
					if (docs.size() == 1) {
						user.profileImage = docs.get(0);
					} else {
						chat("Couldn't find the picture");
					}
				} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
					sqlerror(ex);
				}
			} else {
				chat("image not provided");
			}
		}
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

	public void setBackgroundToFullsizeImage(HasStyle aThis, Document profileImage) {
		if (profileImage != null
				&& profileImage.mediaType.getValue() != null
				&& profileImage.mediaType.getValue().startsWith("image/")) {
			String imageString
					= "data:" + profileImage.mediaType.getValue()
					+ ";base64,"
					+ DatatypeConverter.printBase64Binary(profileImage.documentContents.getBytes());
			aThis.getStyle().set("background-image", "url(" + imageString + ")");
			aThis.getStyle().set("background-size", "cover");
			aThis.getStyle().set("background-position", "center");
			aThis.getStyle().set("background-repeat", "no-repeat");
		}
	}

	public void setBackgroundToSmallImage(HasStyle aThis, Document profileImage) throws IOException {
		if (profileImage != null
				&& profileImage.mediaType.getValue() != null
				&& profileImage.mediaType.getValue().startsWith("image/")) {
			ThumbnailImageDocumentStreamFactory res = new ThumbnailImageDocumentStreamFactory(profileImage);
			DatatypeConverter.printBase64Binary(res.getByteArray());
			String imageString
					= "data:" + profileImage.mediaType.getValue()
					+ ";base64,"
					+ DatatypeConverter.printBase64Binary(res.getByteArray());//profileImage.documentContents.getBytes());
			aThis.getStyle().set("background-image", "url(" + imageString + ")");
			aThis.getStyle().set("background-size", "cover");
			aThis.getStyle().set("background-position", "center");
			aThis.getStyle().set("background-repeat", "no-repeat");
		}
	}

	public void setBackgroundToLargeImage(HasStyle aThis, Document profileImage) throws IOException {
		if (profileImage != null
				&& profileImage.mediaType.getValue() != null
				&& profileImage.mediaType.getValue().startsWith("image/")) {
			SizedImageDocumentStreamFactory res = new SizedImageDocumentStreamFactory(profileImage);
			DatatypeConverter.printBase64Binary(res.getByteArray());
			String imageString
					= "data:" + profileImage.mediaType.getValue()
					+ ";base64,"
					+ DatatypeConverter.printBase64Binary(profileImage.documentContents.getBytes());
			aThis.getStyle().set("background-image", "url(" + imageString + ")");
			aThis.getStyle().set("background-size", "cover");
			aThis.getStyle().set("background-position", "center");
			aThis.getStyle().set("background-repeat", "no-repeat");
		}
	}

	public List<Task> getTasksOfProject(Long projectID) throws AccidentalCartesianJoinException, SQLException, AccidentalBlankQueryException {
		Task example = new Task();
		example.taskID.permittedValues(projectID);
		final DBDatabase database = getDatabase();
		DBQuery query = database.getDBQuery(example);
		DBRecursiveQuery<Task> recurse = query.getDBRecursiveQuery(example.column(example.projectID), example);
		List<Task> descendants = recurse.getDescendants();
		return descendants;
	}

	public List<Task> getProjectsOfTask(Long taskId) throws AccidentalCartesianJoinException, SQLException, AccidentalBlankQueryException {
		Task example = new Task();
		example.taskID.permittedValues(taskId);
		final DBDatabase database = getDatabase();
		DBQuery query = database.getDBQuery(example);
		DBRecursiveQuery<Task> recurse = query.getDBRecursiveQuery(example.column(example.projectID), example);
		List<Task> ancestors = recurse.getAncestors();
		return ancestors;
	}

	public void completeTaskWithCongratulations(Task task) {
		try {
			if (task != null) {
				completeTask(task);
				Globals.animatedNotice(new Icon(VaadinIcon.CHECK), "Done.");
				Long projectID = task.projectID.getValue();
				Task usersCompletedTasks = new Task();
				usersCompletedTasks.userID.setValue(getUserID());
				usersCompletedTasks.completionDate.excludeNull();
				try {
					final Long completedTaskCount = getDatabase().getDBQuery(usersCompletedTasks).count();
					Task currentProject = new Task();
					currentProject.projectID.setValue(projectID);
					currentProject.completionDate.permitOnlyNull();
					if (getDatabase().getDBQuery(currentProject).count() == 0) {
						Globals.congratulate("All the subtasks are completed!");
					}
					if (completedTaskCount < 11) {
						Globals.congratulate(new Label("" + completedTaskCount + " TASKS"), "Completed");
					} else if (completedTaskCount > 11 && completedTaskCount < 51
							&& completedTaskCount % 10 == 0) {
						Globals.congratulate(new Label("" + completedTaskCount + " TASKS"), "Completed");
					} else if (completedTaskCount % 50 == 0) {
						Globals.congratulate(new Label("" + completedTaskCount + " TASKS"), "Completed");
					}
				} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
					sqlerror(ex);
				}
			}
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void completeTask(Task task) throws Globals.InaccessibleTaskException {
		if (task != null) {
			List<Task> subtasks = Globals.getActiveSubtasks(task, getUser());
			for (Task subtask : subtasks) {
				completeTask(subtask);
			}
			task.completionDate.setValue(new Date());
			try {
				final DBDatabase database = Globals.getDatabase();
				DBActionList update = database.update(task);
				repeatTask(task);
			} catch (SQLException ex) {
				Globals.sqlerror(ex);
			}
		}
	}

	public void repeatTask(Task task) {
		if (task.repeatOffset.isNotNull()) {
			Period value = task.repeatOffset.getValue();
			final Date now = new Date();
			final Date startDateValue = task.startDate.getValue();
			if (startDateValue != null
					&& (startDateValue.before(now))) {
				Period period = new Period(now.getTime() - startDateValue.getTime(), (Chronology) null);
				value = value.plus(period);
			}
			Task copy = DBRow.copyDBRow(task);
			copy.taskID.setValueToNull();
			copy.completionDate.setValueToNull();
			copy.startDate.setValue(offsetDate(copy.startDate.getValue(), value));
			copy.preferredDate.setValue(offsetDate(copy.preferredDate.getValue(), value));
			copy.finalDate.setValue(offsetDate(copy.finalDate.getValue(), value));
			try {
				getDatabase().insert(copy);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
	}

	public Date offsetDate(final Date originalDate, Period value) {
		if (originalDate != null) {
			Date newDate = new DateTime(originalDate.getTime()).plus(value).toDate();
			return newDate;
		} else {
			return null;
		}
	}

	public boolean taskIsFavourited(Task task) {
		FavouritedTasks favouritedTasks = new FavouritedTasks();
		favouritedTasks.taskID.permittedValues(task.taskID);
		favouritedTasks.userID.permittedValues(getUserID());
		try {
			return getDatabase().get(favouritedTasks).size()>0;
		} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
			sqlerror(ex);
		}
		return false;
	}
}
