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
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.actions.DBActionList;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.tasklists.OpenTaskList;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.documentupload.DocumentUpload;
import nz.co.gregs.minortask.documentupload.ImageUpload;
import nz.co.gregs.minortask.place.PlaceSearchComponent;
import nz.co.gregs.minortask.weblinks.WeblinkEditorComponent;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 *
 * @author gregorygraham
 */
public class EditTask extends Div implements RequiresLogin {

	PaperInput name = new PaperInput();
	TextField user = new TextField("User");
	PaperInput description = new PaperInput();
	AddTaskButton addSubTask = new AddTaskButton();
	Button addDates = new Button("Dates", new Icon(VaadinIcon.CALENDAR_O));
	Button addRepeat = new Button("Repeat", new Icon(VaadinIcon.TIME_FORWARD));
	Button addPlace = new Button("Place", new Icon(VaadinIcon.MAP_MARKER));
	Button addImage = new Button("Image", new Icon(VaadinIcon.FILE_PICTURE));
	Button addDocument = new Button("Document", new Icon(VaadinIcon.FILE_ADD));
	Button addWebLink = new Button("Bookmark", new Icon(VaadinIcon.BOOKMARK_O));
	Button completeButton = new Button("Complete This Task");
	Button reopenButton = new Button("Reopen This Task");
	ProjectPicker project;
	OpenTaskList subtasks;
	CompletedTaskList completedTasks;
	TextArea notes = new TextArea("Notes");
	OptionalDatePicker startDate = new OptionalDatePicker("Start Date");
	OptionalDatePicker preferredEndDate = new OptionalDatePicker("Reminder");
	OptionalDatePicker deadlineDate = new OptionalDatePicker("Deadline");
	OptionaDateRepeat repeatEditor = new OptionaDateRepeat("Repeat");
	DatePicker completedDate = new DatePicker("Completed");
	private Div dates = new Div(
			startDate,
			preferredEndDate,
			deadlineDate,
			completedDate
	);
	WeblinkGrid weblinkGrid = new WeblinkGrid();
	WeblinkEditorComponent weblinkEditor = new WeblinkEditorComponent();
	DocumentGrid documentGrid = new DocumentGrid();
	DocumentUpload documentUpload = new DocumentUpload();
	ImageUpload imageUpload = new ImageUpload();
	PlaceGrid placeGrid = new PlaceGrid();
	PlaceSearchComponent placeSearcher = new PlaceSearchComponent();
	Label activeIndicator = new Label("Active");
	Label startedIndicator = new Label("Started");
	Label overdueIndicator = new Label("Overdue");
	Label oneDayMaybeIndicator = new Label("One Day Maybe");
	Label completedIndicator = new Label("COMPLETED");
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

		name.addClassName("edit-task-name-input");
		description.addClassName("edit-task-description");
		notes.addClassName("edit-task-notes");

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

		dates.addClassName("dates-component");
		dates.setSizeUndefined();

		ProjectPathNavigator.WithAddTaskButton projectPath = new ProjectPathNavigator.WithAddTaskButton(taskID);

		addSubTask.addClassName("friendly");
		addDates.addClassName("friendly");
		addRepeat.addClassName("friendly");
		addDocument.addClassName("friendly");
		addImage.addClassName("friendly");
		addPlace.addClassName("friendly");
		addWebLink.addClassName("friendly");
		final Div addButtons = new Div();
		addButtons.add(
				addSubTask,
				addDates,
				addRepeat,
				addDocument,
				addImage,
				addPlace,
				addWebLink
		);
		addButtons.addClassName("edit-task-addbuttons");

		Div extrasLayout = new Div();
		extrasLayout.add(notes);
		extrasLayout.add(placeGrid);
		extrasLayout.add(documentGrid);
		extrasLayout.add(weblinkGrid);

		final Div nameDiv = new Div(name, project);
		nameDiv.addClassName("edit-task-name");

