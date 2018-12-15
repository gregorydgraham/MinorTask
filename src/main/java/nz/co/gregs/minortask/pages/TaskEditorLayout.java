/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.EditTask;
import nz.co.gregs.minortask.components.RootTaskComponent;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@Route(value = "task", layout = MinortaskPage.class)
public class TaskEditorLayout extends AuthorisedOptionalTaskPage {

	public TaskEditorLayout() {
		super();
	}

	@Override
	public Component getInternalComponent(Long parameter) {
		if (parameter == null) {
			return new RootTaskComponent(null);
		} else {
//			chat("making EditTask");
			final EditTask editor = new EditTask(parameter);
			return editor;
		}
	}

	@Override
	public String getPageTitle() {
		try {
			final Task task = getTask(taskID);
			if (taskID == null || task == null) {
				return "MinorTask: Projects";
			} else {
				return "MinorTask: " + task.name.stringValue();
			}
		} catch (MinorTask.InaccessibleTaskException ex) {
//			Logger.getLogger(TaskEditorLayout.class.getName()).log(Level.INFO, null, ex);
		}
		return "MinorTask: Access Denied";
	}
}
