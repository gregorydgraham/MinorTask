/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class TaskRootComponent extends MinorTaskComponent {

	public TaskRootComponent(MinorTask minortask, Long taskID) {
		super(minortask, taskID);
		setCompositionRoot(taskID==null?getComponent():new TaskEditor(minortask, taskID));
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();
//		String projectName = "All";
//		if (getTaskID() != null) {
//			Task task = MinorTask.getTask(getTaskID(), minortask().getUserID());
//			projectName = task.name.getValue();
//		}

		layout.addComponents(
				new ProjectPathNavigator(minortask(), getTaskID()),
				new ActiveTaskList(minortask(), getTaskID()),
				new CompletedTaskList(minortask(), getTaskID())
		);
		return layout;
	}

}
