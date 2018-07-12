/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import nz.co.gregs.minortask.MinorTask;

/**
 *
 * @author gregorygraham
 */
public class TaskRootComponent extends VerticalLayout implements RequiresLogin{

	public TaskRootComponent(Long taskID) throws MinorTask.InaccessibleTaskException {
		add(taskID==null?getComponent():new TaskEditor(taskID));
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();

		layout.add(
				new ProjectPathNavigator(null),
				new ActiveTaskList(null),
				new CompletedTaskList(null)
		);
		return layout;
	}

}
