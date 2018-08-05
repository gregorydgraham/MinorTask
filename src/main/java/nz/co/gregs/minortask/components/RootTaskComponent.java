/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import nz.co.gregs.minortask.components.tasklists.CompletedTaskList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import nz.co.gregs.minortask.components.tasklists.OpenTaskList;

/**
 *
 * @author gregorygraham
 */
public class RootTaskComponent extends VerticalLayout implements RequiresLogin{

	public RootTaskComponent(Long taskID) {
		add(taskID==null?getComponent():new EditTask(taskID));
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();

		layout.add(
				new ProjectPathNavigator.WithAddTaskButton(null),
				new OpenTaskList(null),
				new CompletedTaskList(null)
		);
		return layout;
	}

}
