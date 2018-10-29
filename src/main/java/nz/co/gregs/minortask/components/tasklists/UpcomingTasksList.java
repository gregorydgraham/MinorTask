/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

//@Tag("upcoming-task-list")
public class UpcomingTasksList extends AbstractTaskList {
	
	private static final int DAYS_AHEAD = +3;

	public UpcomingTasksList() {
		super();
	}

	@Override
	public String getListClassName() {
		return "upcomingtasklist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return tasks.size() + " Tasks will start within "+DAYS_AHEAD+" days";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task.Project.WithSortColumns example = new Task.Project.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		LocalDate now = LocalDate.now();
		final LocalDate plusDays = now.plusDays(DAYS_AHEAD);
		Date threeDaysHence = asDate(plusDays);
		
		example.startDate.permittedRange(asDate(now), threeDaysHence);
		example.completionDate.permitOnlyNull();
		final Task task = new Task();
		final DBQuery query = MinorTask.getDatabase().getDBQuery(example).addOptional(task);
		query.addCondition(task.column(task.taskID).isNull());
		query.setSortOrder(
				example.column(example.isOverdue),
				example.column(example.hasStarted),
				example.column(example.finalDate),
				example.column(example.startDate)
		);
		List<Task> tasks = query.getAllInstancesOf(example);
		return tasks;
	}
}
