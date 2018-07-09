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
import java.util.logging.*;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.*;
import nz.co.gregs.minortask.datamodel.*;

/**
 *
 * @author gregorygraham
 */
public class TaskCreator extends MinorTaskComponent {

	TextField name = new TextField("Name");
	TextField description = new TextField("Description");
	TextField project = new TextField("Project");
	TextField notes = new TextField("Notes");
	DateField startDate = new DateField("Start");
	DateField preferredEndDate = new DateField("End");
	DateField deadlineDate = new DateField("Deadline");
	Button createButton = new Button("Create");
	Button cancelButton = new Button("Cancel");

	public TaskCreator(MinorTask minortask, Long currentTask) {
		super(minortask, currentTask);
		this.setCompositionRoot(getComponent());
	}

	public final Component getComponent() {

		VerticalLayout layout = new VerticalLayout();
		layout.setWidthUndefined();
		layout.addComponent(new ProjectPathNavigator(minortask(), getTaskID()));
		try {
			setEscapeButton(cancelButton);
			setAsDefaultButton(createButton);

			name.setWidthUndefined();
			name.setCursorPosition(0);
			description.setWidth(100, Unit.PERCENTAGE);
			description.setHeight(3, Unit.CM);
			project.setCaption("Part Of:");
			project.setReadOnly(true);

			setFieldValues();

			HorizontalLayout details = new HorizontalLayout(
					name);
			details.setWidthUndefined();

			layout.addComponent(details);
			layout.addComponent(description);

			HorizontalLayout dates = new HorizontalLayout(
					startDate,
					preferredEndDate,
					deadlineDate
			);
			dates.setWidthUndefined();
			layout.addComponent(dates);
			layout.addComponent(
					new HorizontalLayout(
							cancelButton,
							createButton));
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			minortask().sqlerror(ex);
		}
		return layout;
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException {
		LocalDate startDefault = LocalDate.now().plusDays(1);
		LocalDate preferredDefault = LocalDate.now().plusWeeks(2);
		LocalDate deadlineDefault = LocalDate.now().plusMonths(1);
		final Task.Project projectExample = new Task.Project();
		projectExample.taskID.permittedValues(getTaskID());
		if (getTaskID() != null) {
			final Task fullTaskDetails = getDatabase().getDBTable(projectExample).getOnlyRow();
			project.setValue(fullTaskDetails.name.getValue());
		}

		Task.Project taskProject = getProject();
		if (taskProject != null) {
			
			final LocalDate projectStart = MinorTask.asLocalDate(taskProject.startDate.getValue());
			final LocalDate projectEnd = MinorTask.asLocalDate(taskProject.finalDate.getValue());

			startDefault = startDefault.isAfter(projectStart)?startDefault:projectStart;
			preferredDefault = preferredDefault.isAfter(projectStart)?preferredDefault:projectStart;
			preferredDefault = preferredDefault.isBefore(projectEnd)?preferredDefault:projectEnd;
			deadlineDefault = deadlineDefault.isBefore(projectEnd)?deadlineDefault:projectEnd;
			
			startDate.setValue(startDefault);
			preferredEndDate.setValue(preferredDefault);
			deadlineDate.setValue(deadlineDefault);

			startDate.setRangeStart(projectStart);
			startDate.setRangeEnd(projectEnd);
			preferredEndDate.setRangeStart(projectStart);
			preferredEndDate.setRangeEnd(projectEnd);
			deadlineDate.setRangeStart(projectStart);
			deadlineDate.setRangeEnd(projectEnd);
		} else {
			startDate.setValue(startDefault);
			preferredEndDate.setValue(preferredDefault);
			deadlineDate.setValue(deadlineDefault);

		}
	}

	public void handleDefaultButton() {
		Task task = new Task();

		task.userID.setValue(minortask().getUserID());
		task.projectID.setValue(getTaskID());
		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
		task.startDate.setValue(MinorTask.asDate(startDate.getValue()));
		task.preferredDate.setValue(MinorTask.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(MinorTask.asDate(deadlineDate.getValue()));

		try {
			getDatabase().insert(task);
		} catch (SQLException ex) {
			Logger.getLogger(TaskCreator.class.getName()).log(Level.SEVERE, null, ex);
			minortask().sqlerror(ex);
		}
		minortask().showTask(task.taskID.getValue());
	}

	public void handleEscapeButton() {
		minortask().showCurrentTask();
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
