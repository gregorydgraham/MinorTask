/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import java.sql.SQLException;
import java.util.Date;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.components.changes.Changes;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/createtaskinline.css")
public class CreateTaskInline extends SecureTaskSpan {

	public CreateTaskInline() {
		super(); 
		setClassName();
	}
	public CreateTaskInline(Task task) {
		super(task);
		setClassName();
	}

	private void setClassName() {
		addClassName("createtaskinline");
	}

	public void addNewMinorTask(HtmlContainer container, Component replaceComponent) {
		container.remove(replaceComponent);
		this.removeAll();

		final TextField textArea = new TextField();
		textArea.setPlaceholder("Project Name");
		textArea.setSizeFull();
		final Button saveButton = new Button("Save");
		saveButton.addClassName("save");
		saveButton.setSizeUndefined();
		saveButton.addClickListener((event) -> {
			container.remove(this);
			container.add(replaceComponent);
			createNewMinorTaskFromName(textArea.getValue());
		});
		final Button cancelButton = new Button("Cancel");
		cancelButton.addClassName("cancel");
		cancelButton.setSizeUndefined();
		cancelButton.addClickListener((event) -> {
			container.remove(this);
			container.add(replaceComponent);
			cancelCreateNewMinorTask();
		});

		this.add(textArea, saveButton, cancelButton);
		container.add(this);
		textArea.focus();
	}

	private void createNewMinorTaskFromName(String name) {
		if (name != null && !name.isEmpty()) {
			Task task = new Task();
			task.name.setValue(name);
			task.userID.setValue(getCurrentUserID());
			task.projectID.setValue(getTaskID());
			task.startDate.setValue(new Date());
			if (getTask() != null) {
				task.finalDate.setValue(getTask().finalDate.getValue());
			}
			try {
				getDatabase().insert(task);
				getDatabase().insert(new Changes(getCurrentUser(), task));
			} catch (SQLException ex) {
				sqlerror(ex);
			} finally {
				fireEvent(new TaskCreatedEvent(this));
			}
		}
	}

	private void cancelCreateNewMinorTask() {
	}

	public Registration addCreateTaskListener(ComponentEventListener<TaskCreatedEvent> listener) {
		return addListener(TaskCreatedEvent.class, listener);
	}
}
