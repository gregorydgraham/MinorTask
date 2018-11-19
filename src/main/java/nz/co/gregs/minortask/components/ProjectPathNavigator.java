/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import static nz.co.gregs.minortask.Globals.getDatabase;
import static nz.co.gregs.minortask.Globals.getTaskExample;
import static nz.co.gregs.minortask.Globals.sqlerror;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
//@Tag("project-path-navigator")
public class ProjectPathNavigator extends Div implements MinorTaskComponent, RequiresLogin {

	private final Long taskID;

	public ProjectPathNavigator(Long taskID) {
		this.taskID = taskID;
		buildComponent();
		addClassName("project-path-navigator");
	}

	protected void buildComponent() {
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
		final Button button = new Button((task == null ? "Projects" : task.name.getValue()), (ClickEvent<Button> event) -> {
			final Long foundID = task == null ? null : task.taskID.getValue();
			MinorTask.showTask(foundID);
		});
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

	public static class WithAddTaskButton extends ProjectPathNavigator {

		public WithAddTaskButton(Long taskID) {
			super(taskID);
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
			super(null);
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

		public WithNewTaskLabel(Long taskID) {
			super(taskID);
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
