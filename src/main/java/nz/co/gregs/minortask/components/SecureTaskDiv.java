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

	@Override
	protected boolean checkForPermission() {
		return checkForPermission(task);
	}

	protected boolean checkForPermission(Task task) {
		if (task == null) {
			return super.checkForPermission();
		} else {
			return super.checkForPermission()
					&& (
					Objects.equals(task.userID.getValue(), getCurrentUserID())
					|| Objects.equals(task.assigneeID.getValue(), getCurrentUserID())
					);
		}
	}

}
