/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.datamodel.Task;

public class SecureTaskDiv extends SecureDiv implements HasTask {

	private Task task;

	public SecureTaskDiv() {
	}

	public SecureTaskDiv(Long taskid) {
		setTask(taskid);
	}

	public SecureTaskDiv(Task task) {
		setTask(task);
	}

	@Override
	public final Task getTask() {
		return task;
	}

	@Override
	public final void setTask(Task newTask) {
		this.task = newTask;
	}
}
