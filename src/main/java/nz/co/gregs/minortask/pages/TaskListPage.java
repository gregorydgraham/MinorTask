/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.datatypes.DBBoolean;
import nz.co.gregs.dbvolution.expressions.DateExpression;
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

		TaskWithSortColumns example = new TaskWithSortColumns();
		example.userID.permittedValues(getUserID());
		example.projectID.permittedValues(projectID);
		example.startDate.setSortOrderAscending();
		final DBTable<TaskWithSortColumns> dbTable = getDatabase().getDBTable(example);
		dbTable.setSortOrder(
				example.column(example.isOverdue),
				example.column(example.hasStarted),
				example.column(example.finalDate),
				example.column(example.startDate)
		);
		try {
			List<TaskWithSortColumns> tasks = dbTable.getAllRows();
			layout.addComponent(new Label(tasks.size() + " Tasks Found"));
			GridLayout gridlayout = new GridLayout(4, 4);
			for (Task task : tasks) {
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

}
