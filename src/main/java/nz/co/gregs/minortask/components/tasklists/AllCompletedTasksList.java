/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Tag;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.minortask.datamodel.Task;

@Tag("all-completed-task-list")
public class AllCompletedTasksList extends AbstractTaskList{

	public AllCompletedTasksList(Long taskID) {
		super(taskID);
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
		example.userID.permittedValues(minortask().getUserID());
		example.completionDate.excludedValues((Date) null);
		example.completionDate.setSortOrderDescending();
		final DBTable<Task> dbTable = getDatabase().getDBTable(example);
		dbTable.setSortOrder(
				example.column(example.completionDate),
				example.column(example.name),
				example.column(example.taskID)
		);
		List<Task> tasks =dbTable.getRowsByExample(example);
		return tasks;
	}

	@Override
	protected String getListClassName() {
		return "allcompletedtaskslist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return ""+tasks.size()+" Completed Tasks";
	}

}
