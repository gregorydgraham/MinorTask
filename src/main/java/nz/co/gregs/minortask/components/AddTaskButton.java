/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import nz.co.gregs.minortask.MinorTask;

/**
 *
 * @author gregorygraham
 */
//@Tag("add-task-button")
public class AddTaskButton extends SecureButton {

//	final Button newTaskButton;
	private Long originatingTaskID = null;
	private static final String DEFAULT_LABEL = "+ Add Subtask";

	public AddTaskButton() {
		this(null, DEFAULT_LABEL);
	}
	
	public AddTaskButton(String buttonLabel) {
		this(null, buttonLabel);
	}
	
	public AddTaskButton(Long taskID) {
		this(taskID, DEFAULT_LABEL);
	}

	AddTaskButton(Long taskID, String buttonLabel) {
		super(buttonLabel);
		this.originatingTaskID = taskID;
		buildComponent();
		addClassNames("addtaskbutton");
		setTooltipText("Expand and simplify this task by adding a smaller task that will help complete this task");
	}
	
	public void setTaskID(Long id){
		setValue(id);
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
