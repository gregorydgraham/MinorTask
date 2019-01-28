/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.google.common.base.Objects;
import nz.co.gregs.minortask.components.polymer.PaperInput;
import nz.co.gregs.minortask.place.PlaceGrid;
import nz.co.gregs.minortask.components.upload.DocumentGrid;
import nz.co.gregs.minortask.weblinks.WeblinkGrid;
import nz.co.gregs.minortask.components.tasklists.CompletedTaskList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.tasklists.OpenTaskList;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.components.upload.DocumentAddedEvent;
import nz.co.gregs.minortask.components.upload.DocumentUploadAndSelector;
import nz.co.gregs.minortask.components.upload.ImageUploadAndSelector;
import nz.co.gregs.minortask.components.upload.TaskDocumentLink;
import nz.co.gregs.minortask.datamodel.TaskViews;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.place.PlaceSearchComponent;
import nz.co.gregs.minortask.weblinks.WeblinkEditorComponent;
import org.joda.time.Period;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/edittask.css")
public class EditTask extends SecureDiv implements ProjectPathChanger {

	PaperInput name = new PaperInput();
	TextField user = new TextField("User");
	PaperInput description = new PaperInput();
	Label assignedToLabel = new Label("Assigned To ");
	UserSelector assignedToSelector = new UserSelector.ColleagueSelector();
	TextArea notes = new TextArea("Notes");
	AddTaskButton addSubTask = new AddTaskButton();
	SecureButton addDates = new SecureButton("Dates", new Icon(VaadinIcon.CALENDAR_O));
	SecureButton addRepeat = new SecureButton("Repeat", new Icon(VaadinIcon.TIME_FORWARD));
	SecureButton addPlace = new SecureButton("Place", new Icon(VaadinIcon.MAP_MARKER));
	SecureButton addImage = new SecureButton("Image", new Icon(VaadinIcon.FILE_PICTURE));
	SecureButton addDocument = new SecureButton("Document", new Icon(VaadinIcon.FILE_ADD));
	SecureButton addWebLink = new SecureButton("Bookmark", new Icon(VaadinIcon.BOOKMARK_O));
	SecureButton addNotes = new SecureButton("Notes", new Icon(VaadinIcon.NOTEBOOK));
	SecureButton completeButton = new SecureButton("Complete This Task");
	SecureButton reopenButton = new SecureButton("Reopen This Task");
	ProjectPicker project;
	OpenTaskList subtasks;
	CompletedTaskList completedTasks;
	OptionalDatePicker startDate = new OptionalDatePicker("Start Date");
	OptionalDatePicker preferredEndDate = new OptionalDatePicker("Reminder");
	OptionalDatePicker deadlineDate = new OptionalDatePicker("Deadline");
	OptionaDateRepeat repeatEditor = new OptionaDateRepeat("Repeat");
	OptionaDateRepeat repeat = new OptionaDateRepeat("Repeat");
	Period repeatValue = null;
	SecureDatePicker completedDate = new SecureDatePicker("Completed");
	private SecureDiv dates = new SecureDiv(
			startDate,
			preferredEndDate,
			deadlineDate,
			completedDate
	);
	WeblinkGrid weblinkGrid = new WeblinkGrid();
	WeblinkEditorComponent weblinkEditor = new WeblinkEditorComponent();
	TextArea notesEditor = new TextArea("Notes");
	SecureDiv notesEditorDiv = new SecureDiv(notesEditor);
	DocumentGrid documentGrid = new DocumentGrid();
	DocumentUploadAndSelector documentUpload;
	ImageUploadAndSelector imageUpload;
	PlaceGrid placeGrid = new PlaceGrid();
	PlaceSearchComponent placeSearcher = new PlaceSearchComponent();
	Label activeIndicator = new Label("Active");
	Label startedIndicator = new Label("Started");
	Label overdueIndicator = new Label("Overdue");
	Label oneDayMaybeIndicator = new Label("One Day Maybe");
	Label completedIndicator = new Label("COMPLETED");
	private final Long taskID;
	private Task.TaskAndProject taskAndProject;
	private SecureDiv nameDiv;
	private SecureDiv descriptionDiv;
	private SecureDiv notesDiv;
	private SecureDiv assignmentDiv;

