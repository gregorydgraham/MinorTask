/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.datamodel.Task;

//@Tag("completed-task-list")
public class CompletedTaskList extends AbstractTaskList {

	public CompletedTaskList(Long taskID) {
		super(taskID);
		setTooltipText("When you complete a task it will be moved to here, so you can see your progress");
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
//		example.userID.permittedValues(minortask().getCurrentUserID());
		example.projectID.permittedValues(taskID);
		example.completionDate.permitOnlyNotNull();
		example.completionDate.setSortOrderDescending();
		final DBQuery query = getDatabase().getDBQuery(example);
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(getCurrentUserID())
						.or(
								example.column(example.assigneeID).is(getCurrentUserID())
						)
		);
		query.setSortOrder(
				example.column(example.completionDate).descending(),
				example.column(example.name).ascending(),
				example.column(example.taskID).ascending()
		);
		List<Task> tasks = query.getAllInstancesOf(example);
		return tasks;
	}

	@Override
	protected String getListClassName() {
		return "completedtasklist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Completed Tasks";
	}

}
