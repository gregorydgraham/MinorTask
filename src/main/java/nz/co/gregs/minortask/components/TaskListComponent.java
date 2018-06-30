/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import nz.co.gregs.minortask.datamodel.TaskWithSortColumns;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class TaskListComponent extends MinorTaskComponent {

	private final TaskWithSortColumns example;

	public TaskListComponent(MinorTaskUI ui, Long selectedTask, TaskWithSortColumns example) {
		super(ui, selectedTask);
		this.example = example;
		this.setCompositionRoot(getComponent());
	}

	public final Component getComponent() {

		VerticalLayout layout = new VerticalLayout();
		try {
			layout.addComponent(new ProjectPathNavigatorComponent(minortask(), getTaskID()));

			Label actualTaskName = new Label("All");
			final Task actualTask = new Task();
			actualTask.userID.permittedValues(minortask().getUserID());
			actualTask.taskID.permittedValues(getTaskID());
			final DBDatabase database = Helper.getDatabase();
			if (getTaskID() != null) {
				final Task fullTaskDetails = database.getDBTable(actualTask).getOnlyRow();
				actualTaskName.setValue(fullTaskDetails.name.getValue());
			}
			layout.addComponent(actualTaskName);

			final DBTable<TaskWithSortColumns> dbTable = database.getDBTable(example);
			dbTable.setSortOrder(
					example.column(example.isOverdue),
					example.column(example.hasStarted),
					example.column(example.finalDate),
					example.column(example.startDate)
			);
			List<TaskWithSortColumns> tasks = dbTable.getAllRows();
			final String caption = tasks.size() + " Tasks Found";
			layout.addComponent(addTasksToLayout(caption, tasks));
		} catch (SQLException | UnexpectedNumberOfRowsException ex) {
			Helper.sqlerror(ex);
		}
		return layout;
	}

	public AbstractLayout addTasksToLayout(String caption, List<TaskWithSortColumns> tasks) {
		GridLayout gridlayout = new GridLayout(4, 4);
		gridlayout.addComponent(new Label(caption));
		gridlayout.newLine();
		for (Task task : tasks) {
			TaskClickListener taskClickListener = new TaskClickListener(task);

			Label name = new Label(task.name.getValue());
			Label desc = new Label(task.description.getValue());

			name.setWidth(10, Sizeable.Unit.CM);
			desc.setWidth(10, Sizeable.Unit.CM);
			desc.addStyleName("tiny");

			final VerticalLayout summary = new VerticalLayout(name, desc);
			summary.setWidth(10, Sizeable.Unit.CM);
			summary.setDefaultComponentAlignment(Alignment.TOP_LEFT);

			final TextField startdate = new TextField("Start", Helper.asDateString(task.startDate.getValue(), minortask()));
			final TextField readyDate = new TextField("Ready", Helper.asDateString(task.preferredDate.getValue(), minortask()));
			final TextField deadline = new TextField("Deadline", Helper.asDateString(task.finalDate.getValue(), minortask()));

			startdate.setReadOnly(true);
			startdate.setWidth(8, Sizeable.Unit.EM);
			readyDate.setReadOnly(true);
			readyDate.setWidth(8, Sizeable.Unit.EM);
			deadline.setReadOnly(true);
			deadline.setWidth(8, Sizeable.Unit.EM);

			summary.addLayoutClickListener(taskClickListener);
			startdate.addFocusListener(taskClickListener);
			readyDate.addFocusListener(taskClickListener);
			deadline.addFocusListener(taskClickListener);
			gridlayout.addComponent(summary);
			gridlayout.addComponent(startdate);
			gridlayout.addComponent(readyDate);
			gridlayout.addComponent(deadline);
			gridlayout.newLine();
		}
		return gridlayout;
	}

//	@Override
	public void handleDefaultButton() {
		minortask().showTaskCreation(getTaskID());
	}

//	@Override
	public void handleEscapeButton() {
		minortask().showTask();
	}

	private class TaskClickListener implements LayoutEvents.LayoutClickListener, FieldEvents.FocusListener {

		private final Task task;

		public TaskClickListener(Task task) {
			this.task = task;
		}

		@Override
		public void layoutClick(LayoutEvents.LayoutClickEvent event) {
			handleEvent(event);
		}

		@Override
		public void focus(FieldEvents.FocusEvent event) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void handleEvent(LayoutEvents.LayoutClickEvent event) {
			Helper.chat("Switching to " + task.name.getValue());
			if (event.getButton() == MouseEventDetails.MouseButton.LEFT) {
				final Long taskID = task.taskID.getValue();
				minortask().showTask(taskID);
			}
		}

	}

}
