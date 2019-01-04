/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.minortask.components.AddTaskButton;
import nz.co.gregs.minortask.datamodel.Task;


@StyleSheet("styles/open-task-list.css")
public class OpenTaskList extends AbstractTaskList {

	private AddTaskButton newTaskButton;

	public OpenTaskList(Long taskID) {
		super(taskID);
		 newTaskButton= new AddTaskButton(taskID);
	}

	@Override
	protected String getListClassName() {
		return "opentasks";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Open Subtasks";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		example.projectID.permittedValues(taskID);
		example.completionDate.permittedValues((Date) null);
		final DBTable<Task> dbTable = getDatabase().getDBTable(example);
		dbTable.setSortOrder(
				example.column(example.finalDate).isLessThan(DateExpression.currentDate()).descending(),
				example.column(example.startDate).isLessThan(DateExpression.currentDate()).descending(),
				example.column(example.finalDate).ascending(),
				example.column(example.startDate).ascending(),
				example.column(example.name).ascending()
		);
		List<Task> tasks = dbTable.getAllRows();
		return tasks;
	}

	/**
	 * @return the newTaskButton
	 */
	public final AddTaskButton getNewTaskButton() {
		if (newTaskButton == null) {
			newTaskButton = new AddTaskButton(taskID);
		}
		return newTaskButton;
	}
	
	@Override
	protected Component[] getFooterExtras() {
		return new Component[]{getNewTaskButton()};
	}

	public void disableNewButton() {
		this.getNewTaskButton().setEnabled(false);
	}
	
}
