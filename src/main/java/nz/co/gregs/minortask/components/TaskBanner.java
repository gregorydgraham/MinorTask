/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import nz.co.gregs.minortask.components.task.SecureTaskDiv;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.TaskEditorLayout;

/**
 *
 * @author gregorygraham
 */
@Tag("task-banner")
@StyleSheet("styles/task-banner.css")
public class TaskBanner extends SecureTaskDiv {

	public TaskBanner() {
		super();
	}

	public TaskBanner(Long taskid) {
		super(taskid);
		initComponents();
	}

	public TaskBanner(Task task) {
		super(task);
		initComponents();
	}

	private void initComponents() {
		addClassName("taskbanner");
		Label nameLabel = new Label("Projects");
		nameLabel.addClassName("name");
		Label descriptionLabel = new Label("");
		descriptionLabel.addClassName("description");
		final Task task = getTask();
		if (task != null) {
			nameLabel.setText(task.name.getValue(""));
			descriptionLabel.setText(task.description.getValue(""));
		}
		final ProjectPathNavigator projectPathNavigator = new ProjectPathNavigator(TaskEditorLayout.class, getTaskID());
		add(nameLabel, descriptionLabel, projectPathNavigator);
	}
}
