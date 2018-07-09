/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.data.HasValue;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.*;

/**
 *
 * @author gregorygraham
 */
public class TaskEditor extends MinorTaskComponent {

	TextField name = new TextField("Name");
	TextArea description = new TextArea("Description");
	ProjectPicker project = new ProjectPicker(minortask(), getTaskID());
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

	public TaskEditor(MinorTask minortask, Long currentTask) {
		super(minortask, currentTask);
		setCompositionRoot(currentTask != null ? getComponent() : new TaskRootComponent(minortask, currentTask));
	}

	public final Component getComponent() {

		VerticalLayout layout = new VerticalLayout();
		layout.setWidthUndefined();
		layout.addComponent(new ProjectPathNavigator(minortask(), getTaskID()));
		try {
			setEscapeButton(cancelButton);
			setAsDefaultButton(createButton);

			name.setWidthUndefined();
			description.setWidth(100, Unit.PERCENTAGE);
			description.setHeight(3, Unit.CM);
			activeIndicator.setWidth(100, Unit.PERCENTAGE);
			startedIndicator.setWidth(100, Unit.PERCENTAGE);
			overdueIndicator.setWidth(100, Unit.PERCENTAGE);
			completedIndicator.setWidth(100, Unit.PERCENTAGE);
			activeIndicator.setVisible(false);
			startedIndicator.setVisible(false);
			overdueIndicator.setVisible(false);
			completedIndicator.setVisible(false);
			startedIndicator.addStyleName("friendly");
			overdueIndicator.addStyleName("danger");
			completedIndicator.addStyleName("neutral");
			completedDate.setVisible(false);
			completedDate.setReadOnly(true);

			completeButton.addStyleName("danger");
			completeButton.addClickListener(new CompleteTaskListener(minortask(), getTaskID()));
			completeButton.setVisible(false);

			reopenButton.addStyleName("friendly");
			reopenButton.addClickListener(new ReopenTaskListener(minortask(), getTaskID()));
			reopenButton.setVisible(false);

			setFieldValues();

			HorizontalLayout details = new HorizontalLayout(
					name, project,
					activeIndicator, startedIndicator, overdueIndicator, completedIndicator);
			details.setWidthUndefined();

			HorizontalLayout dates = new HorizontalLayout(
					startDate,
					preferredEndDate,
					deadlineDate,
					completedDate
			);
			dates.setWidthUndefined();
			layout.addComponent(details);
			layout.addComponent(description);
			layout.addComponent(dates);
			layout.addComponent(subtasks);
			layout.addComponent(completeButton);
			layout.addComponent(reopenButton);
			layout.addComponent(completedTasks);
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			minortask.sqlerror(ex);
		}
		return layout;
	}

