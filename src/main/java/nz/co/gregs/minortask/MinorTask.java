/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import nz.co.gregs.minortask.datamodel.*;
import nz.co.gregs.minortask.components.upload.Document;
import nz.co.gregs.minortask.components.images.SizedImageDocumentStreamFactory;
import nz.co.gregs.minortask.components.images.ThumbnailImageDocumentStreamFactory;
import nz.co.gregs.minortask.pages.ColleaguesPage;
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

	public long NOT_LOGGED_IN_USERID = 0l;
	private long userID = NOT_LOGGED_IN_USERID;
	private User loggedInUser = null;
	boolean notLoggedIn = true;

	static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MinorTask.class);

	private MinorTask() {
		super();
	}

	public Task getTask(Long taskID) throws InaccessibleTaskException {
		Task returnTask = null;
		if (taskID == null) {
			return returnTask;
		}
		final Task example = new Task();
		example.taskID.permittedValues(taskID);
		try {
			final DBQuery query = getDatabase()
					.getDBQuery(example)
					.addOptional(new Task.Assignee())
					.addOptional(new Task.Owner());
			// add user requirement
			query.addCondition(
					example.column(example.userID).is(getCurrentUserID())
							.or(
									example.column(example.assigneeID).is(getCurrentUserID())
							)
			);
			return query.getOnlyInstanceOf(example);
		} catch (UnexpectedNumberOfRowsException ex) {
			throw new InaccessibleTaskException(taskID);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		return returnTask;
	}

	public Task.WithSortColumns getTaskWithSortColumnsExampleForTaskID(Long taskID) {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(getCurrentUserID());
		example.projectID.permittedValues(taskID);
		return example;
	}

	public synchronized void loginAs(User user, String password, Boolean rememberMe) throws UnknownUserException, TooManyUsersException, SQLException, IncorrectPasswordException {
		DBPasswordHash queryPassword = user.queryPassword();
		String oldHash = queryPassword.getValue();
		if (queryPassword.checkPasswordAndUpdateHash(password)) {
			if (oldHash == null ? queryPassword.getValue() != null : !oldHash.equals(queryPassword.getValue())) {
				getDatabase().update(user);
			}
			doLogin(user, rememberMe, null);
		}
	}

	private void doLogin(User user, boolean rememberUser, String cookieValue) throws TooManyUsersException, UnknownUserException {
		this.notLoggedIn = false;
		this.userID = user.getUserID();
		this.loggedInUser = user;
		if (rememberUser) {
			try {
				setRememberMeCookie(user, cookieValue);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
	}

	private void removeRememberMeCookieValue() {
		Optional<Cookie> existingCookie = getRememberMeCookieValue();
		if (existingCookie.isPresent()) {
			String cookieValue = existingCookie.get().getValue();
			if (cookieValue != null) {
				RememberedLogin mem = new RememberedLogin();
				mem.userid.permittedValues(getCurrentUserID());
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

	private boolean loginAsRememberedUser() {
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
		return loggedInUser.getUsername();
	}

	public void logout() {
		removeRememberMeCookieValue();
		this.loggedInUser = null;
		this.userID = 0;
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
	public long getCurrentUserID() {
		if (isLoggedIn()) {
			return getCurrentUser().getUserID();
		} else {
			return NOT_LOGGED_IN_USERID;
		}
	}

	public User getCurrentUser() {
		if (isLoggedIn()) {
			setProfileImage(loggedInUser);
			return loggedInUser;
		}
		return null;
	}

	public User getUser(long userID) {
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			List<User> got = getDatabase().getDBQuery(example).addOptional(new Document()).getAllInstancesOf(example);
			if (got.size() != 1) {
				warning("User Issue", "There is an issue with user #" + userID + ", please contact MinorTask to correct it.");
			} else {
				User foundUser = got.get(0);
				return foundUser;
			}
		} catch (SQLException ex) {
			sqlerror(ex);
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
						chat("Couldn't find your profile picture sorry");
					}
				} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
					sqlerror(ex);
				}
			}
		}
	}

	public Task.TaskAndProject getTaskAndProject(Long taskID) throws InaccessibleTaskException {
		if (taskID != null) {
			final Task example = new Task();
			example.taskID.permittedValues(taskID);
			final Task.Project projectExample = new Task.Project();
			// avoid connecting the project to anything other than the task
			projectExample.ignoreAllForeignKeysExceptFKsTo(example);
			DBQuery dbQuery = getDatabase().getDBQuery(example)
					.addOptional(projectExample, new Task.Assignee(), new Task.Owner());
			dbQuery.addCondition(
					example.column(example.userID).is(getCurrentUserID())
							.or(example.column(example.assigneeID).is(getCurrentUserID()))
			);
			System.out.println("" + dbQuery.getSQLForQuery());
			try {
				List<DBQueryRow> allRows = dbQuery.getAllRows(1);
				final DBQueryRow onlyRow = allRows.get(0);
				final Task taskFound = onlyRow.get(example);
				final Task.Project projectFound = onlyRow.get(projectExample);
				System.out.println("TASK: " + taskFound);
				System.out.println("PROJECT: " + projectFound);
				return new Task.TaskAndProject(taskFound, projectFound);
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
		project.userID.setValue(getCurrentUserID());
		project.name.setValue("Projects");
		return project;
	}

	public void showProfile() {
		UI.getCurrent().navigate(UserProfilePage.class);
	}

	public void showColleagues() {
		UI.getCurrent().navigate(ColleaguesPage.class);
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
		// add user requirement
		query.addCondition(example.column(example.userID).is(getCurrentUserID())
				.or(example.column(example.assigneeID).is(getCurrentUserID())
				)
		);
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
		if (task != null) {
			Globals
					.getActiveSubtasks(task, getCurrentUser())
					.forEach((subtask) -> {
				completeTaskWithCongratulations(subtask);
			});
			task.completionDate.setValue(new Date());
			try {
				final DBDatabase database = Globals.getDatabase();
				DBActionList update = database.update(task);
				repeatTask(task);
			} catch (SQLException ex) {
				Globals.sqlerror(ex);
			}
			Globals.animatedNotice(new Icon(VaadinIcon.CHECK), "Done.");
			Long projectID = task.projectID.getValue();
			Task usersCompletedTasks = new Task();
			usersCompletedTasks.userID.setValue(getCurrentUserID());
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
		favouritedTasks.userID.permittedValues(getCurrentUserID());
		try {
			return getDatabase().get(favouritedTasks).size() > 0;
		} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
			sqlerror(ex);
		}
		return false;
	}

	public void reopenTask(Task task) {
		// a completed task can't be within a complete project so reopen all the tasks above it
		List<Task> projectPathTasks = getProjectPathTasks(task.taskID.getValue(), getCurrentUserID());
		projectPathTasks.forEach((projectPathTask) -> {
			setCompletionDateToNull(projectPathTask);
		});
		setCompletionDateToNull(task);
	}

	public void setCompletionDateToNull(Task projectPathTask) {
		projectPathTask.completionDate.setValue((Date) null);
		try {
			Globals.getDatabase().update(projectPathTask);
		} catch (SQLException ex) {
			Globals.sqlerror(ex);
		}
	}
}
