/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.Collections;
import java.util.List;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class ProjectPathNavigator extends HorizontalLayout implements HasMinorTask {

	private final Long taskID;

	public ProjectPathNavigator(Long taskID) {
		this.taskID = taskID;
		buildComponent();
	}

	private void buildComponent() {
		add(getButtonForTaskID(null));
		List<Task> ancestors = minortask().getProjectPathTasks(taskID, minortask().getUserID());
		Collections.reverse(ancestors);
		for (Task ancestor : ancestors) {
			final Button label = getButtonForTaskID(ancestor);
			add(label);
		}
		final AddTaskButton addTaskButton = new AddTaskButton(taskID);
		addTaskButton.addClassName("addbutton");
		add(addTaskButton);
	}

	public Button getButtonForTaskID(Task task) {
		final Button button = new Button((task == null ? "Projects" : task.name.getValue()), (event) -> {
			final Long foundID = task == null ? null : task.taskID.getValue();
			minortask().showTask(foundID);
		});
		button.setIcon(VaadinIcon.ANGLE_RIGHT.create());
		button.setIconAfterText(true);
		button.setSizeUndefined();
		button.setWidth("100%");
		button.addClassNames("tiny", "projectpath");
		return button;
	}

}
