/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 *
 * @author gregorygraham
 */
@Tag("add-task-button")
public class AddTaskButton extends HorizontalLayout implements RequiresLogin {

	final Button newTaskButton = new Button("+ Add Subtask");
	private final Long originatingTaskID;

	public AddTaskButton(Long taskID) {
		this.originatingTaskID = taskID;
		add(buildComponent());
	}

	public Component buildComponent() {

//		HorizontalLayout panel = new HorizontalLayout();
		newTaskButton.addClassNames("addtaskbutton");
		newTaskButton.getElement().setAttribute("theme", "small success primary");
		newTaskButton.setSizeUndefined();
		newTaskButton.addClickListener((event) -> {
			minortask().showTaskCreation(originatingTaskID);
		});
		setSizeUndefined();
		return newTaskButton;
	}
}
