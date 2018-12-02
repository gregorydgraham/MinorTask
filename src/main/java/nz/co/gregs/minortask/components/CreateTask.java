/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.*;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.*;
import nz.co.gregs.minortask.datamodel.*;
import nz.co.gregs.minortask.pages.TaskEditorLayout;

/**
 *
 * @author gregorygraham
 */
//@Tag("createtask")
public class CreateTask extends VerticalLayout implements RequiresLogin {

	TextField name = new TextField("Name");
	TextField description = new TextField("Description");
	TextField project = new TextField("Project");
	TextField notes = new TextField("Notes");
	OptionalDatePicker startDate = new OptionalDatePicker("Start");
	OptionalDatePicker preferredEndDate = new OptionalDatePicker("Reminder");
	OptionalDatePicker deadlineDate = new OptionalDatePicker("Deadline");
	Button createAndShowTaskButton = new Button("Create and Edit");
	Button createAndShowProjectButton = new Button("Create and Return To Project");
	Button createAndAddAnother = new Button("Create and Add Another");
	Button cancelButton = new Button("Cancel");
	private final Long projectID;

	public CreateTask(Long projectID) throws Globals.InaccessibleTaskException {
		this.projectID = projectID;
		addClassName("create-task-component");
		this.add(getComponent());
	}

	public final Component getComponent() throws Globals.InaccessibleTaskException {

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeUndefined();
		layout.add(new ProjectPathNavigator.WithNewTaskLabel(TaskEditorLayout.class, projectID));
		try {
			setEscapeButton(cancelButton);
			setAsDefaultButton(createAndShowProjectButton);
			createAndShowTaskButton.addClickListener((event) -> {
				saveAndEdit();
			});
			createAndAddAnother.addClickListener((event) -> {
				saveAndCreateAnother();
			});

			name.addClassName("create-task-name");
			name.setSizeUndefined();
			name.focus();
			description.addClassName("create-task-description");
			project.setReadOnly(true);

			setFieldValues();

			Div details = new Div(
					name);
			details.setSizeUndefined();

			layout.add(details);
			layout.add(description);

			Div dates = new Div(
					startDate,
					preferredEndDate,
					deadlineDate
			);
			dates.setSizeUndefined();
			layout.add(dates);
			layout.add(
					new Div(
							cancelButton,
							createAndShowTaskButton,
							createAndShowProjectButton,
							createAndAddAnother
					)
			);
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			MinorTask.sqlerror(ex);
		}
		return layout;
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException, MinorTask.InaccessibleTaskException {
		name.clear();
		description.clear();
		startDate.setValue(null);
		preferredEndDate.setValue(null);
		deadlineDate.setValue(null);

		LocalDate startDefault = LocalDate.now().plusDays(1);
		LocalDate preferredDefault = LocalDate.now().plusWeeks(2);
		LocalDate deadlineDefault = LocalDate.now().plusMonths(1);

		Task taskProject = getTask(projectID);
		if (taskProject != null) {

			project.setValue(taskProject.name.getValue());

			final LocalDate projectStart = MinorTask.asLocalDate(taskProject.startDate.getValue());
			final LocalDate projectEnd = MinorTask.asLocalDate(taskProject.finalDate.getValue());

			if (startDefault != null && projectStart != null) {
				startDefault = startDefault.isAfter(projectStart) ? startDefault : projectStart;
			}
			if (preferredDefault != null) {
				if (projectStart != null) {
					preferredDefault = preferredDefault.isAfter(projectStart) ? preferredDefault : projectStart;
				}
				if (projectEnd != null) {
					preferredDefault = preferredDefault.isBefore(projectEnd) ? preferredDefault : projectEnd;
				}
			}
			if (deadlineDefault != null && projectEnd != null) {
				deadlineDefault = deadlineDefault.isBefore(projectEnd) ? deadlineDefault : projectEnd;
			}

			startDate.setDefaultValue(startDefault);
			preferredEndDate.setDefaultValue(preferredDefault);
			deadlineDate.setDefaultValue(deadlineDefault);

			startDate.setMin(projectStart);
			startDate.setMax(projectEnd);
			preferredEndDate.setMin(projectStart);
			preferredEndDate.setMax(projectEnd);
			deadlineDate.setMin(projectStart);
			deadlineDate.setMax(projectEnd);
		} else {
			startDate.setValue(startDefault);
			preferredEndDate.setValue(preferredDefault);
			deadlineDate.setValue(deadlineDefault);

		}
	}

	public void saveAndEdit() {
		Task task = saveTask();
		MinorTask.showTask(task.taskID.getValue());
	}

	public void saveAndCreateAnother() {
		try {
			Task task = saveTask();
			setFieldValues();
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
			sqlerror(ex);
		} catch (Globals.InaccessibleTaskException ex) {
			error("Inaccessible Task", ex.getMessage());
		}
	}

	public void saveAndProject() {
		Task task = saveTask();
		MinorTask.showTask(task.projectID.getValue());
	}

	public void handleEscapeButton() {
		MinorTask.showTask(projectID);
	}

	protected Task saveTask() {
		Task task = new Task();
		task.userID.setValue(minortask().getUserID());
		task.projectID.setValue(projectID);
		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
		task.startDate.setValue(Globals.asDate(startDate.getValue()));
		task.preferredDate.setValue(Globals.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(Globals.asDate(deadlineDate.getValue()));
		try {
			getDatabase().insert(task);
		} catch (SQLException ex) {
			Logger.getLogger(CreateTask.class.getName()).log(Level.SEVERE, null, ex);
			MinorTask.sqlerror(ex);
		}
		return task;
	}

	public final void setAsDefaultButton(Button button) {
		button.getElement().setAttribute("theme", "success primary");
		button.addClickListener((event) -> {
			saveAndProject();
		});
	}

	public final void setEscapeButton(Button button) {
		button.addClickListener((event) -> {
			handleEscapeButton();
		});
	}
}
