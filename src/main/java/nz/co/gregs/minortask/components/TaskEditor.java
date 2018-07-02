/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
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
public class TaskEditor extends MinorTaskComponent {

	TextField name = new TextField("Name");
	TextField description = new TextField("Description");
	TextField project = new TextField("Project");
	ActiveTaskList subtasks = new ActiveTaskList(minortask(), getTaskID());
	Button completeButton = new Button("Complete This Task");
	CompletedTaskList completedTasks = new CompletedTaskList(minortask(), getTaskID());
	TextField notes = new TextField("Notes");
	DateField startDate = new DateField("Start");
	DateField preferredEndDate = new DateField("End");
	DateField deadlineDate = new DateField("Deadline");
	Button statusIndicator = new Button("creating");
	Button createButton = new Button("Create");
	Button cancelButton = new Button("Cancel");

	public TaskEditor(MinorTaskUI ui, Long currentTask) {
		super(ui, currentTask);
		setCompositionRoot(currentTask != null ? getComponent() : new TaskRootComponent(ui, currentTask));
	}

	public final Component getComponent() {

		VerticalLayout layout = new VerticalLayout();
		layout.setWidthUndefined();
		layout.addComponent(new ProjectPathNavigator(minortask(), getTaskID()));
		try {
			setEscapeButton(cancelButton);
			setAsDefaultButton(createButton);

			name.setWidthUndefined();
			description.setWidthUndefined();
			statusIndicator.setWidth(100, Unit.PERCENTAGE);
			project.setCaption("Part Of:");
			project.setReadOnly(true);

			completeButton.addStyleName("danger");
			completeButton.addClickListener(new CompleteTaskListener(minortask(), getTaskID()));

			setFieldValues();

			HorizontalLayout details = new HorizontalLayout(
					name,
					description, statusIndicator);
			details.setComponentAlignment(statusIndicator, Alignment.BOTTOM_RIGHT);
			details.setWidthUndefined();

			layout.addComponent(details);

			HorizontalLayout dates = new HorizontalLayout(
					startDate,
					preferredEndDate,
					deadlineDate
			);
			dates.setWidthUndefined();
			layout.addComponent(dates);
			layout.addComponent(subtasks);
			layout.addComponent(completeButton);
			layout.addComponent(completedTasks);
			layout.addComponent(
					new HorizontalLayout(
							cancelButton,
							createButton));
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			Helper.sqlerror(ex);
		}
		return layout;
	}

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
			final Date now = new Date();
			if (task.completionDate != null) {

				statusIndicator.removeStyleName("friendly");
				statusIndicator.addStyleName("danger");

				completeButton = new Button("Reopen Task");
				completeButton.addStyleName("friendly");
				completeButton.removeStyleName("danger");
				completeButton.addClickListener(new ReopenTaskListener(minortask(), getTaskID()));
			} else if (task.finalDate.dateValue().before(now)) {
				statusIndicator.removeStyleName("friendly");
				statusIndicator.addStyleName("danger");
			} else if (task.startDate.dateValue().before(now)) {
				statusIndicator.removeStyleName("danger");
				statusIndicator.addStyleName("friendly");
			} else {
				statusIndicator.setCaption(Task.Status.CREATED.toString());
				statusIndicator.setStyleName("friendly");
			}
			createButton.setCaption("Save");
		}
	}

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

	public void handleEscapeButton() {
		minortask().showTask(getTaskID());
	}

	public final void setAsDefaultButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener((event) -> {
			handleDefaultButton();
		});
	}

	public final void setEscapeButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
		button.addClickListener((event) -> {
			handleEscapeButton();
		});
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

	private static class CompleteTaskListener implements Button.ClickListener {

		private final Long taskID;
		private final MinorTaskUI minortask;

		public CompleteTaskListener(MinorTaskUI minortask, Long taskID) {
			this.minortask = minortask;
			this.taskID = taskID;
		}

		@Override
		public void buttonClick(Button.ClickEvent event) {
			completeTask(taskID);
			Task task = Helper.getTask(taskID);
			task.status.setValue(Task.Status.COMPLETED);
			try {
				Helper.getDatabase().update(task);
			} catch (SQLException ex) {
				Helper.sqlerror(ex);
			}
			minortask.showTask(task.projectID.getValue());
		}

		private void completeTask(Long taskID) {
			if (taskID != null) {
				List<Task> subtasks = Helper.getActiveSubtasks(taskID);
				for (Task subtask : subtasks) {
					Helper.warning("Task", subtask.name.toString());
					completeTask(subtask.taskID.getValue());
				}
				Task task = Helper.getTask(taskID);
				task.status.setValue(Task.Status.COMPLETED);
				task.completionDate.setValue(new Date());
				try {
					Helper.getDatabase().update(task);
				} catch (SQLException ex) {
					Helper.sqlerror(ex);
				}
			}
		}
	}
}
