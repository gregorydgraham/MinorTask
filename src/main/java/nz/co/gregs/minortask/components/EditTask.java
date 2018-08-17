/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import nz.co.gregs.minortask.components.tasklists.CompletedTaskList;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.tasklists.OpenTaskList;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class EditTask extends VerticalLayout implements RequiresLogin {

	TextField name = new TextField("Name");
	TextField user = new TextField("User");
	TextArea description = new TextArea("Description");
	Button completeButton = new Button("Complete This Task");
	Button reopenButton = new Button("Reopen This Task");
	ProjectPicker project;
	OpenTaskList subtasks;
	CompletedTaskList completedTasks;
	TextField notes = new TextField("Notes");
	DatePicker startDate = new DatePicker("Start");
	DatePicker preferredEndDate = new DatePicker("End");
	DatePicker deadlineDate = new DatePicker("Deadline");
	DatePicker completedDate = new DatePicker("Completed");
	Label activeIndicator = new Label("Active");
	Label startedIndicator = new Label("Started");
	Label overdueIndicator = new Label("Overdue");
	Label completedIndicator = new Label("COMPLETED");
	Button createButton = new Button("Create");
	Button cancelButton = new Button("Cancel");
	private final Long taskID;
	private Task.TaskAndProject taskAndProject;

	public EditTask(Long currentTask) {
		this.taskID = currentTask;
		try {

			taskAndProject = getTaskAndProject(taskID);
			project = new ProjectPicker(currentTask);

			subtasks = new OpenTaskList(currentTask);
			completedTasks = new CompletedTaskList(currentTask);
			add(currentTask != null ? getComponent() : new RootTaskComponent(currentTask));
		} catch (MinorTask.InaccessibleTaskException ex) {
			add(new AccessDeniedComponent());
		}
	}

	public final Component getComponent() {

		setEscapeButton(cancelButton);
		setAsDefaultButton(createButton);

		name.setSizeUndefined();
		description.setWidth("100%");
		description.setHeight("3cm");
		activeIndicator.setWidth("100%");
		startedIndicator.setWidth("100%");
		overdueIndicator.setWidth("100%");
		completedIndicator.setWidth("100%");
		activeIndicator.setVisible(false);
		startedIndicator.setVisible(false);
		overdueIndicator.setVisible(false);
		completedIndicator.setVisible(false);
		startedIndicator.addClassName("friendly");
		overdueIndicator.addClassName("danger");
		completedIndicator.addClassName("neutral");
		completedDate.setVisible(false);
		completedDate.setReadOnly(true);

		completeButton.addClassNames("danger", "completebutton");
		completeButton.addClickListener(new CompleteTaskListener(minortask(), taskID));
		completeButton.setVisible(false);

		reopenButton.addClassName("friendly");
		reopenButton.addClickListener(new ReopenTaskListener(minortask(), taskID));
		reopenButton.setVisible(false);

		HorizontalLayout details = new HorizontalLayout(
				name, project,
				activeIndicator, startedIndicator, overdueIndicator, completedIndicator);
		details.setSizeUndefined();

		HorizontalLayout dates = new HorizontalLayout(
				startDate,
				preferredEndDate,
				deadlineDate,
				completedDate
		);
		dates.setSizeUndefined();
		
		VerticalLayout detailsLayout = new VerticalLayout();
		detailsLayout.setSizeUndefined();
		detailsLayout.add(new ProjectPathNavigator.WithAddTaskButton(taskID));
		detailsLayout.add(details);
		detailsLayout.add(description);
		detailsLayout.add(dates);
//		detailsLayout.add(new ImageGrid(taskID));
		detailsLayout.add(new DocumentGrid(taskID));
		VerticalLayout tasksLayout = new VerticalLayout(subtasks);
		tasksLayout.add(completeButton, reopenButton, completedTasks);
		Div topLayout = new Div(detailsLayout, tasksLayout);
		try {
			setFieldValues();
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			minortask().sqlerror(ex);
		}
		return topLayout;
	}

	protected void addChangeListeners() {
		name.addValueChangeListener((event) -> {
			saveTask();
			minortask().showTask(taskID);
		});
		description.addValueChangeListener((event) -> {
			saveTask();
		});

		name.setValueChangeMode(ValueChangeMode.ON_BLUR);
		description.setValueChangeMode(ValueChangeMode.ON_BLUR);
		final HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<DatePicker, LocalDate>> dateChange = (event) -> {
			saveTask();
		};

		startDate.addValueChangeListener(dateChange);
		preferredEndDate.addValueChangeListener(dateChange);
		deadlineDate.addValueChangeListener(dateChange);
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException {
		if (taskID != null) {
			Task task = taskAndProject.getTask();
			if (task != null) {
				name.setValue(task.name.stringValue());
				description.setValue(task.description.toString());
				startDate.setValue(asLocalDate(task.startDate.dateValue()));
				preferredEndDate.setValue(asLocalDate(task.preferredDate.dateValue()));
				deadlineDate.setValue(asLocalDate(task.finalDate.dateValue()));
				
				Task.Project taskProject = taskAndProject.getProject();
				if (taskProject != null) {
					LocalDate projectStart = asLocalDate(taskProject.startDate.getValue());
					LocalDate projectEnd = asLocalDate(taskProject.finalDate.getValue());
					if (projectStart != null && projectEnd != null) {
						if (projectStart.isAfter(projectEnd)) {
							projectStart = projectEnd.minusDays(1);
						}
						startDate.setMin(projectStart);
						startDate.setMax(projectEnd.minusDays(1));
						preferredEndDate.setMin(projectStart);
						preferredEndDate.setMax(projectEnd);
						deadlineDate.setMin(projectStart.plusDays(1));
						deadlineDate.setMax(projectEnd);
					}
				}

				final Date completed = task.completionDate.dateValue();

				if (completed != null) {
					completedDate.setValue(asLocalDate(completed));
					this.addClassName("completed");
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
				createButton.setText("Save");

				addChangeListeners();
			}
		}
	}

	public void saveTask() {
		try {
			Task task = getTask(taskID);

			task.name.setValue(name.getValue());
			task.description.setValue(description.getValue());
			task.startDate.setValue(asDate(startDate.getValue()));
			task.preferredDate.setValue(asDate(preferredEndDate.getValue()));
			task.finalDate.setValue(asDate(deadlineDate.getValue()));

			try {
				getDatabase().update(task);
			} catch (SQLException ex) {
				Logger.getLogger(CreateTask.class.getName()).log(Level.SEVERE, null, ex);
				minortask().sqlerror(ex);
			}
			minortask().chat("Saved.");
		} catch (MinorTask.InaccessibleTaskException ex) {
			Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void handleEscapeButton() {
		minortask().showTask(taskID);
	}

	public final void setAsDefaultButton(Button button) {
		button.addClickListener((event) -> {
			saveTask();
		});
	}

	public final void setEscapeButton(Button button) {
		button.addClickListener((event) -> {
			handleEscapeButton();
		});
	}

	private class ReopenTaskListener implements ComponentEventListener<ClickEvent<Button>> {

		private final Long taskID;
		private final MinorTask minortask;

		public ReopenTaskListener(MinorTask minortask, Long taskID) {
			this.minortask = minortask;
			this.taskID = taskID;
		}

		@Override
		public void onComponentEvent(ClickEvent<Button> event) {
			List<Task> projectPathTasks = getProjectPathTasks(taskID);
			for (Task projectPathTask : projectPathTasks) {
				projectPathTask.completionDate.setValue((Date) null);
				try {
					minortask().getDatabase().update(projectPathTask);
				} catch (SQLException ex) {
					minortask().sqlerror(ex);
				}
			}
			Task task;
			try {
				task = getTask(taskID);
				task.completionDate.setValue((Date) null);
				try {
					minortask().getDatabase().update(task);
				} catch (SQLException ex) {
					minortask().sqlerror(ex);
				}
			} catch (MinorTask.InaccessibleTaskException ex) {
				Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
			}
			minortask().showTask(taskID);
		}
	}

	private class CompleteTaskListener implements ComponentEventListener<ClickEvent<Button>> {

		private final Long taskID;

		public CompleteTaskListener(MinorTask minortask, Long taskID) {
			this.taskID = taskID;
		}

		@Override
		public void onComponentEvent(ClickEvent<Button> event) {
			try {
				Task task = completeTask(taskID);
				if (task == null) {
					minortask().showTask(null);
				} else {
					minortask().showTask(task.projectID.getValue());
				}
			} catch (MinorTask.InaccessibleTaskException ex) {
				Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		private Task completeTask(Long taskID) throws MinorTask.InaccessibleTaskException {
			if (taskID != null) {
				List<Task> subtasks = minortask().getActiveSubtasks(taskID, minortask().getUserID());
				for (Task subtask : subtasks) {
					completeTask(subtask.taskID.getValue());
				}
				Task task = getTask(taskID);
				task.completionDate.setValue(new Date());
				try {
					minortask().getDatabase().update(task);
				} catch (SQLException ex) {
					minortask().sqlerror(ex);
				}
				return task;
			}
			return null;
		}
	}
}
