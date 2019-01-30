/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.minortask.Globals;
import static nz.co.gregs.minortask.Globals.getTaskExample;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.AuthorisedOptionalTaskPage;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/project-path-navigator.css")
public class ProjectPathNavigator extends Div implements MinorTaskComponent, RequiresLogin {

	private final Long taskID;
	private final Class<? extends AuthorisedOptionalTaskPage> targetPage;

	public ProjectPathNavigator() {
		this(null, null);
	}

	public ProjectPathNavigator(Class<? extends AuthorisedOptionalTaskPage> targetPage, Long taskID) {
		this.targetPage = targetPage;
		this.taskID = taskID;
		buildComponent();
		addClassName("project-path-navigator");
	}

	protected final void buildComponent() {
		removeAll();
		add(getPrefixComponents());
		List<Task> ancestors = getProjectPathTasks(getTaskID(), getUserID());
		Collections.reverse(ancestors);
		ancestors.stream()
				//.filter((ancestor) -> (ancestor != null))
				.forEachOrdered((ancestor) -> {
					add(getButtonForTaskID(ancestor));
				});
		final AddTaskButton addTaskButton;
		if (getTaskID() == null) {
			addTaskButton = new AddTaskButton("Add Project...");
		} else {
			addTaskButton = new AddTaskButton(getTaskID());
		}
		addTaskButton.addClassNames("small", "projectpath");
		addTaskButton.getElement().setAttribute("theme", "small");
		add(addTaskButton);
	}

	public void refresh() {
//		chat("refreshing path..."); 
		buildComponent();
	}

	public List<Task> getProjectPathTasks(Long taskID, final long userID) {
		try {
			final Task task = getTaskExample(taskID, userID);
			DBQuery query = getDatabase().getDBQuery(task);
			DBRecursiveQuery<Task> recurse = new DBRecursiveQuery<Task>(query, task.column(task.projectID));
			List<Task> ancestors = recurse.getAncestors();
			ancestors.add(null);
			return ancestors;
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		return new ArrayList<>();
	}

	public Button getButtonForTaskID(Task task) {
		final GoToTaskButton button = new GoToTaskButton(taskID, task, targetPage);
		return button;
	}

	/**
	 * @return the taskID
	 */
	public Long getTaskID() {
		return taskID;
	}

	private Component[] getPrefixComponents() {
		IconWithClickHandler search = new IconWithClickHandler(VaadinIcon.SEARCH);
		search.addClickListener((event) -> {
			Globals.showSearchPage();
		});
		search.addClassName("navigator-task-search");
		search.setTooltipText("Find that task the quick way");

		IconWithClickHandler today = new IconWithClickHandler(VaadinIcon.TIMER);
		today.addClickListener((event) -> {
			Globals.showTodaysTasks();
		});
		today.addClassName("navigator-task-today");
		search.setTooltipText("All of tasks you could work on today");

		IconWithClickHandler recent = new IconWithClickHandler(VaadinIcon.CLOCK);
		recent.addClickListener((event) -> {
			Globals.showRecentsPage();
		});
		recent.addClassName("navigator-task-recents");
		search.setTooltipText("See the tasks you've looked at recently");

		IconWithClickHandler favourites = new IconWithClickHandler(VaadinIcon.HEART);
		favourites.addClickListener((event) -> {
			Globals.showFavouritesPage();
		});
		favourites.addClassName("navigator-task-favourites");
		search.setTooltipText("Your favourite tasks collected together");

		return new Component[]{search, today, recent, favourites};
	}
}
