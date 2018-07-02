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
import com.vaadin.ui.Label;
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
	ActiveTaskList subtasks = new ActiveTaskList(minortask(), getTaskID());
	Button completeButton = new Button("Complete This Task");
	Button reopenButton = new Button("Reopen This Task");
	CompletedTaskList completedTasks = new CompletedTaskList(minortask(), getTaskID());
	TextField notes = new TextField("Notes");
	DateField startDate = new DateField("Start");
	DateField preferredEndDate = new DateField("End");
	DateField deadlineDate = new DateField("Deadline");
	DateField completedDate = new DateField("Completed");
	Label activeIndicator = new Label("Active");
	Label startedIndicator = new Label("Started");
	Label overdueIndicator = new Label("Overdue");
	Label completedIndicator = new Label("COMPLETED");
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
			activeIndicator.setWidth(100, Unit.PERCENTAGE);
			startedIndicator.setWidth(100, Unit.PERCENTAGE);
			overdueIndicator.setWidth(100, Unit.PERCENTAGE);
			completedIndicator.setWidth(100, Unit.PERCENTAGE);
			activeIndicator.setVisible(false);
			startedIndicator.setVisible(false);
			overdueIndicator.setVisible(false);
			completedIndicator.setVisible(false);
			overdueIndicator.addStyleName("danger");
			completedIndicator.addStyleName("danger");

			completeButton.addStyleName("danger");
			completeButton.addClickListener(new CompleteTaskListener(minortask(), getTaskID()));
			completeButton.setVisible(false);

			reopenButton.addStyleName("friendly");
			reopenButton.addClickListener(new ReopenTaskListener(minortask(), getTaskID()));
			reopenButton.setVisible(false);

			setFieldValues();

			HorizontalLayout details = new HorizontalLayout(
					name,
					description, activeIndicator, startedIndicator, overdueIndicator, completedIndicator);
//			details.setComponentAlignment(activeIndicator, Alignment.BOTTOM_RIGHT);
			details.setWidthUndefined();

			layout.addComponent(details);

			HorizontalLayout dates = new HorizontalLayout(
					startDate,
					preferredEndDate,
					deadlineDate,
					completedDate
			);
			dates.setWidthUndefined();
			layout.addComponent(dates);
			layout.addComponent(subtasks);
			layout.addComponent(completeButton);
			layout.addComponent(reopenButton);
			layout.addComponent(
					new HorizontalLayout(
							cancelButton,
							createButton));
			layout.addComponent(completedTasks);
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
			completedDate.setReadOnly(true);
			final Date completed = task.completionDate.dateValue();
			if (completed == null) {
				completedDate.setVisible(false);
			} else {
				completedDate.setValue(Helper.asLocalDate(completed));
//				this.setReadOnly(true);
//				reopenButton.setEnabled(true);
			}

			final Date now = new Date();
			if (task.completionDate.dateValue() != null) {
				completedIndicator.setVisible(true);
				reopenButton.setVisible(true);
				name.setReadOnly(true);
				description.setReadOnly(true);
				startDate.setReadOnly(true);
				preferredEndDate.setReadOnly(true);
				deadlineDate.setReadOnly(true);
			} else {
				completeButton.setVisible(true);
				if (task.finalDate.dateValue().before(now)) {
					overdueIndicator.setVisible(true);
				} else if (task.startDate.dateValue().before(now)) {
					startedIndicator.setVisible(true);
				} else {
					activeIndicator.setVisible(true);
				}
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
				projectPathTask.completionDate.setValue((Date) null);
				try {
					Helper.getDatabase().update(projectPathTask);
				} catch (SQLException ex) {
					Helper.sqlerror(ex);
				}
			}
			Task task = Helper.getTask(taskID);
			task.completionDate.setValue((Date) null);
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
			Task task = completeTask(taskID);
//			Task task = Helper.getTask(taskID);
//			task.completionDate.setValue(new Date());
//			try {
//				Helper.getDatabase().update(task);
//			} catch (SQLException ex) {
//				Helper.sqlerror(ex);
//			}
			if (task == null) {
				minortask.showTask(null);
			} else {
				minortask.showTask(task.projectID.getValue());
			}
		}

		private Task completeTask(Long taskID) {
			if (taskID != null) {
				List<Task> subtasks = Helper.getActiveSubtasks(taskID);
				for (Task subtask : subtasks) {
					Helper.warning("Task", subtask.name.toString());
					completeTask(subtask.taskID.getValue());
				}
				Task task = Helper.getTask(taskID);
				task.completionDate.setValue(new Date());
				try {
					Helper.getDatabase().update(task);
				} catch (SQLException ex) {
					Helper.sqlerror(ex);
				}
				return task;
			}
			return null;
		}
	}
}
