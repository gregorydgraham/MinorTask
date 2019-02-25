/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.SecureSpan;
import nz.co.gregs.minortask.components.SecureTaskDiv;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class SecureTaskSpan extends SecureSpan{
	
	private Task task;

	public SecureTaskSpan(Long taskid) {
		setTask(taskid);
	}

	public SecureTaskSpan(Task task) {
		this.task = task;
	}
	
	public final Task getTask(){
		return task;
	}

	public final Long getTaskID(){
		return task==null? null: task.taskID.longValue();
	}

	public final void setTask(Long id){
		try {
			this.task = getTask(id);
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(SecureTaskDiv.class.getName()).log(Level.SEVERE, null, ex);
			this.task = null;
		}
	}

	@Override
	protected boolean checkForPermission() {
		return checkForPermission(task);
	}

	protected boolean checkForPermission(Task task) {
		if (task == null) {
			return super.checkForPermission();
		} else {
			final Long userID = task.userID.getValue();
			final Long assigneeID = task.assigneeID.getValue();
			return super.checkForPermission()
					&& (
					Objects.equals(userID, getCurrentUserID())
					|| Objects.equals(assigneeID, getCurrentUserID())
					);
		}
	}
	
}