	public EditTask(Long currentTask) {
		this.taskID = currentTask;
		try {

			taskAndProject = getTaskAndProject(taskID);
			project = new ProjectPicker(taskID);

			subtasks = new OpenTaskList(taskID);
			completedTasks = new CompletedTaskList(taskID);
			documentUpload = new DocumentUploadAndSelector(taskID);
			imageUpload = new ImageUploadAndSelector(taskID);
			add(currentTask != null ? getComponent() : new RootTaskComponent(taskID));
			addViewedDate(taskAndProject);
		} catch (Globals.InaccessibleTaskException ex) {
			add(new AccessDeniedComponent());
		}
		addClassName("edit-task-component");
	}

	public final Component getComponent() {

		name.setLabel("Task");
		name.addClassName("edit-task-name-input");
		nameDiv = new SecureDiv(name);

		description.setLabel("Description");
		description.addClassName("edit-task-description");
		descriptionDiv = new SecureDiv(description);
		descriptionDiv.addClassName("edit-task-description");

		notes.addClassName("edit-task-notes");
		notesEditor.addClassName("edit-task-notes");
		notesDiv = new SecureDiv(notes);

		assignedToLabel.addClassName("edit-task-assignedto-label");
		assignedToSelector.addClassName("edit-task-assignedto-selector");
		assignmentDiv = new SecureDiv(assignedToLabel, assignedToSelector);

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
		completeButton.addClickListener((event) -> {
			minortask().completeTaskWithCongratulations(taskAndProject.getTask());
			Globals.showTask(taskAndProject.getProject().taskID.getValue());
		});
		completeButton.setVisible(false);
		Div completeButtonDiv = new Div(completeButton);
		completeButtonDiv.addClassName("edit-task-complete-button-container");

		reopenButton.addClassNames("friendly", "edit-task-reopenbutton");
		reopenButton.addClickListener((event) -> {
			minortask().reopenTask(taskAndProject.getTask());
			refresh();
		});
		reopenButton.setVisible(false);

		completedIndicator.getStyle().set("padding", "0").set("margin-left", "0").set("margin-right", "0").set("margin-bottom", "0");
		Div completedLayout = new Div(completedIndicator, reopenButton);
		completedLayout.addClassName("completedindicator-container");

		Div details = new Div(
				activeIndicator, startedIndicator, overdueIndicator, completedLayout);
		details.addClassName("statusindicators-container");
		details.setSizeUndefined();

		dates.addClassName("dates-component");
		dates.setSizeUndefined();

		addSubTask.addClassName("friendly");
		addDates.addClassName("edit-task-button");
		addRepeat.addClassName("edit-task-button");
		addDocument.addClassName("edit-task-button");
		addImage.addClassName("edit-task-button");
		addPlace.addClassName("edit-task-button");
		addWebLink.addClassName("edit-task-button");
		addNotes.addClassName("edit-task-button");
		final Div addButtons = new Div();
		addButtons.add(
				addSubTask,
				addDates,
				addRepeat,
				addDocument,
				addImage,
				addPlace,
				addWebLink,
				addNotes
		);
		addButtons.addClassName("edit-task-addbuttons");

		Div extrasLayout = new Div();
		extrasLayout.add(notesDiv);
		extrasLayout.add(placeGrid);
		extrasLayout.add(documentGrid);
		extrasLayout.add(weblinkGrid);

		final Div nameAndProjectDiv = new Div(nameDiv, project);
		nameAndProjectDiv.addClassName("edit-task-name");

		Div topLayout = new Div(
				nameAndProjectDiv,
				descriptionDiv,
				assignmentDiv,
				addButtons,
				repeatEditor,
				placeSearcher,
				weblinkEditor,
				notesEditorDiv,
				documentUpload,
				imageUpload,
				repeat,
				dates,
				subtasks,
				extrasLayout,
				Globals.getSpacer(),
				completeButtonDiv,
				reopenButton,
				Globals.getSpacer(),
				completedTasks);
		topLayout.addClassName("edit-task-contents");

		addToolTips();

		try {
			setFieldValues();
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			Globals.sqlerror(ex);
		}
		return topLayout;
	}

