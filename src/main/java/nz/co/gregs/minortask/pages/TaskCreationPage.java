/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.ui.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.*;

/**
 *
 * @author gregorygraham
 */
public class TaskCreationPage extends AuthorisedPage {

	TextField name = new TextField("Name");
	TextField description = new TextField("Description");
	TextField notes = new TextField("Notes");
	DateField startDate = new DateField("Start");
	DateField preferredEndDate = new DateField("End");
	DateField deadlineDate = new DateField("Deadline");
	Button createButton = new Button("Create");
	Button cancelButton = new Button("Cancel");
	private final Long projectID;

	public TaskCreationPage(MinorTaskUI ui, Long projectID) {
		super(ui);
		this.projectID = projectID;
	}

	@Override
	public void show() {

		VerticalLayout layout = new VerticalLayout();

		setEscapeButton(cancelButton);
		
		setAsDefaultButton(createButton);
		name.setMaxLength(20);
		description.setMaxLength(50);
		 startDate.setValue( LocalDate.now().plusDays(1));
		 preferredEndDate.setValue( LocalDate.now().plusWeeks(1));
		 deadlineDate.setValue( LocalDate.now().plusWeeks(2));

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

		super.show(layout);
	}

	@Override
	public void handleDefaultButton() {
		Task task = new Task();

		task.userID.setValue(ui.getUserID());
		if (projectID == null) {
			task.projectID.setValueToNull();
		} else {
			task.projectID.setValue(projectID);
		}
		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
//		task.notes.setValue(notes.getValue());
		task.startDate.setValue(Helper.asDate(startDate.getValue()));
		task.preferredDate.setValue(Helper.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(Helper.asDate(deadlineDate.getValue()));

		try {
			getDatabase().insert(task);
		} catch (SQLException ex) {
			Logger.getLogger(TaskCreationPage.class.getName()).log(Level.SEVERE, null, ex);
			sqlerror(ex);
		}
		new TasksPage(ui).show();
	}

	@Override
	public void handleEscapeButton() {
		new TasksPage(ui).show();
	}

}
