/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class OverdueTasksList extends AbstractTaskListOfDBQueryRow {

	public OverdueTasksList(Task task) {
		super(task);
		setTooltipText("This list show tasks that has passed their deadline, you might need to reschedule them or get them done now");
	}

	public OverdueTasksList() {
		super();
		setTooltipText("This list show tasks that has passed their deadline, you might need to reschedule them or get them done now");
	}

	@Override
	protected String getListClassName() {
		return "overduetaskslist";
	}

	@Override
	protected String getListCaption(List<DBQueryRow> tasks) {
		return tasks.size() + " Overdue Tasks";
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		if (getTaskID() == null) {
			Task.Project.WithSortColumns example = new Task.Project.WithSortColumns();
			example.finalDate.permittedRangeExclusive(null, new Date());
			example.completionDate.permittedValues((Date) null);
			final Task task = new Task();
			final DBQuery query = getDatabase().getDBQuery(example).addOptional(task);
			// add user requirement
			query.addCondition(
					example.column(example.userID).is(getCurrentUserID())
							.or(
									example.column(example.assigneeID).is(getCurrentUserID())
							)
			);
			query.addCondition(task.column(task.taskID).isNull());
			query.setSortOrder(
					example.column(example.isOverdue).descending(),
					example.column(example.hasStarted).descending(),
					example.column(example.finalDate).ascending(),
					example.column(example.startDate).ascending(),
					example.column(example.name).ascending()
			);
			query.printAllRows();
			return query.getAllRows();
		} else {
			List<Task> descendants = minortask().getTasksOfProject(getTaskID());

			final Date now = new Date();
			List<Long> taskIDs = descendants
					.stream()
					.filter((t) -> 
							t.completionDate.getValue() == null
						&& !t.taskID.getValue().equals(getTaskID())
						&& t.finalDate.getValue() != null
						&& t.finalDate.getValue().before(now)
					)
					.map((t) -> t.taskID.getValue())
					.collect(Collectors.toList());
			Task example = new Task();
			example.taskID.permittedValues(taskIDs.toArray(new Long[]{}));
			DBQuery query = getDatabase().getDBQuery(example);
			query.addCondition(
					example.column(example.userID).is(minortask().getCurrentUserID())
							.or(
									example.column(example.assigneeID).is(minortask().getCurrentUserID())
							)
			);
			List<DBQueryRow> list = query.getAllRows();
			return list;
		}
	}

}
