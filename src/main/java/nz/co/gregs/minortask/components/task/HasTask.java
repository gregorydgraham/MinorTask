/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import nz.co.gregs.minortask.components.RequiresPermission;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public interface HasTask extends RequiresPermission{
	
	public Task getTask();

	public void setTask(Task newTask);

	public default Long getTaskID(){
		return getTask()==null? null: getTask().taskID.longValue();
	}
	
	public default void setTask(Long id){
		try {
			setTask(getTask(id));
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(SecureTaskDiv.class.getName()).log(Level.SEVERE, null, ex);
			setTask((Task)null);
		}
	}

	public default void setTaskID(Long taskID) {
		setTask(taskID);
	}

	@Override
	public default boolean checkForPermission() {
		return checkForPermission(getTask());
	}

	public default boolean checkForPermission(Task task) {
		if (task == null) {
			return isLoggedIn();
		} else {
			final Long userID = task.userID.getValue();
			final Long assigneeID = task.assigneeID.getValue();
			return isLoggedIn()
					&& (Objects.equals(userID, getCurrentUserID())
					|| Objects.equals(assigneeID, getCurrentUserID()));
		}
	}
}
