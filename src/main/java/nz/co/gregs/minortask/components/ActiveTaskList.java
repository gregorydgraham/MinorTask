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


public class ActiveTaskList extends TaskListComponent {

	public ActiveTaskList(MinorTaskUI ui, Long selectedTask) {
		super(ui, selectedTask);
	}

	@Override
	protected List<Task.WithSortColumns> getTasksToList() throws SQLException {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		example.projectID.permittedValues(getTaskID());
		final DBTable<Task.WithSortColumns> dbTable =Helper.getDatabase().getDBTable(example);
		dbTable.setSortOrder(
				example.column(example.isOverdue),
				example.column(example.hasStarted),
				example.column(example.finalDate),
				example.column(example.startDate)
		);
		List<Task.WithSortColumns> tasks = dbTable.getAllRows();
		return tasks;
	}

	@Override
	protected String getTaskDescriptor() {
		return "Active";
	}
	
}
