/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import nz.co.gregs.minortask.components.RequiresPermission;
import java.util.Objects;
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

	@Override
	public default boolean checkForPermission() {
		return checkForPermission(getTask());
	}

	public default boolean checkForPermission(Task task) {
		if (task == null) {
			return RequiresPermission.super.checkForPermission();
		} else {
			final Long userID = task.userID.getValue();
			final Long assigneeID = task.assigneeID.getValue();
			return isLoggedIn()
					&& (Objects.equals(userID, getCurrentUserID())
					|| Objects.equals(assigneeID, getCurrentUserID()));
		}
	}
}
