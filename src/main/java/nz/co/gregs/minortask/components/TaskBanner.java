/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import nz.co.gregs.minortask.components.task.editor.ProjectNavigator;
import nz.co.gregs.minortask.MinorTaskEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import nz.co.gregs.minortask.components.task.SecureTaskDiv;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
@Tag("task-banner")
@StyleSheet("styles/task-banner.css")
public class TaskBanner extends SecureTaskDiv implements MinorTaskEventListener, MinorTaskEventNotifier{

	private Label nameLabel;
	private Label descriptionLabel;
	private ProjectNavigator projectPathNavigator;

	public TaskBanner() {
		super();
		initComponents();
	}

	private void initComponents() {
		addClassName("taskbanner");
		nameLabel = new Label("Projects");
		nameLabel.addClassName("name");
		descriptionLabel = new Label("");
		descriptionLabel.addClassName("description");
		
		projectPathNavigator = new ProjectNavigator();
		add(nameLabel, descriptionLabel, projectPathNavigator);
	}

//	@Override
//	public void setTask(Task newTask) {
//		super.setTask(newTask);
//		final Task task = getTask();
//		if (task != null) {
//			nameLabel.setText(task.name.getValue(""));
//			descriptionLabel.setText(task.description.getValue(""));
//		}
//		projectPathNavigator.setTask(newTask);
//	}

	@Override
	public void setTask(Task newTask) {
		super.setTask(newTask);
		final Task task = getTask();
		if (task != null) {
			nameLabel.setText(task.name.getValue(""));
			descriptionLabel.setText(task.description.getValue(""));
		}else{
			nameLabel.setText("Projects");
			descriptionLabel.setText("All Your Plans");
		}
		projectPathNavigator.setTask(newTask);
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		fireEvent(event);
		
	}

	
}
