/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.datamodel.Task;

public class SecureTaskDiv extends SecureDiv implements HasTaskAndProject {

	private Task.TaskAndProject __taskAndProject;

	public SecureTaskDiv() {
	}

	@Override
	public final Task.TaskAndProject getTaskAndProject() throws Globals.InaccessibleTaskException {
			return __taskAndProject;
	}

	@Override
	public void setTaskAndProject(Task.TaskAndProject newTaskAndProject) {
		this.__taskAndProject = newTaskAndProject;
	}
}
