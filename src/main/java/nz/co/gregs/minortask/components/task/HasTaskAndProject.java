/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
interface HasTaskAndProject extends HasTask {

	public void setTaskAndProject(Task.TaskAndProject taskAndProject);

	public Task.TaskAndProject getTaskAndProject() throws Globals.InaccessibleTaskException;

	public default Long getProjectID() {
		try {
			if (getTaskAndProject() != null && getTaskAndProject().getProject() != null) {
				return getTaskAndProject().getProject().taskID.getValue();
			}
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(HasTaskAndProject.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	@Override
	public default Long getTaskID() {
		try {
			if (getTaskAndProject() == null) {
				return null;
			} else if (getTaskAndProject().getTask() == null) {
				return null;
			} else {
				return getTaskAndProject().getTask().taskID.getValue();
			}
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(HasTaskAndProject.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	@Override
	public default Task getTask() {
		try {
			if (getTaskAndProject() == null) {
				return null;
			} else {
				return getTaskAndProject().getTask();
			}
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(HasTaskAndProject.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	@Override
	public default void setTask(Task newTask) {
		try {
			setTaskAndProject(minortask().getTaskAndProject(newTask));
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(HasTaskAndProject.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public default void setTask(Task.TaskAndProject newTask) {
		setTaskAndProject(newTask);
	}
}
