/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.Task;

public class SecureTaskDiv extends SecureDiv {

	private Task task;

	public SecureTaskDiv(Long taskid) {
		try {
			this.task = getTask(taskid);
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(SecureTaskDiv.class.getName()).log(Level.SEVERE, null, ex);
			this.task = null;
		}
	}

	public SecureTaskDiv(Task task) {
		this.task = task;
	}
	
	public Task getTask(){
		return task;
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
