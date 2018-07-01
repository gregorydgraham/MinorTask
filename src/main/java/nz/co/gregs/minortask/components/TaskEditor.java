/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.*;

/**
 *
 * @author gregorygraham
 */
public class TaskEditor extends TaskCreator {

	public TaskEditor(MinorTaskUI ui, Long currentTask) {
		super(ui, currentTask);
		setCompositionRoot(currentTask != null ? getComponent() : new TaskRootComponent(ui, currentTask));
	}

	@Override
	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException {
		final Long taskID = getTaskID();
		if (taskID != null) {
			Task task = Helper.getTask(taskID);
			name.setValue(task.name.toString());
			description.setValue(task.description.toString());
			startDate.setValue(Helper.asLocalDate(task.startDate.dateValue()));
			preferredEndDate.setValue(Helper.asLocalDate(task.preferredDate.dateValue()));
			deadlineDate.setValue(Helper.asLocalDate(task.finalDate.dateValue()));
			
			createButton.setCaption("Save");
		}
	}

	@Override
	public void handleDefaultButton() {
		Task task = Helper.getTask(getTaskID());

		Helper.chat("TASKID = "+task.taskID.getValue());
		task.userID.setValue(minortask().getUserID());
		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
		task.startDate.setValue(Helper.asDate(startDate.getValue()));
		task.preferredDate.setValue(Helper.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(Helper.asDate(deadlineDate.getValue()));

		try {
			Helper.getDatabase().update(task);
		} catch (SQLException ex) {
			Logger.getLogger(TaskCreator.class.getName()).log(Level.SEVERE, null, ex);
			Helper.sqlerror(ex);
		}
		minortask().showTask(getTaskID());
	}

	@Override
	public void handleEscapeButton() {
		minortask().showTask(getTaskID());
	}
}