		Div topLayout = new Div(
				projectPath,
				nameDiv,
				description,
				addButtons,
				repeatEditor,
				placeSearcher,
				weblinkEditor,
				documentUpload,
				imageUpload,
				dates,
				subtasks,
				extrasLayout,
				Globals.getSpacer(),
				completeButton,
				reopenButton,
				Globals.getSpacer(),
				completedTasks);
		topLayout.addClassName("edit-task-contents");
		try {
			setFieldValues();
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			Globals.sqlerror(ex);
		}
		return topLayout;
	}

	protected void addChangeListeners(Task task) {
		name.addBlurListener((event) -> {
			if (!event.getSource().getValue().equals(task.name.getValue())) {
				saveTask();
				Globals.showTask(taskID);
			}
		});
		description.addBlurListener((event) -> {
			if (!event.getSource().getValue().equals(task.description.getValue())) {
				saveTask();
			}
		});
		notes.addValueChangeListener((event) -> {
			saveTask();
		});

		HasValue.ValueChangeListener<HasValue.ValueChangeEvent<LocalDate>> changer = (HasValue.ValueChangeEvent<LocalDate> event) -> {
			saveTask();
		};

		startDate.addValueChangeListener(changer);
		preferredEndDate.addValueChangeListener(changer);
		deadlineDate.addValueChangeListener(changer);

		repeatEditor.addValueChangeListener((event) -> {
			saveTask();
		});

		addDates.addClickListener((event) -> {
			showEditor(dates);
		});
		addRepeat.addClickListener((event) -> {
			showEditor(repeatEditor);
		});
		addDocument.addClickListener((event) -> {
			showEditor(documentUpload);
		});
		documentUpload.addDocumentAddedListener((event) -> {
			documentGrid.refresh();
			showEditor(null);
		});
		addImage.addClickListener((event) -> {
			showEditor(imageUpload);
		});
		imageUpload.addDocumentAddedListener((event) -> {
			documentGrid.refresh();
			showEditor(null);
		});
		addPlace.addClickListener((event) -> {
			showEditor(placeSearcher);
		});
		placeSearcher.addLocationAddedListener((event) -> {
			placeGrid.refresh();
			showEditor(null);
		});
		addWebLink.addClickListener((event) -> {
			showEditor(weblinkEditor);
		});
		weblinkEditor.addWeblinkAddedListener((event) -> {
			weblinkGrid.refresh();
			showEditor(null);
		});
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException {
		if (taskID != null) {
			Task task = taskAndProject.getTask();
			Task.Project taskProject = taskAndProject.getProject();
			if (task != null) {
				name.setValue(task.name.stringValue());
				description.setValue(task.description.toString());
				notes.setValue(task.notes.stringValue());
				startDate.setValue(asLocalDate(task.startDate.dateValue()));
				preferredEndDate.setValue(asLocalDate(task.preferredDate.dateValue()));
				deadlineDate.setValue(asLocalDate(task.finalDate.dateValue()));
				repeatEditor.setValue(task.repeatOffset.getValue());

				if (startDate.isEmpty() && preferredEndDate.isEmpty() && deadlineDate.isEmpty()) {
					dates.setVisible(false);
				} else {
					addDates.setVisible(false);
				}
				addSubTask.setTaskID(taskID);
				documentUpload.setTaskID(taskID);
				imageUpload.setTaskID(taskID);
				placeGrid.setTaskID(taskID);
				placeSearcher.setTaskID(taskID);
				weblinkGrid.setTaskID(taskID);
				weblinkEditor.setTaskID(taskID);
				documentGrid.setTaskID(taskID);

				showEditor(null);

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
					notes.setReadOnly(true);
					startDate.setReadOnly(true);
					preferredEndDate.setReadOnly(true);
					deadlineDate.setReadOnly(true);
					repeatEditor.setReadOnly(true);
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

				addChangeListeners(task);
			}
		}
	}

	public void saveTask() {
		try {
			Task task = getTask(taskID);

			task.name.setValue(name.getValue());
			task.description.setValue(description.getValue());
			task.notes.setValue(notes.getValue());
			task.startDate.setValue(asDate(startDate.getValue()));
			task.preferredDate.setValue(asDate(preferredEndDate.getValue()));
			task.finalDate.setValue(asDate(deadlineDate.getValue()));
			task.repeatOffset.setValue(repeatEditor.getValue());

			try {
				getDatabase().update(task);
			} catch (SQLException ex) {
				Logger.getLogger(CreateTask.class.getName()).log(Level.SEVERE, null, ex);
				Globals.sqlerror(ex);
			}
			Globals.notice(new Icon(VaadinIcon.SAFE), "Saved.");
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

	private void showEditor(Component editor) {
		boolean editorAlreadyShowing = editor == null ? false : editor.isVisible();
		if (startDate.isEmpty() && preferredEndDate.isEmpty() && deadlineDate.isEmpty()) {
			dates.setVisible(false);
		}
		repeatEditor.setVisible(false);
		documentUpload.setVisible(false);
		imageUpload.setVisible(false);
		placeSearcher.setVisible(false);
		weblinkEditor.setVisible(false);
		if (editor != null) {
			editor.setVisible(!editorAlreadyShowing);
		}
	}

	private class ReopenTaskListener implements ComponentEventListener<ClickEvent<Button>> {

		private final Long taskID;

		public ReopenTaskListener(MinorTask minortask, Long taskID) {
			this.taskID = taskID;
		}

		@Override
		public void onComponentEvent(ClickEvent<Button> event) {
			List<Task> projectPathTasks = getProjectPathTasks(taskID);
			projectPathTasks.forEach((projectPathTask) -> {
				setCompletionDateToNull(projectPathTask);
			});
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
							if (getDatabase().getDBQuery(openTasks).count() == 0) {
								Globals.congratulate("All the subtasks are completed!");
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
					repeatTask(task);
				} catch (SQLException ex) {
					Globals.sqlerror(ex);
				}
				return task;
			}
			return null;
		}

		private void repeatTask(Task task) {
			if (task.repeatOffset.isNotNull()) {
				Period value = task.repeatOffset.getValue();
				final Date now = new Date();
				final Date startDateValue = task.startDate.getValue();
				if (startDateValue != null
						&& (startDateValue.before(now))) {
					Period period = new Period(now.getTime() - startDateValue.getTime(), (Chronology) null);
					value = value.plus(period);
				}
				Task copy = DBRow.copyDBRow(task);
				copy.taskID.setValueToNull();
				copy.completionDate.setValueToNull();
				copy.startDate.setValue(offsetDate(copy.startDate.getValue(), value));
				copy.preferredDate.setValue(offsetDate(copy.preferredDate.getValue(), value));
				copy.finalDate.setValue(offsetDate(copy.finalDate.getValue(), value));
				try {
					getDatabase().insert(copy);
				} catch (SQLException ex) {
					sqlerror(ex);
				}
			}
		}

		private Date offsetDate(final Date originalDate, Period value) {
			if (originalDate != null) {
				Date newDate = new DateTime(originalDate.getTime()).plus(value).toDate();
				return newDate;
			} else {
				return null;
			}
		}
	}
}
