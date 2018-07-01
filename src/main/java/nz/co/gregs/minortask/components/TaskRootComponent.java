/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class TaskRootComponent extends MinorTaskComponent {

	public TaskRootComponent(MinorTaskUI ui, Long taskID) {
		super(ui, taskID);
		setCompositionRoot(taskID==null?getComponent():new TaskEditorComponent(ui, taskID));
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();
		String projectName = "All";
		if (getTaskID() != null) {
			Task task = Helper.getTask(getTaskID());
			projectName = task.name.getValue();
		}

		layout.addComponents(
				new ProjectPathNavigatorComponent(minortask(), getTaskID()),
				new ActiveTaskList(minortask(), getTaskID()),
				new CompletedTaskList(minortask(), getTaskID())
		);
		return layout;
	}

}
