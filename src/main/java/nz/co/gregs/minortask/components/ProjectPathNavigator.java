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
		List<Task> ancestors = getProjectPathTasks(getTaskID(), getCurrentUserID());
		Collections.reverse(ancestors);
		ancestors.stream()
				.forEachOrdered((ancestor) -> {
					add(getButtonForTaskID(ancestor));
				});
	}

	public void refresh() {
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
		IconWithToolTip search = new IconWithToolTip(VaadinIcon.SEARCH, "Search");
		search.addClickListener((event) -> {
			Globals.showSearchPage();
		});
		search.addClassName("navigator-task-search");

		IconWithToolTip today = new IconWithToolTip(VaadinIcon.TIMER, "Today's Tasks");
		today.addClickListener((event) -> {
			Globals.showTodaysTasks();
		});
		today.addClassName("navigator-task-today");

		IconWithToolTip recent = new IconWithToolTip(VaadinIcon.CLOCK, "Recently Viewed");
		recent.addClickListener((event) -> {
			Globals.showRecentsPage();
		});
		recent.addClassName("navigator-task-recents");

		IconWithToolTip favourites = new IconWithToolTip(VaadinIcon.HEART, "Favourited");
		favourites.addClickListener((event) -> {
			Globals.showFavouritesPage();
		});
		favourites.addClassName("navigator-task-favourites");

		return new Component[]{search, today, recent, favourites};
	}
}
