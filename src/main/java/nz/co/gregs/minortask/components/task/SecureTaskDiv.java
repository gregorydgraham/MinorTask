/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.datamodel.Task;

public class SecureTaskDiv extends SecureDiv implements HasTask {

	private Task __task;

	public SecureTaskDiv() {
	}

	@Override
	public final Task getTask() {
		return __task;
	}

	@Override
	public void setTask(Task newTask) {
		this.__task = newTask;
	}
}
