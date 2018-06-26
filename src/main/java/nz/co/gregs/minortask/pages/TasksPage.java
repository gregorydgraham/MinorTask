/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTaskUI;

/**
 *
 * @author gregorygraham
 */
public class TasksPage extends AuthorisedPage{
	
	Long currentTask = null;

	public TasksPage(MinorTaskUI loginUI) {
		super(loginUI);
	}

	@Override
	public void show() {
		VerticalLayout layout = new VerticalLayout();

		final Button createTaskButton = new Button("New");
		setAsDefaultButton(createTaskButton);

		final Button showTasks = new Button("List");
		showTasks.addClickListener((event) -> {
			new TaskListPage(ui, currentTask).show();
		});

		layout.addComponents(createTaskButton, showTasks);

		show(layout);
	}

	@Override
	public void handleDefaultButton() {
		new TaskCreationPage(ui, currentTask).show();
	}

	@Override
	public void handleEscapeButton() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
