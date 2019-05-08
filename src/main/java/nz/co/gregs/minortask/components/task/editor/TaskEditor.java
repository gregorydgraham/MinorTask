/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

import com.google.common.base.Objects;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.ClarityAndProgress;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;
import nz.co.gregs.minortask.components.OptionaDateOnlyRepeat;
import nz.co.gregs.minortask.components.OptionalDatePicker;
import nz.co.gregs.minortask.components.SecureButton;
import nz.co.gregs.minortask.components.SecureDatePicker;
import nz.co.gregs.minortask.components.UserSelector;
import nz.co.gregs.minortask.components.changes.Changes;
import nz.co.gregs.minortask.components.generic.FlexBox;
import nz.co.gregs.minortask.components.generic.FlexColumn;
import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.components.polymer.Details;
import nz.co.gregs.minortask.components.polymer.PaperInput;
import nz.co.gregs.minortask.components.task.HasTaskAndProject;
import nz.co.gregs.minortask.components.tasklists.CompletedTasksList;
import nz.co.gregs.minortask.components.tasklists.OpenTaskList;
import nz.co.gregs.minortask.components.upload.DocumentAddedEvent;
import nz.co.gregs.minortask.components.upload.DocumentGrid;
import nz.co.gregs.minortask.components.upload.DocumentUploadAndSelector;
import nz.co.gregs.minortask.components.upload.ImageUploadAndSelector;
import nz.co.gregs.minortask.components.upload.TaskDocumentLink;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.TaskViews;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.place.PlaceGrid;
import nz.co.gregs.minortask.place.PlaceSearchComponent;
import nz.co.gregs.minortask.weblinks.WeblinkEditorComponent;
import nz.co.gregs.minortask.weblinks.WeblinkGrid;
import org.joda.time.Period;

/**
 *
 * @author gregorygraham
 */
public class TaskEditor extends FlexBox implements HasTaskAndProject, MinorTaskEventListener, MinorTaskEventNotifier {

	private final PaperInput name = new PaperInput();
	private final TextField ownerField = new TextField("User");
	private final TextArea description = new TextArea();
	private final UserSelector assignedToSelector = new UserSelector.ColleagueSelector("Assigned To");
	private final TextArea notes = new TextArea("Notes");
	private final SecureButton addDates = new SecureButton("Dates", new Icon(VaadinIcon.CALENDAR_O));
	private final SecureButton addRepeat = new SecureButton("Repeat", new Icon(VaadinIcon.TIME_FORWARD));
	private final SecureButton addPlace = new SecureButton("Place", new Icon(VaadinIcon.MAP_MARKER));
	private final SecureButton addImage = new SecureButton("Image", new Icon(VaadinIcon.FILE_PICTURE));
	private final SecureButton addDocument = new SecureButton("Document", new Icon(VaadinIcon.FILE_ADD));
	private final SecureButton addWebLink = new SecureButton("Bookmark", new Icon(VaadinIcon.BOOKMARK_O));
	private final SecureButton addNotes = new SecureButton("Notes", new Icon(VaadinIcon.NOTEBOOK));
	private final SecureButton completeButton = new SecureButton("Complete This Task");
	private final SecureButton reopenButton = new SecureButton("Reopen This Task");
	private final SecureButton deleteButton = new SecureButton("Delete Completely");
	private final TaskProjectPicker project = new TaskProjectPicker();
	private final OpenTaskList subtasks = new OpenTaskList();
	private final CompletedTasksList completedTasks = new CompletedTasksList();
	private final OptionalDatePicker startDate = new OptionalDatePicker("Start Date");
	private final OptionalDatePicker preferredEndDate = new OptionalDatePicker("Reminder");
	private final OptionalDatePicker deadlineDate = new OptionalDatePicker("Deadline Date");
	private final OptionaDateOnlyRepeat repeatEditor = new OptionaDateOnlyRepeat("Repeat");
	private final OptionaDateOnlyRepeat repeat = new OptionaDateOnlyRepeat("Repeat");
	private Period repeatValue = null;
	private final SecureDatePicker completedDate = new SecureDatePicker("Completed");
	private final FlexBox dates = new FlexBox(
			startDate,
			preferredEndDate,
			deadlineDate,
			completedDate
	);
	private final WeblinkGrid weblinkGrid = new WeblinkGrid();
	private final WeblinkEditorComponent weblinkEditor = new WeblinkEditorComponent();
	private final TextArea notesEditor = new TextArea("Notes");
	private final SecureDiv notesEditorDiv = new SecureDiv(notesEditor);
	private final DocumentGrid documentGrid = new DocumentGrid();
	private final DocumentUploadAndSelector documentUpload = new DocumentUploadAndSelector();
	private final ImageUploadAndSelector imageUpload = new ImageUploadAndSelector();
	private final PlaceGrid placeGrid = new PlaceGrid();
	private final PlaceSearchComponent placeSearcher = new PlaceSearchComponent();
	private final Label activeIndicator = new Label("Active");
	private final Label startedIndicator = new Label("Started");
	private final Label overdueIndicator = new Label("Overdue");
	private final Label oneDayMaybeIndicator = new Label("One Day Maybe");
	private final Label completedIndicator = new Label("COMPLETED");
	private Task.TaskAndProject taskAndProject;
	private final SecureDiv nameDiv = new SecureDiv();
	private final SecureDiv descriptionDiv = new SecureDiv();
	private final SecureDiv notesDiv = new SecureDiv();
	private final SecureDiv addButtons = new SecureDiv();
	private final Div buttonsAndEditors = new Div();

