/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import nz.co.gregs.minortask.MinorTask;

/**
 *
 * @author gregorygraham
 */
public class AddTaskButton extends MinorTaskComponent {
		final Button newTaskButton = new Button("+ Add Subtask");

	public AddTaskButton(MinorTask minortask, Long taskID) {
		super(minortask, taskID);
		HorizontalLayout panel = new HorizontalLayout();
		newTaskButton.addStyleName("friendly");
		newTaskButton.setWidthUndefined();//(100, Unit.PERCENTAGE);
		newTaskButton.addClickListener((event) -> {
			minortask().showTaskCreation(this.getTaskID());
		});
		panel.setWidthUndefined();
		panel.addComponent(newTaskButton);
		panel.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
		this.setCompositionRoot(panel);
	}

	@Override
	public void addStyleName(String style) {
		super.addStyleName(style);
		newTaskButton.addStyleName(style);
	}

	@Override
	public void addStyleNames(String... style) {
		super.addStyleNames(style);
		newTaskButton.addStyleNames(style);
	}

}
