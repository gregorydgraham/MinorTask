/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.minortask.components.AddTaskButton;
import nz.co.gregs.minortask.datamodel.Task;

@Tag("active-task-list")
public class ActiveTaskList extends AbstractTaskList {

	private AddTaskButton newTaskButton = new AddTaskButton();

	public ActiveTaskList(Long selectedTask) {
		super(selectedTask);
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		example.projectID.permittedValues(taskID);
		example.completionDate.permittedValues((Date) null);
		final DBTable<Task> dbTable = minortask().getDatabase().getDBTable(example);
		dbTable.setSortOrder(
				example.column(example.isOverdue),
				example.column(example.hasStarted),
				example.column(example.finalDate),
				example.column(example.startDate)
		);
		List<Task> tasks = dbTable.getAllRows();
		return tasks;
	}

	public void disableNewButton() {
		this.getNewTaskButton().setEnabled(false);
	}

	@Override
	protected String getListClassName() {
		return "activelist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Tasks";
	}

	@Override
	protected Component[] getFooterExtras() {
		return new Component[]{getNewTaskButton()};
	}

	/**
	 * @return the newTaskButton
	 */
	public final AddTaskButton getNewTaskButton() {
		if (newTaskButton == null) {
			newTaskButton = new AddTaskButton();
		}
		return newTaskButton;
	}
}
