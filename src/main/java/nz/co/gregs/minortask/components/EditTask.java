/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import nz.co.gregs.minortask.place.PlaceGrid;
import nz.co.gregs.minortask.documentupload.DocumentGrid;
import nz.co.gregs.minortask.weblinks.WeblinkGrid;
import nz.co.gregs.minortask.components.tasklists.CompletedTaskList;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
import nz.co.gregs.dbvolution.actions.DBActionList;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.tasklists.OpenTaskList;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class EditTask extends Div implements RequiresLogin {

	PaperInput name = new PaperInput();
	TextField user = new TextField("User");
	TextArea description = new TextArea("");
	Button completeButton = new Button("Complete This Task");
	Button reopenButton = new Button("Reopen This Task");
	ProjectPicker project;
	OpenTaskList subtasks;
	CompletedTaskList completedTasks;
	TextField notes = new TextField("Notes");
	OptionalDatePicker startDate = new OptionalDatePicker("Start Date");
	OptionalDatePicker preferredEndDate = new OptionalDatePicker("Reminder");
	OptionalDatePicker deadlineDate = new OptionalDatePicker("Deadline");
	WeblinkGrid weblinkGrid = new WeblinkGrid();
	DocumentGrid documentGrid = new DocumentGrid();
	PlaceGrid placeGrid = new PlaceGrid();
	RangeDatePicker rangeDatePicker = new RangeDatePicker();
	DatePicker completedDate = new DatePicker("Completed");
	Label activeIndicator = new Label("Active");
	Label startedIndicator = new Label("Started");
	Label overdueIndicator = new Label("Overdue");
	Label oneDayMaybeIndicator = new Label("One Day Maybe");
	Label completedIndicator = new Label("COMPLETED");
	Button createButton = new Button("Create");
	Button cancelButton = new Button("Cancel");
	private final Long taskID;
	private Task.TaskAndProject taskAndProject;

	public EditTask(Long currentTask) {
		this.taskID = currentTask;
		try {

			taskAndProject = getTaskAndProject(taskID);
			project = new ProjectPicker(taskID);

			subtasks = new OpenTaskList(taskID);
			completedTasks = new CompletedTaskList(taskID);
			add(currentTask != null ? getComponent() : new RootTaskComponent(taskID));
		} catch (Globals.InaccessibleTaskException ex) {
			add(new AccessDeniedComponent());
		}
		addClassName("edit-task-component");
	}

	public final Component getComponent() {

		setEscapeButton(cancelButton);
		setAsDefaultButton(createButton);

		name.addClassName("edit-task-name-input");
		description.addClassName("edit-task-description");

		activeIndicator.setVisible(false);
		startedIndicator.setVisible(false);
		overdueIndicator.setVisible(false);
		completedIndicator.setVisible(false);
		oneDayMaybeIndicator.setVisible(false);
		startedIndicator.addClassName("friendly");
		overdueIndicator.addClassName("danger");
		completedIndicator.addClassName("neutral");
		oneDayMaybeIndicator.addClassName("neutral");

		completedDate.setVisible(false);
		completedDate.setReadOnly(true);

		completeButton.addClassNames("danger", "completebutton");
		completeButton.addClickListener(new CompleteTaskListener(minortask(), taskID));
		completeButton.setVisible(false);

		reopenButton.addClassNames("friendly", "edit-task-reopenbutton");
		reopenButton.addClickListener(new ReopenTaskListener(minortask(), taskID));
		reopenButton.setVisible(false);

		completedIndicator.getStyle().set("padding", "0").set("margin-left", "0").set("margin-right", "0").set("margin-bottom", "0");
		VerticalLayout completedLayout = new VerticalLayout(completedIndicator, reopenButton);
		completedLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.END);

		HorizontalLayout details = new HorizontalLayout(
				activeIndicator, startedIndicator, overdueIndicator, completedLayout);
		details.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
		details.setSizeUndefined();

		Div dates = new Div(
				startDate,
				preferredEndDate,
				deadlineDate,
				completedDate
		);
		dates.addClassName("dates-component");
		dates.setSizeUndefined();

		ProjectPathNavigator.WithAddTaskButton projectPath = new ProjectPathNavigator.WithAddTaskButton(taskID);
		Div extrasLayout = new Div();
//		extrasLayout.add(description);
//		extrasLayout.add(rangeDatePicker);
		extrasLayout.add(dates);
		extrasLayout.add(placeGrid);
		extrasLayout.add(documentGrid);
		extrasLayout.add(weblinkGrid);
		final Div nameDiv = new Div(name, project);
		nameDiv.addClassName("edit-task-name");
		Div topLayout = new Div(
				projectPath,
				nameDiv,
				description,
				subtasks,
				extrasLayout,
				completeButton,
				reopenButton,
				completedTasks);
		topLayout.addClassName("edit-task-contents");
		try {
			setFieldValues();
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			Globals.sqlerror(ex);
		}
		return topLayout;
	}

	protected void addChangeListeners() {
		name.addBlurListener((event) -> {
			saveTask();
			Globals.showTask(taskID);
		});
		description.addValueChangeListener((event) -> {
			saveTask();
		});

		description.setValueChangeMode(ValueChangeMode.ON_BLUR);

		HasValue.ValueChangeListener<HasValue.ValueChangeEvent<LocalDate>> changer = (HasValue.ValueChangeEvent<LocalDate> event) -> {
			saveTask();
		};

		startDate.addValueChangeListener(changer);
		preferredEndDate.addValueChangeListener(changer);
		deadlineDate.addValueChangeListener(changer);
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException {
		if (taskID != null) {
			Task task = taskAndProject.getTask();
			Task.Project taskProject = taskAndProject.getProject();
			if (task != null) {
				name.setValue(task.name.stringValue());
				description.setValue(task.description.toString());
				startDate.setValue(asLocalDate(task.startDate.dateValue()));
				preferredEndDate.setValue(asLocalDate(task.preferredDate.dateValue()));
				deadlineDate.setValue(asLocalDate(task.finalDate.dateValue()));
				placeGrid.setTaskID(taskID);
				weblinkGrid.setTaskID(taskID);
				documentGrid.setTaskID(taskID);

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

					placeGrid.setReadOnly(true);
					weblinkGrid.setReadOnly(true);
					documentGrid.setReadOnly(true);

				} else {
					completeButton.setVisible(true);
					final Date now = new Date();
					if (task.startDate.dateValue() == null && task.finalDate.dateValue() == null) {
						oneDayMaybeIndicator.setVisible(true);
					} else if (task.finalDate.dateValue() != null && task.finalDate.dateValue().before(now)) {
						overdueIndicator.setVisible(true);
					} else if (task.startDate.dateValue() != null && task.startDate.dateValue().before(now)) {
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
				Globals.sqlerror(ex);
			}
			Globals.notice("Saved.");
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void handleEscapeButton() {
		Globals.showTask(taskID);
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

		public ReopenTaskListener(MinorTask minortask, Long taskID) {
			this.taskID = taskID;
		}

		@Override
		public void onComponentEvent(ClickEvent<Button> event) {
			List<Task> projectPathTasks = getProjectPathTasks(taskID);
			for (Task projectPathTask : projectPathTasks) {
				setCompletionDateToNull(projectPathTask);
			}
			Task task;
			try {
				task = getTask(taskID);
				setCompletionDateToNull(task);
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
			}
			Globals.showTask(taskID);
		}

		private void setCompletionDateToNull(Task projectPathTask) {
			projectPathTask.completionDate.setValue((Date) null);
			try {
				Globals.getDatabase().update(projectPathTask);
			} catch (SQLException ex) {
				Globals.sqlerror(ex);
			}
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
					Globals.showTask(null);
				} else {
					Long projectID = task.projectID.getValue();
					Task completedTasks = new Task();
					completedTasks.projectID.setValue(projectID);
					completedTasks.completionDate.excludeNull();
					try {
						if (getDatabase().getDBQuery(completedTasks).count() > 0) {
							Task openTasks = new Task();
							openTasks.projectID.setValue(projectID);
							openTasks.completionDate.permitOnlyNull();
							if (getDatabase().getDBQuery(openTasks).count() == 0){ 
								Globals.notice("All the subtasks are completed!");
							}
						}
					} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
						sqlerror(ex);
					}
					Globals.showTask(projectID);
				}
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		private Task completeTask(Long taskID) throws Globals.InaccessibleTaskException {
			if (taskID != null) {
				List<Task> subtasks = Globals.getActiveSubtasks(taskID, minortask().getUserID());
				for (Task subtask : subtasks) {
					completeTask(subtask.taskID.getValue());
				}
				Task task = getTask(taskID);
				task.completionDate.setValue(new Date());
				try {
					final DBDatabase database = Globals.getDatabase();
					DBActionList update = database.update(task);
					System.out.println(update.getSQL(database));
				} catch (SQLException ex) {
					Globals.sqlerror(ex);
				}
				return task;
			}
			return null;
		}
	}
}