	private void addToolTips() {
		nameDiv.setTooltipText("Label the task so that you can recognise and find it easily");
		descriptionDiv.setTooltipText("Add more context to the task name, displayed below the task's name in lists");
		notesEditorDiv.setTooltipText("Add random text including your thoughts, feedback, and findings or anything else you'd like to kepp");
		notesDiv.setTooltipText("Fill this with any findings, thoughts, or conclusions you like, it'll take all the text you can write");
		
		assignmentDiv.setTooltipText("Ask someone else to do this task, note that they can refuse");
		
		addDates.setTooltipText("Add dates to have the task appear on the Today's Task list when appropriate");
		addDocument.setTooltipText("Collect relevant files and documents");
		addImage.setTooltipText("Add images that support this task");
		addNotes.setTooltipText("Write everything you need to help with this task");
		addPlace.setTooltipText("Include any locations that are relevant");
		addRepeat.setTooltipText("Have a new version of this task automatically created when you complete the current one, great for those regular jobs");
		addWebLink.setTooltipText("Collect all the websites you need");
		
		startDate.setTooltipText("Start date defines when the task will start appearing in the Today's Tasks list, and removes it from the Ideas list.");
		preferredEndDate.setTooltipText("The date when you would like to finish the task");
		deadlineDate.setTooltipText("The task MUST be finished on or before this date.  Deadlines will push the task higher up the Today's Task list");
		
		completeButton.setTooltipText("When you're happy it's done press this button");
	}

	@Override
	public Registration addProjectPathAlteredListener(
			ComponentEventListener<ProjectPathAltered> listener) {
		return addListener(ProjectPathAltered.class, listener);
	}