	private boolean refreshing = false;
	private final Div completeButtonDiv = new Div();
	private final SecureDiv nameAndProjectDiv = new SecureDiv();
	private final FlexColumn extrasLayout = new FlexColumn();
	private final FlexColumn tasksDiv = new FlexColumn();

	public TaskEditor() {
		initComponents();

		this.add(
				Globals.getSpacer(),
				new Div(
						nameAndProjectDiv,
						buttonsAndEditors,
						extrasLayout,
						tasksDiv),
				Globals.getSpacer()
		);
		addClassName("edit-task-contents");
		addToolTips();
		addChangeListeners();
	}

	private void initComponents() {
		name.setLabel("Task");
		name.addClassName("edit-task-name-input");
		nameDiv.add(name);

		ownerField.setEnabled(false);
		ownerField.setLabel("Owner");

		description.setLabel("Description");
		description.addClassName("edit-task-description");
		descriptionDiv.add(description);
		descriptionDiv.addClassName("edit-task-description");

		notes.addClassName("edit-task-notes");
		notesEditor.addClassName("edit-task-notes");
		notesDiv.add(notes);

		assignedToSelector.addClassName("edit-task-assignedto-selector");

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
			fireEvent(new MinorTaskEvent(this, taskAndProject.getProject(), false));
		});
		completeButton.setVisible(false);

		reopenButton.addClassNames("friendly", "edit-task-reopenbutton");
		reopenButton.addClickListener((event) -> {
			minortask().reopenTask(taskAndProject.getTask());
			fireEvent(new MinorTaskEvent(this, taskAndProject.getProject(), false));
		});
		reopenButton.setVisible(false);

		deleteButton.addClassNames("danger", "edit-task-deletebutton");
		deleteButton.addClickListener((event) -> {
			Long oldTaskID = taskAndProject.getTask().taskID.getValue();
			try {
				minortask().deleteTask(getTask(oldTaskID));
			} catch (Globals.InaccessibleTaskException ex) {
				error("You Can't Delete That Task", ex);
			}
			fireEvent(new MinorTaskEvent(this, taskAndProject.getProject(), false));
		});
		deleteButton.setVisible(false);

		completeButtonDiv.add(reopenButton, deleteButton, completeButton);
		completeButtonDiv.addClassName("edit-task-complete-button-container");

		dates.addClassName("edit-task-dates");
		dates.setSizeUndefined();

		addDates.addClassName("edit-task-button");
		addRepeat.addClassName("edit-task-button");
		addDocument.addClassName("edit-task-button");
		addImage.addClassName("edit-task-button");
		addPlace.addClassName("edit-task-button");
		addWebLink.addClassName("edit-task-button");
		addNotes.addClassName("edit-task-button");
		addButtons.add(
				addDates,
				addRepeat,
				addDocument,
				addImage,
				addPlace,
				addWebLink,
				addNotes
		);
		addButtons.addClassName("edit-task-addbuttons");

		buttonsAndEditors.removeAll();
		buttonsAndEditors.addClassName("edit-task-editors");
		buttonsAndEditors.add(
				addButtons,
				repeatEditor,
				placeSearcher,
				weblinkEditor,
				notesEditorDiv,
				documentUpload,
				imageUpload);

		project.addMinorTaskEventListener(this);
		subtasks.addMinorTaskEventListener(this);
		completedTasks.addMinorTaskEventListener(this);

		extrasLayout.add(repeat, dates, notesDiv, placeGrid, documentGrid, weblinkGrid);

		final ClarityAndProgress clarity = new ClarityAndProgress(getTask());

		Label clarityLabel = new Label("Clarity: " + clarity.getDeltaClarity() + " Progress: " + clarity.getDeltaProgress());

		final SecureDiv nameAndDescriptionDiv = new SecureDiv(nameDiv, descriptionDiv, clarityLabel);
		nameAndDescriptionDiv.addClassName("edit-task-nameanddescription");

		final SecureDiv projectAndAssignmentDiv = new SecureDiv();
		projectAndAssignmentDiv.add(ownerField, project, assignedToSelector);
		projectAndAssignmentDiv.addClassName("edit-task-projectandassignment");

		final Details projectAndAssignmentDetails = new Details("Ownership & Assignment");
		projectAndAssignmentDetails.add(projectAndAssignmentDiv);

		nameAndProjectDiv.add(nameAndDescriptionDiv, projectAndAssignmentDetails);
		nameAndProjectDiv.addClassName("edit-task-nameandproject");

		tasksDiv.add(
				subtasks,
				Globals.getSpacer(),
				completeButtonDiv,
				Globals.getSpacer(),
				completedTasks
		);
		tasksDiv.addClassName("tasksdiv");
	}

	private void addToolTips() {
		nameDiv.setTooltipText("Label the task so that you can recognise and find it easily");
		descriptionDiv.setTooltipText("Add more context to the task name, displayed below the task's name in lists");
		notesEditorDiv.setTooltipText("Add random text including your thoughts, feedback, and findings or anything else you'd like to kepp");
		notesDiv.setTooltipText("Fill this with any findings, thoughts, or conclusions you like, it'll take all the text you can write");

		assignedToSelector.setTooltipText("Ask someone else to do this task, note that they can refuse");

		addDates.setTooltipText("Add dates to have the task appear on the Today's Task list when appropriate");
		addRepeat.setTooltipText("Have a new version of this task automatically created when you complete the current one, great for those regular jobs");
		addDocument.setTooltipText("Collect relevant files and documents");
		addImage.setTooltipText("Add images that support this task");
		addNotes.setTooltipText("Write everything you need to help with this task");
		addPlace.setTooltipText("Include any locations that are relevant");
		addWebLink.setTooltipText("Collect all the websites you need");

		startDate.setTooltipText("Start date defines when the task will start appearing in the Today's Tasks list, and removes it from the Ideas list.");
		preferredEndDate.setTooltipText("The date when you would like to finish the task");
		deadlineDate.setTooltipText("The task MUST be finished on or before this date.  Deadlines will push the task higher up the Today's Task list");

		completeButton.setTooltipText("When you're happy it's done press this button");
	}

	protected void addChangeListeners() {
		name.addBlurListener((event) -> {
			checkAndSaveName(event);
		});
		description.addBlurListener((event) -> {
			checkAndSaveDescription(event);
		});
		assignedToSelector.addValueChangeListener((event) -> {
			checkAndSaveAssignee(event);
		});
		notes.addBlurListener((event) -> {
			checkAndSaveNotes();
		});
		notesEditor.addBlurListener((event) -> {
			checkAndSaveNotesEditor();
		});

		HasValue.ValueChangeListener<HasValue.ValueChangeEvent<LocalDate>> changer = (HasValue.ValueChangeEvent<LocalDate> event) -> {
			saveTask();
		};

		startDate.addValueChangeListener(changer);
		preferredEndDate.addValueChangeListener(changer);
		deadlineDate.addValueChangeListener(changer);

		repeat.addValueChangeListener((event) -> {
			repeatValue = repeat.getValue();
			saveTask();
		});

		addDates.addClickListener((event) -> {
			showEditor(dates);
		});
		addRepeat.addClickListener((event) -> {
			System.out.println("ADD REPEAT CLICKED");
			showEditor(repeatEditor);
		});
		repeatEditor.addValueChangeListener((event) -> {
			showEditor(null);
			repeatValue = repeatEditor.getValue();
			saveTask();
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
		placeSearcher.addPlaceAddedListener((event) -> {
			placeGrid.refresh();
			showEditor(null);
		});
		placeGrid.addPlaceRemovedListener((event) -> {
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

	private void checkAndSaveName(BlurNotifier.BlurEvent<PaperInput> event) {
		if (!refreshing) {
			try {
				final Task task1 = getTask(getTaskID());
				if (!event.getSource().getValue().equals(task1.name.getValue())) {
					saveTask();
				}
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(MinorTaskView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void checkAndSaveDescription(BlurNotifier.BlurEvent<TextArea> event) {
		if (!refreshing) {
			try {
				final Task task1 = getTask(getTaskID());
				if (!event.getSource().getValue().equals(task1.description.getValue())) {
					saveTask();
				}
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(MinorTaskView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void checkAndSaveNotesEditor() {
		if (!refreshing) {
			try {
				final Task task1 = getTask(getTaskID());
				if (!notesEditor.getValue().equals(task1.notes.stringValue())) {
					notes.setValue(notesEditor.getValue());
					notes.setVisible(!notes.getValue().isEmpty());
					saveTask();
				}
			} catch (Globals.InaccessibleTaskException ex) {
				error("Inaccessible Task " + getTaskID(), ex);
			}
			showEditor(null);
		}
	}

	private void checkAndSaveNotes() {
		if (!refreshing) {
			try {
				final Task task1 = getTask(getTaskID());
				if (!notes.getValue().equals(task1.notes.stringValue())) {
					notesEditor.setValue(notes.getValue());
					saveTask();
				}
				notes.setVisible(!notes.getValue().isEmpty());
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(MinorTaskView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void checkAndSaveAssignee(AbstractField.ComponentValueChangeEvent<ComboBox<User>, User> event) {
		if (!refreshing) {
			try {
				final Task task1 = getTask(getTaskID());
				final User sourceValue = event.getSource().getValue();
				if (sourceValue != null) {
					final Long sourceUserID = sourceValue.getUserID();
					if (sourceUserID == null) {
						if (task1.assigneeID.isNotNull()) {
							saveTask();
						}
					} else if (!sourceUserID.equals(task1.assigneeID.getValue())) {
						saveTask();
					}
				} else {
					if (task1.assigneeID.isNotNull()) {
						saveTask();
					}
				}
			} catch (Globals.InaccessibleTaskException ex) {
				Logger.getLogger(MinorTaskView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public void setFieldValues() {
		final Long currentTaskId = getTaskID();
		Task task = taskAndProject.getTask();
		project.setTask(taskAndProject);
		subtasks.setTask(task);
		completedTasks.setTask(task);
		if (currentTaskId != null && task != null) {
			Task.Project taskProject = taskAndProject.getProject();

			nameAndProjectDiv.setVisible(true);
			buttonsAndEditors.setVisible(true);
			extrasLayout.setVisible(true);
			completeButtonDiv.setVisible(true);

			final User owner = taskAndProject.getTask().getOwner();
			if (owner != null) {
				ownerField.setValue(owner.getUsername());
			}
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
				addDates.setVisible(true);
			} else {
				dates.setVisible(true);
				addDates.setVisible(false);
			}
			if (task.repeatOffset.isNotNull()) {
				repeat.setVisible(true);
				addRepeat.setVisible(false);
			} else {
				repeat.setVisible(false);
				addRepeat.setVisible(true);
			}
			documentUpload.setTaskID(currentTaskId);
			imageUpload.setTaskID(currentTaskId);
			placeGrid.setTaskID(currentTaskId);
			placeSearcher.setTask(task);
			weblinkGrid.setTaskID(currentTaskId);
			weblinkEditor.setTaskID(currentTaskId);
			documentGrid.setTaskAndProject(getTaskAndProject());

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
				completeButton.setVisible(false);
				deleteButton.setVisible(true);
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
				deleteButton.setVisible(false);
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
				name.setReadOnly(false);
				project.setEnabled(true);
				description.setReadOnly(false);
				notes.setReadOnly(false);
				startDate.setReadOnly(false);
				preferredEndDate.setReadOnly(false);
				deadlineDate.setReadOnly(false);
				repeatEditor.setReadOnly(false);
				repeat.setReadOnly(false);
				subtasks.enableNewButton();

				placeGrid.setReadOnly(false);
				weblinkGrid.setReadOnly(false);
				documentGrid.setReadOnly(false);
			}
		} else {
			// task is null so hide the fields and editors
			nameAndProjectDiv.setVisible(false);
			buttonsAndEditors.setVisible(false);
			extrasLayout.setVisible(false);
			completeButtonDiv.setVisible(false);
		}
	}

	public void saveTask() {
		if (!refreshing) {
			Task task = getTask();

			task.name.setValue(name.getValue());
			task.description.setValue(description.getValue());
			if (assignedToSelector.getValue() == null
					|| Objects.equal(assignedToSelector.getEmptyValue(), assignedToSelector.getValue())) {
				task.assigneeID.setValueToNull();
			} else {
				task.assigneeID.setValue(assignedToSelector.getValue().getUserID());
			}
			if (!notes.getValue().equals(task.notes.stringValue())) {
				task.notes.setValue(notes.getValue());
			}
			task.startDate.setValue(asDate(startDate.getValue()));
			task.preferredDate.setValue(asDate(preferredEndDate.getValue()));
			task.finalDate.setValue(asDate(deadlineDate.getValue()));
			task.repeatOffset.setValue(repeatValue);

			try {
				getDatabase().insert(Changes.getChanges(getCurrentUser(), task));
				getDatabase().update(task);
				fireEvent(new MinorTaskEvent(this, task, true));
			} catch (SQLException ex) {
				sqlerror(ex);
			}
			Globals.savedNotice();
			refresh();
		}
	}

	public void refresh() {
		try {
			refreshing = true;
			setFieldValues();
			addViewedDate(taskAndProject);
		} finally {
			refreshing = false;
		}
//		try {
//			this.setTask(getTask(getTaskID()));
//		} catch (Globals.InaccessibleTaskException ex) {
//			Logger.getLogger(MinorTaskView.class.getName()).log(Level.SEVERE, null, ex);
//		}
	}

	public final void setAsDefaultButton(Button button) {
		button.addClickListener((event) -> {
			saveTask();
		});
	}

	private void showEditor(Component editor) {
		System.out.println("EDITOR: " + editor);
		boolean editorAlreadyShowing = editor == null ? false : editor.isVisible();
		System.out.println("EDITOR ALREADY SHOWING: " + editorAlreadyShowing);
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
			if (editorAlreadyShowing) {
				editor.setVisible(false);
				buttonsAndEditors.removeClassName("open");
			} else {
				editor.setVisible(true);
				buttonsAndEditors.addClassName("open");
			}
		} else {
			buttonsAndEditors.removeClassName("open");
		}
	}

	private void insertLinkToDocument(DocumentAddedEvent event) {
		if (event.getValue() != null) {
			chat("Adding document link..." + event.getSource().getClass().getSimpleName());
			TaskDocumentLink link = new TaskDocumentLink();
			link.documentID.setValue(event.getValue().documentID);
			link.taskID.setValue(getTaskID());
			link.ownerID.setValue(getCurrentUserID());
			try {
				getDatabase().insert(link);
				getDatabase().insert(new Changes(
						getCurrentUser(),
						getTask(),
						"Document",
						"no file",
						event.getValue().filename.getValue(),
						"Connected File: " + event.getValue().filename.getValue()
				)
				);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		} else {
			chat("No Document Found!!: " + event.getSource().getClass().getSimpleName());
		}
	}

	private void addViewedDate(Task.TaskAndProject taskAndProject) {
		final Task task = taskAndProject.getTask();
		if (task != null) {
			TaskViews taskView = new TaskViews();
			taskView.taskID.setValue(task.taskID);
			taskView.userID.setValue(getCurrentUserID());
			taskView.lastviewed.setValue(new Date());
			try {
				getDatabase().insert(taskView);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		fireEvent(event);
	}

	@Override
	public Task getTask() {
		if (taskAndProject == null) {
			return null;
		} else {
			return this.taskAndProject.getTask();
		}
	}

	@Override
	public Task.TaskAndProject getTaskAndProject() {
		return this.taskAndProject;
	}

	@Override
	public void setTask(Task newTask) {
		try {
			setTaskAndProject(getTaskAndProject(newTask));
		} catch (Globals.InaccessibleTaskException ex) {
			sqlerror(ex);
		}
	}

	@Override
	public void setTaskAndProject(Task.TaskAndProject taskAndProject) {
		this.taskAndProject = taskAndProject;
	}

}
