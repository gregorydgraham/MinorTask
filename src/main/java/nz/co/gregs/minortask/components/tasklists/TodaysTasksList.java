/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.datamodel.Task;

//@Tag("todays-task-list")
public class TodaysTasksList extends AbstractTaskList {

	public TodaysTasksList(Long taskID) {
		super(taskID);
		setTooltipText("Set a start date on the task to have it appear here when you need to start it");
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		if (getTaskID() == null) {
			Task.Project example = new Task.Project();
			example.startDate.permittedRangeInclusive(null, new Date());
			example.completionDate.permitOnlyNull();
			final Task task = new Task();
			task.completionDate.permitOnlyNull();
			final DBQuery query = getDatabase().getDBQuery(example).addOptional(task);
			// add user requirement
			query.addCondition(
					example.column(example.userID).is(getCurrentUserID())
							.or(
									example.column(example.assigneeID).is(getCurrentUserID())
							)
			);
			// add the leaf requirement
			query.addCondition(task.column(task.taskID).isNull());
			query.setSortOrder(
					example.column(example.finalDate).ascending().nullsLast(),
					example.column(example.startDate).ascending().nullsLast(),
					example.column(example.name).ascending()
			);
			List<Task> tasks = query.getAllInstancesOf(new Task.Project());
			return tasks;
		} else {
			List<Task> descendants = minortask().getTasksOfProject(getTaskID());
			List<Task> tasks = new ArrayList<>();
			final Date now = new Date();
			descendants.stream().filter((t) -> {
				return t.completionDate.getValue() == null
						&& !t.taskID.getValue().equals(getTaskID())
						&& t.startDate.getValue() != null
						&& t.startDate.getValue().before(now);
			}).forEach(tasks::add);
			return tasks;
		}
	}

	@Override
	protected String getListClassName() {
		return "todaystasklist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " for Today (open tasks with a start date  in the past)";
	}
}
