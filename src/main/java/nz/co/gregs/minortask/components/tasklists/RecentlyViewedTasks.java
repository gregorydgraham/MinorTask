/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.TaskViews;


public class RecentlyViewedTasks extends AbstractTaskList {

	public RecentlyViewedTasks() {
		setTooltipText("This list shows the tasks that you've viewed with the most recent one at the top");
	}

	@Override
	protected String getListClassName() {
		return "recentlyviewedtasks";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return ""+tasks.size()+" Tasks viewed in the last week";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		final TaskViews taskViews = new TaskViews();
		taskViews.userID.permittedValues(getCurrentUserID());
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(GregorianCalendar.DAY_OF_YEAR, -7);
		taskViews.lastviewed.permittedRange(cal.getTime(), null);
		final Task task = new Task();
		DBQuery query = getDatabase().getDBQuery(task, taskViews);
		query.setSortOrder(
				taskViews.column(taskViews.lastviewed).descending(),
				task.column(task.name).ascending());
		return query.getAllInstancesOf(task);
	}
	
}
