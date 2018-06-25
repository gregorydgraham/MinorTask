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
public class CreateTaskPage extends AuthorisedPage {

	TextField name = new TextField("Name");
	TextField description = new TextField("Name");
	DateField startDate = new DateField("Start");
	DateField preferredEndDate = new DateField("Preferred");
	DateField deadlineDate = new DateField("Deadline");
	Button createButton = new Button("Create");
	Button cancelButton = new Button("Cancel");
	private final Long projectID;

	public CreateTaskPage(MinorTaskUI ui, Long projectID) {
		super(ui);
		this.projectID = projectID;
	}

	@Override
	public void show() {

		VerticalLayout layout = new VerticalLayout();

		layout.addComponents(name, description, startDate, preferredEndDate, deadlineDate);

		cancelButton.addClickListener((event) -> {
			new TasksPage(ui).show();
		});

		createButton.addClickListener((event) -> {
			handle();
		});

		HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, createButton);

		layout.addComponent(buttonLayout);

		super.show(layout);
	}

	@Override
	public void handle() {
		Task task = new Task();

		task.userID.setValue(ui.getUserID());
		if (projectID == null) {
			task.projectID.setValueToNull();
		} else {
			task.projectID.setValue(projectID);
		}
		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
		task.startDate.setValue(Helper.asDate(startDate.getValue()));
		task.preferredDate.setValue(Helper.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(Helper.asDate(deadlineDate.getValue()));

		try {
			MinorTaskUI.database.insert(task);
		} catch (SQLException ex) {
			Logger.getLogger(CreateTaskPage.class.getName()).log(Level.SEVERE, null, ex);
			sqlerror(ex);
		}
		new TasksPage(ui).show();
	}

}
