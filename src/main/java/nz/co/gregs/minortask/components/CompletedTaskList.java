/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.minortask.datamodel.Task;

@Tag("completed-task-list")
public class CompletedTaskList extends AbstractTaskList{

	public CompletedTaskList(Long taskID) {
		super(taskID);
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		example.projectID.permittedValues(taskID);
		example.completionDate.excludedValues((Date) null);
		final DBTable<Task> dbTable = getDatabase().getDBTable(example);
		example.completionDate.setSortOrderDescending();
		dbTable.setSortOrder(
				example.column(example.completionDate),
				example.column(example.name),
				example.column(example.taskID)
		);
		List<Task> tasks = dbTable.getAllRows();
		return tasks;
	}

	@Override
	protected String getListClassName() {
		return "completedtasklist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return ""+tasks.size()+" Completed Tasks";
	}

}
