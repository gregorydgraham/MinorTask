/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ClickEvent;
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
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.AuthorisedOptionalTaskPage;
import nz.co.gregs.minortask.pages.TaskEditorLayout;

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

	protected void buildComponent() {
		add(getPrefixComponents());
		List<Task> ancestors = getProjectPathTasks(getTaskID(), getUserID());
		Collections.reverse(ancestors);
		ancestors.stream()
				//.filter((ancestor) -> (ancestor != null))
				.forEachOrdered((ancestor) -> {
					add(getButtonForTaskID(ancestor));
				});
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
		final Button button;
		if (task == null) {
			button = new Button(
					"Projects",
					(ClickEvent<Button> event) -> {
						if (targetPage == null || targetPage.equals(TaskEditorLayout.class)) {
							MinorTask.showProjects();
						} else {
							MinorTask.showPage(targetPage, null);
						}
					});
		} else if((task != null && task.taskID.getValue().equals(taskID))
				|| (task == null && taskID == null)){
			// clicking the current task should go to the details page
			button = new Button(
					task.name.getValue(),
					(ClickEvent<Button> event) -> {
						final Long foundID = task.taskID.getValue();
						MinorTask.showTask(foundID);
					});
		}else{
			// jump to the same tab on the new task
			button = new Button(
					task.name.getValue(),
					(ClickEvent<Button> event) -> {
						final Long foundID = task.taskID.getValue();
						MinorTask.showPage(targetPage, foundID);
					});
		}
		formatButton(button);
		if ((task != null && task.taskID.getValue().equals(taskID))
				|| (task == null && taskID == null)) {
			button.addClassName("currenttask");
		}
		return button;
	}

	protected void formatButton(final Button button) {
		button.setIcon(VaadinIcon.ANGLE_RIGHT.create());
		button.setIconAfterText(true);
		button.setSizeUndefined();
		button.addClassNames("small", "projectpath");
		button.getElement().setAttribute("theme", "small");
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

		IconWithClickHandler today = new IconWithClickHandler(VaadinIcon.TIMER);
		today.addClickListener((event) -> {
			Globals.showTodaysTasks();
		});
		today.addClassName("navigator-task-recents");
		
		IconWithClickHandler recent = new IconWithClickHandler(VaadinIcon.CLOCK);
		recent.addClickListener((event) -> {
			Globals.showRecentsPage();
		});
		recent.addClassName("navigator-task-recents");

		IconWithClickHandler favourites = new IconWithClickHandler(VaadinIcon.HEART);
		favourites.addClickListener((event) -> {
			Globals.showFavouritesPage();
		});
		favourites.addClassName("navigator-task-favourites");

		return new Component[]{search, today, recent, favourites};
	}

	public static class WithAddTaskButton extends ProjectPathNavigator {

		public WithAddTaskButton(Class<? extends AuthorisedOptionalTaskPage> targetPage, Long taskID) {
			super(targetPage, taskID);
		}

		@Override
		protected void buildComponent() {
			super.buildComponent();
			final AddTaskButton addTaskButton = new AddTaskButton(getTaskID());
			addTaskButton.addClassNames("small", "projectpath");
			addTaskButton.getElement().setAttribute("theme", "small");
			add(addTaskButton);
		}
	}

	public static class WithAddProjectButton extends ProjectPathNavigator {

		public WithAddProjectButton() {
			super(null, null);
		}

		@Override
		protected void buildComponent() {
			super.buildComponent();
			final AddTaskButton addTaskButton = new AddTaskButton("Add Project...");
			addTaskButton.addClassNames("small", "projectpath");
			addTaskButton.getElement().setAttribute("theme", "small");
			add(addTaskButton);
		}
	}

	public static class WithNewTaskLabel extends ProjectPathNavigator {

		public WithNewTaskLabel(Class<? extends AuthorisedOptionalTaskPage> targetPage, Long taskID) {
			super(targetPage, taskID);
		}

		@Override
		protected void buildComponent() {
			super.buildComponent();
			final Button newTaskButton = new Button("New Task...");
			formatButton(newTaskButton);
			//		newTaskButton.setEnabled(false);
			add(newTaskButton);
		}
	}

}
