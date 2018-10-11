/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import nz.co.gregs.minortask.MinorTask;

/**
 *
 * @author gregorygraham
 */
//@Tag("add-task-button")
public class AddTaskButton extends Button implements RequiresLogin {

	final Button newTaskButton = new Button("+ Add Subtask");
	private Long originatingTaskID = null;

	public AddTaskButton(Long taskID) {
		super("+Add Subtask");
		this.originatingTaskID = taskID;
		buildComponent();
	}

	public final void buildComponent() {
		addClassNames("addtaskbutton");
		addClickListener((event) -> {
			MinorTask.showTaskCreation(originatingTaskID);
		});
	}

	public void setValue(Long value) {
		originatingTaskID = value;
	}
}
