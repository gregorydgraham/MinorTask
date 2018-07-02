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
import java.util.*;
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
	ActiveTaskList subtasks = new ActiveTaskList(minortask(), getTaskID());
	Button completedButton = new Button("Complete This Task");
	CompletedTaskList completedTasks = new CompletedTaskList(minortask(), getTaskID());
	TextField notes = new TextField("Notes");
	DateField startDate = new DateField("Start");
	DateField preferredEndDate = new DateField("End");
	DateField deadlineDate = new DateField("Deadline");
	Button createButton = new Button("Create");
	Button cancelButton = new Button("Cancel");

	public TaskCreator(MinorTaskUI ui, Long currentTask) {
		super(ui, currentTask);
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
			description.setWidthUndefined();
			project.setCaption("Part Of:");
			project.setReadOnly(true);
			
			completedButton.addStyleName("danger");
			completedButton.addClickListener(new CompleteTaskListener(getTaskID()));

			setFieldValues();

			HorizontalLayout details = new HorizontalLayout(
					name,
					description);
			details.setWidthUndefined();
			layout.addComponent(details);
			HorizontalLayout dates = new HorizontalLayout(
							startDate,
							preferredEndDate,
							deadlineDate
			);
			dates.setWidthUndefined();
			layout.addComponent(dates);
			layout.addComponent(subtasks);
			layout.addComponent(completedButton);
			layout.addComponent(completedTasks);
			layout.addComponent(
					new HorizontalLayout(
							cancelButton,
							createButton));
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			Helper.sqlerror(ex);
		}
		return layout;
	}

	public void setFieldValues() throws SQLException, UnexpectedNumberOfRowsException {
		final LocalDate startDefault = LocalDate.now().plusDays(1);
		final LocalDate preferredDefault = LocalDate.now().plusWeeks(1);
		final LocalDate deadlineDefault = LocalDate.now().plusWeeks(2);
		final Task.Project projectExample = new Task.Project();
		projectExample.taskID.permittedValues(getTaskID());
		if (getTaskID() != null) {
			final Task fullTaskDetails = Helper.getDatabase().getDBTable(projectExample).getOnlyRow();
			project.setValue(fullTaskDetails.name.getValue());
		}
		startDate.setValue(startDefault);
		preferredEndDate.setValue(preferredDefault);
		deadlineDate.setValue(deadlineDefault);
	}

	public void handleDefaultButton() {
		Task task = new Task();

		task.userID.setValue(minortask().getUserID());
		task.projectID.setValue(getTaskID());
		task.name.setValue(name.getValue());
		task.description.setValue(description.getValue());
		task.startDate.setValue(Helper.asDate(startDate.getValue()));
		task.preferredDate.setValue(Helper.asDate(preferredEndDate.getValue()));
		task.finalDate.setValue(Helper.asDate(deadlineDate.getValue()));

		try {
			Helper.getDatabase().insert(task);
		} catch (SQLException ex) {
			Logger.getLogger(TaskCreator.class.getName()).log(Level.SEVERE, null, ex);
			Helper.sqlerror(ex);
		}
		minortask().showTask(task.taskID.getValue());
	}

	public void handleEscapeButton() {
		minortask().showTask();
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

	private static class CompleteTaskListener implements Button.ClickListener {

		private final Long taskID;

		public CompleteTaskListener(Long taskID) {
			this.taskID = taskID;
		}

		@Override
		public void buttonClick(Button.ClickEvent event) {
			completeTask(taskID);
			Task task = Helper.getTask(taskID);
			task.status.setValue(Task.Status.COMPLETED);
			try {
				Helper.getDatabase().update(task);
			} catch (SQLException ex) {
				Helper.sqlerror(ex);
			}
			
		}
		
		private void completeTask(Long taskID){
			if (taskID!=null){
				List<Task> subtasks = Helper.getSubTasks(taskID);
				for (Task subtask : subtasks){
					completeTask(subtask.taskID.getValue());
				}
				Task task = Helper.getTask(taskID);
				task.status.setValue(Task.Status.COMPLETED);
				task.completionDate.setValue(new Date());
			}
		}
	}

}
