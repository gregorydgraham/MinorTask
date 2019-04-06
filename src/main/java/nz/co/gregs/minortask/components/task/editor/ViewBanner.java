/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

import nz.co.gregs.minortask.MinorTaskEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.shared.Registration;
import nz.co.gregs.minortask.components.task.SecureTaskDiv;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.MinorTaskLayout;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
@Tag("task-banner")
@StyleSheet("styles/task-banner.css")
public class ViewBanner extends SecureTaskDiv implements MinorTaskEventListener, MinorTaskEventNotifier {

	private final Label nameLabel = new Label("Projects");
	private final Label descriptionLabel = new Label("");
	private final ProjectNavigator projectPathNavigator = new ProjectNavigator();
	private final MinorTaskLayout page;

	public ViewBanner(MinorTaskLayout page) {
		super();
		this.page = page;
		initComponents();
	}

	private void initComponents() {
		addClassName("taskbanner");
		nameLabel.addClassName("name");

		descriptionLabel.addClassName("description");

		projectPathNavigator.addMinorTaskEventListener(this);

		add(nameLabel, descriptionLabel, projectPathNavigator);
	}

	private void setValues() {
		final Task task = getTask();
		if (task != null&&task.getDefined()) {
			nameLabel.setText(task.name.getValue(""));
			descriptionLabel.setText(task.description.getValue(""));
		} else {
			nameLabel.setText("Projects");
			descriptionLabel.setText("Build Your Plans Here");
		}
		projectPathNavigator.setTask(task);
	}

	@Override
	public void setTask(Task newTask) {
		super.setTask(newTask);
		setValues();
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		;
	}

	@Override
	public Registration addMinorTaskEventListener(MinorTaskEventListener listener) {
		this.projectPathNavigator.addMinorTaskEventListener(listener);
		return MinorTaskEventNotifier.super.addMinorTaskEventListener(listener);
	}

	public void setDescription(String description) {
		descriptionLabel.setText(description);
	}

	@Override
	public void setTitle(String title) {
		nameLabel.setText(title);
	}

}
