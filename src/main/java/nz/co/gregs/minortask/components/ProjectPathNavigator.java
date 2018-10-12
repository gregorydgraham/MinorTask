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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.Collections;
import java.util.List;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
//@Tag("project-path-navigator")
public class ProjectPathNavigator extends Div implements RequiresLogin {

	private final Long taskID;

	public ProjectPathNavigator(Long taskID) {
		this.taskID = taskID;
		buildComponent();
		addClassName("project-path-navigator");
	}

	protected void buildComponent() {
		getStyle().set("overflow", "auto");
//		add(getButtonForTaskID(null));
		List<Task> ancestors = MinorTask.getProjectPathTasks(getTaskID(), minortask().getUserID());
		Collections.reverse(ancestors);
		ancestors.stream()
				.filter((ancestor) -> (ancestor != null))
				.forEachOrdered((ancestor) -> {
					add(getButtonForTaskID(ancestor));
				});
		
	}

	public Button getButtonForTaskID(Task task) {
		final Button button = new Button((task == null ? "Projects" : task.name.getValue()), (ClickEvent<Button> event) -> {
			final Long foundID = task == null ? null : task.taskID.getValue();
			MinorTask.showTask(foundID);
		});
		formatButton(button);
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
