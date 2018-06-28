/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.*;
import java.sql.SQLException;
import java.time.LocalDate;
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
public class TaskCreationComponent extends AuthorisedComponent {

	TextField name = new TextField("Name");
	TextField description = new TextField("Description");
	TextField project = new TextField("Project");
	TextField notes = new TextField("Notes");
	DateField startDate = new DateField("Start");
	DateField preferredEndDate = new DateField("End");
	DateField deadlineDate = new DateField("Deadline");
	Button createButton = new Button("Create");
	Button cancelButton = new Button("Cancel");
	private Task existingTask = null;

	public TaskCreationComponent(MinorTaskUI ui, Long currentTask) {
		super(ui, currentTask);
	}

	public TaskCreationComponent(MinorTaskUI ui, Long currentTask, Task existingTask) {
		super(ui, currentTask);
		this.existingTask = existingTask;
	}

	@Override
	public Component getAuthorisedComponent() {

		VerticalLayout layout = new VerticalLayout();
		try {
			layout.addComponent(new Label("Current Project To Create Within: " + currentTaskID));

			setEscapeButton(cancelButton);
			setAsDefaultButton(createButton);

			name.setMaxLength(20);
			description.setMaxLength(50);
			project.setCaption("Part Of:");
			project.setReadOnly(true);

			setFieldValues();

			layout.addComponents(
					name,
					description,
					new HorizontalLayout(
							startDate,
							preferredEndDate,
							deadlineDate),
					//				notes,
					new HorizontalLayout(
							cancelButton,
							createButton)
			);
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			sqlerror(ex);
		}
		return layout;
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException {
		if (existingTask == null) {
			final Project projectExample = new Project();
			projectExample.taskID.permittedValues(currentTaskID);
			if (currentTaskID != null) {
				final Task fullTaskDetails = getDatabase().getDBTable(projectExample).getOnlyRow();
				project.setValue(fullTaskDetails.name.getValue());
			}
			startDate.setValue(LocalDate.now().plusDays(1));
			preferredEndDate.setValue(LocalDate.now().plusWeeks(1));
			deadlineDate.setValue(LocalDate.now().plusWeeks(2));
		} else {
			name.setValue(existingTask.name.getValue());
			description.setValue(existingTask.description.getValue());
			startDate.setValue(Helper.asLocalDate(existingTask.startDate.getValue()));
			preferredEndDate.setValue(Helper.asLocalDate(existingTask.preferredDate.getValue()));
			deadlineDate.setValue(Helper.asLocalDate(existingTask.finalDate.getValue()));
		}
	}

	@Override
	public void handleDefaultButton() {
		Task task = new Task();

		task.userID.setValue(ui.getUserID());
		task.projectID.setValue(currentTaskID);
		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
//		task.notes.setValue(notes.getValue());
		task.startDate.setValue(Helper.asDate(startDate.getValue()));
		task.preferredDate.setValue(Helper.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(Helper.asDate(deadlineDate.getValue()));

		try {
			getDatabase().insert(task);
		} catch (SQLException ex) {
			Logger.getLogger(TaskCreationComponent.class.getName()).log(Level.SEVERE, null, ex);
			sqlerror(ex);
		}
		new TasksComponent(ui).show();
	}

	@Override
	public void handleEscapeButton() {
		new TasksComponent(ui).show();
	}

}
