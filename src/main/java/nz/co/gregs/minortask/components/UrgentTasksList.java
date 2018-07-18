/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Tag;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.datamodel.Task;

@Tag("active-task-list")
public class UrgentTasksList extends AbstractTaskList {

	@Override
	public String getListClassName() {
		return "urgenttasklist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return tasks.size() + " Upcoming Tasks";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task.Project.WithSortColumns example = new Task.Project.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		final GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.add(GregorianCalendar.DATE, +3);
		Date threeDaysHence = gregorianCalendar.getTime();
		example.finalDate.permittedRange(null, threeDaysHence);
		example.completionDate.permittedValues((Date) null);
		final Task task = new Task();
		final DBQuery query = minortask().getDatabase().getDBQuery(example).add(task);
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
