/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Button;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.datatypes.DBStringEnum;
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

			final Task.Status status = task.status.enumValue();
			if (status != null) {
				statusIndicator.setCaption(status.toString());
				statusIndicator.addStyleName("friendly");
				if (status.equals(Task.Status.COMPLETED)){
					
					statusIndicator.removeStyleName("friendly");
					statusIndicator.addStyleName("danger");
					
					completeButton = new Button("Reopen Task");
					completeButton.addStyleName("friendly");
					completeButton.removeStyleName("danger");
					completeButton.addClickListener(new ReopenTaskListener(minortask(), getTaskID()));
				}
			} else {
				statusIndicator.setCaption(Task.Status.CREATED.name());
				statusIndicator.setStyleName("friendly");
			}
			
			createButton.setCaption("Save");
		}
	}

	@Override
	public void handleDefaultButton() {
		Task task = Helper.getTask(getTaskID());

		Helper.chat("TASKID = " + task.taskID.getValue());
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

	private static class ReopenTaskListener implements Button.ClickListener {

		private final Long taskID;
		private final MinorTaskUI minortask;

		public ReopenTaskListener(MinorTaskUI minortask, Long taskID) {
			this.minortask = minortask;
			this.taskID = taskID;
		}

		@Override
		public void buttonClick(Button.ClickEvent event) {
			List<Task> projectPathTasks = Helper.getProjectPathTasks(taskID);
			for (Task projectPathTask : projectPathTasks) {
				projectPathTask.status.setValue(Task.Status.CREATED);
				try {
					Helper.getDatabase().update(projectPathTask);
				} catch (SQLException ex) {
					Helper.sqlerror(ex);
				}
			}
			Task task = Helper.getTask(taskID);
			task.status.setValue(Task.Status.CREATED);
			try {
				Helper.getDatabase().update(task);
			} catch (SQLException ex) {
				Helper.sqlerror(ex);
			}
			minortask.showTask(taskID);
		}
	}
}
