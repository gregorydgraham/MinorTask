/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.datatypes.DBBoolean;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class TaskListComponent extends AuthorisedComponent {

	public TaskListComponent(MinorTaskUI ui, Long selectedTask) {
		super(ui, selectedTask);
	}

	@Override
	public Component getAuthorisedComponent() {

		VerticalLayout layout = new VerticalLayout();
		try {
			layout.addComponent(new ProjectPathNavigatorComponent(ui, currentTaskID).getAuthorisedComponent());
			
			Label actualTaskName = new Label("All");
			final Task actualTask = new Task();
			actualTask.userID.permittedValues(getUserID());
			actualTask.taskID.permittedValues(currentTaskID);
			if (currentTaskID != null) {
				final Task fullTaskDetails = getDatabase().getDBTable(actualTask).getOnlyRow();
				actualTaskName.setValue(fullTaskDetails.name.getValue());
			}
			layout.addComponent(actualTaskName);
			layout.addComponent(new TaskCreationComponent(ui, currentTaskID, actualTask).getAuthorisedComponent());
			TaskWithSortColumns example = new TaskWithSortColumns();
			example.userID.permittedValues(getUserID());
			example.projectID.permittedValues(currentTaskID);
			example.startDate.setSortOrderAscending();
			final DBTable<TaskWithSortColumns> dbTable = getDatabase().getDBTable(example);
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
			sqlerror(ex);
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

			final TextField startdate = new TextField("Start", Helper.asDateString(task.startDate.getValue(), ui));
			final TextField readyDate = new TextField("Ready", Helper.asDateString(task.preferredDate.getValue(), ui));
			final TextField deadline = new TextField("Deadline", Helper.asDateString(task.finalDate.getValue(), ui));

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

	@Override
	public void handleDefaultButton() {
		new TaskCreationComponent(ui, currentTaskID).show();
	}

	@Override
	public void handleEscapeButton() {
		new TasksComponent(ui).show();
	}

	static public class TaskWithSortColumns extends Task {

		@DBColumn
		public DBBoolean hasStarted = new DBBoolean(this.column(this.startDate).isLessThan(DateExpression.currentDate()));

		@DBColumn
		public DBBoolean isOverdue = new DBBoolean(this.column(this.finalDate).isLessThan(DateExpression.currentDate()));

		{
			this.hasStarted.setSortOrderDescending();
			this.isOverdue.setSortOrderDescending();
			this.startDate.setSortOrderAscending();
			this.preferredDate.setSortOrderAscending();
			this.finalDate.setSortOrderAscending();

		}
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
			chat("Switching to "+ task.name.getValue());
			if (event.getButton() == MouseEventDetails.MouseButton.LEFT) {
				new TaskListComponent(ui, task.taskID.getValue()).show();
			}
		}
	}

}
