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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import java.sql.SQLException;
import java.util.Date;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class CreateTaskInline extends SecureTaskSpan {

	public CreateTaskInline(Long taskid) {
		super(taskid);
	}

	public CreateTaskInline(Task task) {
		super(task);
	}

	public void addNewMinorTask(HtmlContainer container, Component instigator) {
		instigator.setVisible(false);
		Span span = new Span();
		span.addClassName("openprojects-newminortask");

		final TextField textArea = new TextField();
		textArea.setPlaceholder("Project Name");
		textArea.setSizeFull();
		final Button saveButton = new Button("Save");
		saveButton.addClassName("save");
		saveButton.setSizeUndefined();
		saveButton.addClickListener((event) -> {
			container.remove(span);
			createNewMinorTaskFromName(textArea.getValue(), instigator);
		});
		final Button cancelButton = new Button("Cancel");
		cancelButton.addClassName("cancel");
		cancelButton.setSizeUndefined();
		cancelButton.addClickListener((event) -> {
			container.remove(span);
			cancelCreateNewMinorTask(instigator);
		});

		span.add(textArea, saveButton, cancelButton);
		container.add(span);
		textArea.focus();
	}

	private void createNewMinorTaskFromName(String name, Component instigator) {
		instigator.setVisible(true);
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
			} catch (SQLException ex) {
				sqlerror(ex);
			} finally {
				fireEvent(new TaskCreatedEvent(this));
			}
		}
	}

	private void cancelCreateNewMinorTask(Component instigator) {
		instigator.setVisible(true);
	}

	public Registration addCreateTaskListener(ComponentEventListener<TaskCreatedEvent> listener) {
		return addListener(TaskCreatedEvent.class, listener);
	}
}
