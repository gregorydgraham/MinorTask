/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.*;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.*;
import nz.co.gregs.minortask.datamodel.*;

/**
 *
 * @author gregorygraham
 */
//@Tag("createtask")
public class CreateTask extends VerticalLayout implements RequiresLogin{

	TextField name = new TextField("Name");
	TextField description = new TextField("Description");
	TextField project = new TextField("Project");
	TextField notes = new TextField("Notes");
	DatePicker startDate = new DatePicker("Start");
	DatePicker preferredEndDate = new DatePicker("End");
	DatePicker deadlineDate = new DatePicker("Deadline");
	Button createAndShowTaskButton = new Button("Create and Edit");
	Button createAndShowProjectButton = new Button("Create and Return To Project");
	Button cancelButton = new Button("Cancel");
	private final Long projectID;

	public CreateTask(Long projectID) throws MinorTask.InaccessibleTaskException {
		this.projectID= projectID;
		this.add(getComponent());
	}

	public final Component getComponent() throws MinorTask.InaccessibleTaskException {

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeUndefined();
		layout.add(new ProjectPathNavigator.WithNewTaskLabel(projectID));
		try {
			setEscapeButton(cancelButton);
			setAsDefaultButton(createAndShowProjectButton);
			createAndShowTaskButton.addClickListener((event) -> {
				saveAndEdit();
			});

			name.setSizeUndefined();
			name.focus();
			description.setWidth("100%");
			description.setHeight("3cm");
//			project.setCaption("Part Of:");
			project.setReadOnly(true);

			setFieldValues();

			HorizontalLayout details = new HorizontalLayout(
					name);
			details.setSizeUndefined();

			layout.add(details);
			layout.add(description);

			HorizontalLayout dates = new HorizontalLayout(
					startDate,
					preferredEndDate,
					deadlineDate
			);
			dates.setSizeUndefined();
			layout.add(dates);
			layout.add(new HorizontalLayout(
							cancelButton,
							createAndShowTaskButton,
							createAndShowProjectButton));
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			minortask().sqlerror(ex);
		}
		return layout;
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException, MinorTask.InaccessibleTaskException {
		LocalDate startDefault = LocalDate.now().plusDays(1);
		LocalDate preferredDefault = LocalDate.now().plusWeeks(2);
		LocalDate deadlineDefault = LocalDate.now().plusMonths(1);
//		final Task.Project projectExample = new Task.Project();
//		projectExample.taskID.permittedValues(projectID);
//		if (projectID != null) {
//			final Task fullTaskDetails = getDatabase().getDBTable(projectExample).getOnlyRow();
//			project.setValue(fullTaskDetails.name.getValue());
//		}

		Task taskProject = getTask(projectID);
		if (taskProject != null) {
			
			project.setValue(taskProject.name.getValue());

			final LocalDate projectStart = MinorTask.asLocalDate(taskProject.startDate.getValue());
			final LocalDate projectEnd = MinorTask.asLocalDate(taskProject.finalDate.getValue());

			startDefault = startDefault.isAfter(projectStart)?startDefault:projectStart;
			preferredDefault = preferredDefault.isAfter(projectStart)?preferredDefault:projectStart;
			preferredDefault = preferredDefault.isBefore(projectEnd)?preferredDefault:projectEnd;
			deadlineDefault = deadlineDefault.isBefore(projectEnd)?deadlineDefault:projectEnd;
			
			startDate.setValue(startDefault);
			preferredEndDate.setValue(preferredDefault);
			deadlineDate.setValue(deadlineDefault);

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
		minortask().showTask(task.taskID.getValue());
	}

	public void saveAndProject() {
		Task task = saveTask();
		minortask().showTask(task.projectID.getValue());
	}

	public void handleEscapeButton() {
		minortask().showTask(projectID);
	}

	protected Task saveTask() {
		Task task = new Task();
		task.userID.setValue(minortask().getUserID());
		task.projectID.setValue(projectID);
		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
		task.startDate.setValue(MinorTask.asDate(startDate.getValue()));
		task.preferredDate.setValue(MinorTask.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(MinorTask.asDate(deadlineDate.getValue()));
		try {
			getDatabase().insert(task);
		} catch (SQLException ex) {
			Logger.getLogger(CreateTask.class.getName()).log(Level.SEVERE, null, ex);
			minortask().sqlerror(ex);
		}
		return task;
	}

	public final void setAsDefaultButton(Button button) {
//		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		button.getElement().setAttribute("theme", "success primary");
		button.addClickListener((event) -> {
			saveAndProject();
		});
	}

	public final void setEscapeButton(Button button) {
//		button.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
		button.addClickListener((event) -> {
			handleEscapeButton();
		});
	}
}
