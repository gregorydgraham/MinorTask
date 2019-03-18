/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import nz.co.gregs.minortask.components.generic.SecureSpan;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class SecureTaskSpan extends SecureSpan implements HasTask{
	
	private Task task;

	public SecureTaskSpan(Long taskid) {
		setTask(taskid);
	}

	public SecureTaskSpan(Task task) {
		setTask(task);
	}
	
	@Override
	public final Task getTask(){
		return task;
	}
	
	@Override
	public final void setTask(Task newTask){
		task = newTask;
	}
}
