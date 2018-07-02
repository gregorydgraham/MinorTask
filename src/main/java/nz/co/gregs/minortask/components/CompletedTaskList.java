/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

public class CompletedTaskList extends TaskListComponent {

	public CompletedTaskList(MinorTaskUI ui, Long selectedTask) {
		super(ui, selectedTask);
	}

	@Override
	protected String getTaskDescriptor() {
		return "Completed";
	}

	@Override
	protected List<Task.WithSortColumns> getTasksToList() throws SQLException {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		example.projectID.permittedValues(getTaskID());
		example.status.permittedValues(Task.Status.COMPLETED);
		final DBTable<Task.WithSortColumns> dbTable = Helper.getDatabase().getDBTable(example);
		example.completionDate.setSortOrderDescending();
		dbTable.setSortOrder(
				example.column(example.completionDate),
				example.column(example.name),
				example.column(example.taskID)
		);
		List<Task.WithSortColumns> tasks = dbTable.getAllRows();
		return tasks;
	}

}
