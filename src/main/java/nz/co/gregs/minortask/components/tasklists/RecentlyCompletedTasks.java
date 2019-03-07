/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.datamodel.Task;

//@Tag("completed-task-list")
public class RecentlyCompletedTasks extends AbstractTaskList {

	public RecentlyCompletedTasks() {
		super();
		setTooltipText("When you complete a task it will be moved to here, so you can see your progress");
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(GregorianCalendar.DAY_OF_YEAR, -7);
		example.completionDate.permittedRange(cal.getTime(), null);
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
		query.setPageSize(10);
		List<Task> tasks = query.getAllInstancesOf(example);
		return tasks;
	}

	@Override
	protected String getListClassName() {
		return "recentlycompletedtasklist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Recently Completed";
	}

	@Override
	protected Component getSubTaskNumberComponent(Task task) {
		return new Span();
	}

	@Override
	protected Component getPrefixComponent(Task task) {
		return new Span();
	}
}
