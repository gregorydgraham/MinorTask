/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
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
		example.userID.permittedValues(getUserID());
		if (projectID == null) {
			example.projectID.isNull();
		} else {
			example.projectID.permittedValues(projectID);
		}
		example.startDate.setSortOrderAscending();
		try {
			List<Task> tasks = getDatabase().get(example);
			layout.addComponent(new Label(tasks.size() + " Tasks Found"));
			GridLayout gridlayout = new GridLayout(4, 4);
			for (Task task : tasks) {
				Label name = new Label(task.name.getValue());
				name.setWidth(10, Sizeable.Unit.CM);
				Label desc = new Label(task.description.getValue());
				desc.setWidth(10, Sizeable.Unit.CM);
				desc.addStyleName("tiny");
				final VerticalLayout summary = new VerticalLayout(name, desc);
				summary.setWidth(10, Sizeable.Unit.CM);
				summary.setDefaultComponentAlignment(Alignment.TOP_LEFT);
				
				final TextField startdate = new TextField("Start",Helper.asDateString(task.startDate.getValue(),ui));
				final TextField readyDate = new TextField("Ready",Helper.asDateString(task.preferredDate.getValue(), ui));
				final TextField deadline = new TextField("Deadline",Helper.asDateString(task.finalDate.getValue(),ui));
				
				startdate.setReadOnly(true);
				startdate.setWidth(8, Sizeable.Unit.EM);
				readyDate.setReadOnly(true);
				readyDate.setWidth(8, Sizeable.Unit.EM);
				deadline.setReadOnly(true);
				deadline.setWidth(8, Sizeable.Unit.EM);
				
				gridlayout.addComponent(summary);
				gridlayout.addComponent(startdate);
				gridlayout.addComponent(readyDate);
				gridlayout.addComponent(deadline);
				gridlayout.newLine();
			}
			layout.addComponent(gridlayout);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		
		show(layout);
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