	protected void addChangeListeners() {
		final HasValue.ValueChangeListener<String> stringChange = (event) -> {
			saveTask();
		};
		name.addValueChangeListener((event) -> {
			saveTask();
			minortask.showCurrentTask();
		});
		description.addValueChangeListener(stringChange);

		name.setValueChangeMode(ValueChangeMode.BLUR);
		description.setValueChangeMode(ValueChangeMode.BLUR);

		final HasValue.ValueChangeListener<LocalDate> dateChange = (event) -> {
			saveTask();
		};
		startDate.addValueChangeListener(dateChange);
		preferredEndDate.addValueChangeListener(dateChange);
		deadlineDate.addValueChangeListener(dateChange);

		startDate.setTextFieldEnabled(false);
		preferredEndDate.setTextFieldEnabled(false);
		deadlineDate.setTextFieldEnabled(false);
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException {
		final Long taskID = getTaskID();
		if (taskID != null) {
			Task task = getTask();
			name.setValue(task.name.toString());
			description.setValue(task.description.toString());
			startDate.setValue(MinorTask.asLocalDate(task.startDate.dateValue()));
			preferredEndDate.setValue(MinorTask.asLocalDate(task.preferredDate.dateValue()));
			deadlineDate.setValue(MinorTask.asLocalDate(task.finalDate.dateValue()));

			Task.Project taskProject = getProject();
			if (taskProject != null) {
				LocalDate projectStart = MinorTask.asLocalDate(taskProject.startDate.getValue());
				LocalDate projectEnd = MinorTask.asLocalDate(taskProject.finalDate.getValue());
				if (projectStart.isAfter(projectEnd)) {
					projectStart = projectEnd.minusDays(1);
				}
				startDate.setRangeStart(projectStart);
				startDate.setRangeEnd(projectEnd);
				preferredEndDate.setRangeStart(projectStart);
				preferredEndDate.setRangeEnd(projectEnd);
				deadlineDate.setRangeStart(projectStart);
				deadlineDate.setRangeEnd(projectEnd);
			}

			final Date completed = task.completionDate.dateValue();

			if (completed != null) {
				completedDate.setValue(MinorTask.asLocalDate(completed));
				this.addStyleName("completed");
				completedIndicator.setVisible(true);
				reopenButton.setVisible(true);

				name.setReadOnly(true);
				project.setEnabled(false);
				description.setReadOnly(true);
				startDate.setReadOnly(true);
				preferredEndDate.setReadOnly(true);
				deadlineDate.setReadOnly(true);
				subtasks.disableNewButton();
			} else {
				completeButton.setVisible(true);
				final Date now = new Date();
				if (task.finalDate.dateValue().before(now)) {
					overdueIndicator.setVisible(true);
				} else if (task.startDate.dateValue().before(now)) {
					startedIndicator.setVisible(true);
				} else {
					activeIndicator.setVisible(true);
				}
			}
			createButton.setCaption("Save");

			addChangeListeners();
		}
	}

	public void saveTask() {
		Task task = getTask();

		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
		task.startDate.setValue(MinorTask.asDate(startDate.getValue()));
		task.preferredDate.setValue(MinorTask.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(MinorTask.asDate(deadlineDate.getValue()));

		try {
			getDatabase().update(task);
		} catch (SQLException ex) {
			Logger.getLogger(TaskCreator.class.getName()).log(Level.SEVERE, null, ex);
			minortask.sqlerror(ex);
		}
	}

	public void handleEscapeButton() {
		minortask().showTask(getTaskID());
	}

	public final void setAsDefaultButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener((event) -> {
			saveTask();
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
		private final MinorTask minortask;

		public ReopenTaskListener(MinorTask minortask, Long taskID) {
			this.minortask = minortask;
			this.taskID = taskID;
		}

		@Override
		public void buttonClick(Button.ClickEvent event) {
			List<Task> projectPathTasks = minortask.getProjectPathTasks(taskID, minortask.getUserID());
			for (Task projectPathTask : projectPathTasks) {
				projectPathTask.completionDate.setValue((Date) null);
				try {
					minortask.getDatabase().update(projectPathTask);
				} catch (SQLException ex) {
					minortask.sqlerror(ex);
				}
			}
			Task task = minortask.getTask(taskID, minortask.getUserID());
			task.completionDate.setValue((Date) null);
			try {
				minortask.getDatabase().update(task);
			} catch (SQLException ex) {
				minortask.sqlerror(ex);
			}
			minortask.showTask(taskID);
		}
	}

	private static class CompleteTaskListener implements Button.ClickListener {

		private final Long taskID;
		private final MinorTask minortask;

		public CompleteTaskListener(MinorTask minortask, Long taskID) {
			this.minortask = minortask;
			this.taskID = taskID;
		}

		@Override
		public void buttonClick(Button.ClickEvent event) {
			Task task = completeTask(taskID);
			if (task == null) {
				minortask.showTask(null);
			} else {
				minortask.showTask(task.projectID.getValue());
			}
		}

		private Task completeTask(Long taskID) {
			if (taskID != null) {
				List<Task> subtasks = minortask.getActiveSubtasks(taskID, minortask.getUserID());
				for (Task subtask : subtasks) {
					completeTask(subtask.taskID.getValue());
				}
				Task task = minortask.getTask(taskID, minortask.getUserID());
				task.completionDate.setValue(new Date());
				try {
					minortask.getDatabase().update(task);
				} catch (SQLException ex) {
					minortask.sqlerror(ex);
				}
				return task;
			}
			return null;
		}
	}
}
