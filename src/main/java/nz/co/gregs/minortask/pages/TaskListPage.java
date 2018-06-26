/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class TaskListPage extends AuthorisedPage {

	private final Long projectID;

	public TaskListPage(MinorTaskUI ui, Long selectedTask) {
		super(ui);
		projectID = selectedTask;
	}

	@Override
	public void show() {
		VerticalLayout layout = new VerticalLayout();

		final Button cancelTaskButton = new Button("Back");
		setEscapeButton(cancelTaskButton);
		
		final Button createTaskButton = new Button("New");
		setAsDefaultButton(createTaskButton);

		layout.addComponent(new HorizontalLayout(cancelTaskButton, createTaskButton));

		Task example = new Task();
		example.userID.permittedValues(ui.getUserID());
		if (projectID == null) {
			example.projectID.isNull();
		} else {
			example.projectID.permittedValues(projectID);
		}
		example.startDate.setSortOrderAscending();
		try {
			List<Task> tasks = getDatabase().get(example);
			layout.addComponent(new Label(tasks.size()+" Tasks Found"));
			for (Task task : tasks) {
				HorizontalLayout hlayout = new HorizontalLayout();
				Label name = new Label(task.name.getValue());
				Label desc = new Label(task.description.getValue());
				desc.addStyleName("small");
				hlayout.addComponent(name);
				hlayout.addComponent(desc);
				hlayout.addComponent(new Label(Helper.asDateString(task.startDate.getValue())));
				hlayout.addComponent(new Label(Helper.asDateString(task.preferredDate.getValue())));
				hlayout.addComponent(new Label(Helper.asDateString(task.finalDate.getValue())));
				layout.addComponent(hlayout);
			}
		} catch (SQLException ex) {
			sqlerror(ex);
		}

		super.show(layout);
	}

	@Override
	public void handleDefaultButton() {
		new TaskCreationPage(ui, null).show();
	}

	@Override
	public void handleEscapeButton() {
		new TasksPage(ui).show();
	}

}
