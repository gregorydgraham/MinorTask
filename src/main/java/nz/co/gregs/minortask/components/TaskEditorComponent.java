/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
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
public class TaskEditorComponent extends CustomComponent {

	TextField name = new TextField("Name");
	TextField description = new TextField("Description");
	TextField project = new TextField("Project");
	TextField notes = new TextField("Notes");
	DateField startDate = new DateField("Start");
	DateField preferredEndDate = new DateField("End");
	DateField deadlineDate = new DateField("Deadline");
	Button createButton = new Button("Create");
	Button cancelButton = new Button("Cancel");
	private MinorTaskUI ui;
	private Long taskID;

	public TaskEditorComponent(MinorTaskUI ui, Long currentTask) {
		this.ui =ui;
		this.taskID = currentTask;
		setCompositionRoot(getComponent());
	}
	
	public Component getComponent() {

		VerticalLayout layout = new VerticalLayout();
		try {
			layout.addComponent(new Label("Current Project To Create Within: " + taskID));

			setEscapeButton(cancelButton);
			setAsDefaultButton(createButton);

			name.setMaxLength(40);
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
			ui.sqlerror(ex);
		}
		return layout;
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException {
		final LocalDate startDefault = LocalDate.now().plusDays(1);
		final LocalDate preferredDefault = LocalDate.now().plusWeeks(1);
		final LocalDate deadlineDefault = LocalDate.now().plusWeeks(2);
		final Project projectExample = new Project();
		projectExample.taskID.permittedValues(taskID);
		if (taskID != null) {
			final Task fullTaskDetails = ui.getDatabase().getDBTable(projectExample).getOnlyRow();
			project.setValue(fullTaskDetails.name.getValue());
		}
		startDate.setValue(startDefault);
		preferredEndDate.setValue(preferredDefault);
		deadlineDate.setValue(deadlineDefault);
	}

	public void handleDefaultButton() {
		Task task = new Task();

		task.userID.setValue(ui.getUserID());
		task.projectID.setValue(taskID);
		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
//		task.notes.setValue(notes.getValue());
		task.startDate.setValue(Helper.asDate(startDate.getValue()));
		task.preferredDate.setValue(Helper.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(Helper.asDate(deadlineDate.getValue()));

		try {
			ui.getDatabase().insert(task);
		} catch (SQLException ex) {
			Logger.getLogger(TaskCreationComponent.class.getName()).log(Level.SEVERE, null, ex);
			ui.sqlerror(ex);
		}
		ui.showTask(null);
	}

	public void handleEscapeButton() {
		(ui).showTask();
	}

	public final void setAsDefaultButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener((event) -> {
			handleDefaultButton();
		});
	}

	public final void setEscapeButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
		button.addClickListener((event) -> {
			handleEscapeButton();
		});
	}
}
