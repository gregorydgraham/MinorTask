/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;

public class TaskDiv extends VerticalLayout {

	private Task task;

	public TaskDiv(Long taskid) {
		try {
			this.task = MinorTask.getMinorTask().getTask(taskid);
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(TaskDiv.class.getName()).log(Level.SEVERE, null, ex);
			this.task = null;
		}
	}

	public TaskDiv(Task task) {
		this.task = task;
	}
	
	public Task getTask(){
		return task;
	}

	public Long getTaskID(){
		return task==null? null: task.taskID.longValue();
	}

//	@Override
	protected boolean checkForPermission() {
		return checkForPermission(task);
	}

	protected boolean checkForPermission(Task task) {
		if (task == null) {
			return true;
		} else {
			return true;
		}
	}

	protected MinorTask minortask() {
		return MinorTask.getMinorTask();
	}

	protected DBDatabase getDatabase() {
		return Globals.getDatabase();
	}
	
	protected User getCurrentUser(){
		return minortask().getCurrentUser();
	}
	
	protected long getCurrentUserID(){
		return minortask().getCurrentUserID();
	}

}
