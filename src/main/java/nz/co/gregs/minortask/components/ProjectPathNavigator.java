/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import java.util.List;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class ProjectPathNavigator extends MinorTaskComponent {

//	private final MinorTaskUI ui;
//	private final Long currentTaskID;
	public ProjectPathNavigator(MinorTask minortask, Long taskID) {
		super(minortask, taskID);
		setCompositionRoot(getComponent());
	}

	private Component getComponent() {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.addComponentAsFirst(getButtonForTaskID(null));
		List<Task> ancestors = minortask().getProjectPathTasks(getTaskID(), minortask().getUserID());
		for (Task ancestor : ancestors) {
			final Button label = getButtonForTaskID(ancestor);
			hLayout.addComponent(label, 1);
		}
		final AddTaskButton addTaskButton = new AddTaskButton(minortask, getTaskID());
		hLayout.addComponent(addTaskButton);
		return hLayout;
	}


	public Button getButtonForTaskID(Task task) {
		final Button button = new Button((task == null ? "Projects" : task.name.getValue()) + " > ", (event) -> {
			final Long taskID = task == null ? null : task.taskID.getValue();
			minortask().showTask(taskID);
		});
		button.addStyleNames("tiny", "projectpath");
		return button;
	}

}