	protected void addChangeListeners(Task task) {
		name.addBlurListener((event) -> {
			try {
				final Task task1 = getTask(taskID);
				if (!event.getSource().getValue().equals(task1.name.getValue())) {
					saveTask();
					fireEvent(new ProjectPathAltered(this, task1, false));
				}
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		description.addBlurListener((event) -> {
			try {
				final Task task1 = getTask(taskID);
				if (!event.getSource().getValue().equals(task1.description.getValue())) {
					saveTask();
				}
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		assignedToSelector.addValueChangeListener((event) -> {
			try {
				final Task task1 = getTask(taskID);
				final User sourceValue = event.getSource().getValue();
				final Long sourceUserID = sourceValue.getUserID();
				if (sourceValue == null || sourceUserID == null) {
					if (task1.assigneeID.isNotNull()) {
						saveTask();
					}
				} else if (!sourceUserID.equals(task1.assigneeID.getValue())) {
					saveTask();
				}
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		notes.addBlurListener((event) -> {
			try {
				final Task task1 = getTask(taskID);
				if (!notes.getValue().equals(task1.notes.stringValue())) {
					notesEditor.setValue(notes.getValue());
					saveTask();
				}
				notes.setVisible(!notes.getValue().isEmpty());
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(EditTask.class.getName()).log(Level.SEVERE, null, ex);
			}
		});
		notesEditor.addBlurListener((event) -> {
			try {
				final Task task1 = getTask(taskID);
				if (!notesEditor.getValue().equals(task1.notes.stringValue())) {
					notes.setValue(notesEditor.getValue());
					notes.setVisible(!notes.getValue().isEmpty());
					saveTask();
				}
			} catch (Globals.InaccessibleTaskException ex) {
				error("Inaccessible Task " + taskID, ex);
			}
			showEditor(null);
		});

		HasValue.ValueChangeListener<HasValue.ValueChangeEvent<LocalDate>> changer = (HasValue.ValueChangeEvent<LocalDate> event) -> {
			saveTask();
		};

		startDate.addValueChangeListener(changer);
		preferredEndDate.addValueChangeListener(changer);
		deadlineDate.addValueChangeListener(changer);

		repeatEditor.addValueChangeListener((event) -> {
			repeatValue = repeatEditor.getValue();
			saveTask();
		});

		repeat.addValueChangeListener((event) -> {
			repeatValue = repeat.getValue();
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
			insertLinkToDocument(event);
			documentGrid.refresh();
			showEditor(null);
		});
		addImage.addClickListener((event) -> {
			showEditor(imageUpload);
		});
		imageUpload.addDocumentAddedListener((event) -> {
			insertLinkToDocument(event);
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
		addNotes.addClickListener((event) -> {
			showEditor(notesEditorDiv);
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
				notesEditor.setValue(task.notes.stringValue());
				startDate.setValue(asLocalDate(task.startDate.dateValue()));
				preferredEndDate.setValue(asLocalDate(task.preferredDate.dateValue()));
				deadlineDate.setValue(asLocalDate(task.finalDate.dateValue()));
				repeat.setValue(task.repeatOffset.getValue());
				repeatEditor.setValue(task.repeatOffset.getValue());

				notes.setVisible(!notes.getValue().isEmpty());

				if (task.assigneeID.isNotNull()) {
					if (task.getAssigneeUser() != null) {
						assignedToSelector.setValue(task.getAssigneeUser());
					} else {
						assignedToSelector.setValue(getUser(task.assigneeID.getValue()));
					}
				} else {
					assignedToSelector.setValue(assignedToSelector.getEmptyValue());
				}

				if (startDate.isEmpty() && preferredEndDate.isEmpty() && deadlineDate.isEmpty()) {
					dates.setVisible(false);
				} else {
					addDates.setVisible(false);
				}
				if (task.repeatOffset.isNotNull()) {
					repeat.setVisible(true);
					addRepeat.setVisible(false);
				} else {
					repeat.setVisible(false);
					addRepeat.setVisible(true);
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
					completedDate.setVisible(true);
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
					repeat.setReadOnly(true);
					subtasks.disableNewButton();

					placeGrid.setReadOnly(true);
					weblinkGrid.setReadOnly(true);
					documentGrid.setReadOnly(true);

				} else {
					completedDate.setVisible(false);
					completeButton.setVisible(true);
					reopenButton.setVisible(false);
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
			if (assignedToSelector.getValue() == null
					|| Objects.equal(assignedToSelector.getEmptyValue(), assignedToSelector.getValue())) {
				task.assigneeID.setValueToNull();
			} else {
				task.assigneeID.setValue(assignedToSelector.getValue().getUserID());
			}
			task.notes.setValue(notes.getValue());
			task.startDate.setValue(asDate(startDate.getValue()));
			task.preferredDate.setValue(asDate(preferredEndDate.getValue()));
			task.finalDate.setValue(asDate(deadlineDate.getValue()));
			task.repeatOffset.setValue(repeatValue);

			try {
				getDatabase().update(task);
			} catch (SQLException ex) {
				Logger.getLogger(CreateTask.class.getName()).log(Level.SEVERE, null, ex);
				Globals.sqlerror(ex);
			}
			Globals.savedNotice();
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
		notesEditorDiv.setVisible(false);
		if (editor != null) {
			editor.setVisible(!editorAlreadyShowing);
		}
	}

	private void insertLinkToDocument(DocumentAddedEvent event) {
		if (event.getValue() != null) {
			chat("Adding document link..." + event.getSource().getClass().getSimpleName());
			TaskDocumentLink link = new TaskDocumentLink();
			link.documentID.setValue(event.getValue().documentID);
			link.taskID.setValue(taskID);
			link.ownerID.setValue(getUserID());
			try {
				getDatabase().insert(link);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		} else {
			chat("No Document Found!!: " + event.getSource().getClass().getSimpleName());
		}
	}

	private void addViewedDate(Task.TaskAndProject taskAndProject) {
		TaskViews taskView = new TaskViews();
		taskView.taskID.setValue(taskAndProject.getTask().taskID);
		taskView.userID.setValue(getUserID());
		taskView.lastviewed.setValue(new Date());
		try {
			getDatabase().insert(taskView);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void refresh() {
		try {
			setFieldValues();
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			sqlerror(ex);
		}
	}
}
