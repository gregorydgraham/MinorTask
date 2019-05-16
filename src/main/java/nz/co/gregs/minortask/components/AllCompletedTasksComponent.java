/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.tasklists.AbstractTaskListOfDBQueryRow;
import nz.co.gregs.minortask.datamodel.Task;

//@Tag("all-completed-task-list")
public class AllCompletedTasksComponent extends Div implements MinorTaskComponent {

	private final ArrayList<DBQueryRow> today = new ArrayList<>();
	private final ArrayList<DBQueryRow> week = new ArrayList<>();
	private final ArrayList<DBQueryRow> month = new ArrayList<>();
	private final ArrayList<DBQueryRow> others = new ArrayList<>();
	private final ArrayList<DBQueryRow> year = new ArrayList<>();
	private Task task = null;

	public AllCompletedTasksComponent() {
		this(null);
	}

	public AllCompletedTasksComponent(Task parameter) {
		this.task = parameter;
		addClassName("all-completed-tasks-component");
		add(todaysCompletedTasksList);
		add(Globals.getSpacer());
		add(weeksCompletedTasksList);
		add(Globals.getSpacer());
		add(thisMonthsCompletedTasksList);
		add(Globals.getSpacer());
		add(thisYearsCompletedTasksList);
		add(Globals.getSpacer());
		add(tooManyCompletedTasksList);
	}

	public TooManyCompletedTasksList tooManyCompletedTasksList = new TooManyCompletedTasksList(others);
	public ThisYearsCompletedTasksList thisYearsCompletedTasksList = new ThisYearsCompletedTasksList(year);
	public ThisMonthsCompletedTasksList thisMonthsCompletedTasksList = new ThisMonthsCompletedTasksList(month);
	public WeeksCompletedTasksList weeksCompletedTasksList = new WeeksCompletedTasksList(week);
	public TodaysCompletedTasksList todaysCompletedTasksList = new TodaysCompletedTasksList(today);

	public void refresh() {
		try {
			List<DBQueryRow> allTasks = getTasksToList();
			splitTasks(allTasks);
			todaysCompletedTasksList.refresh();
			weeksCompletedTasksList.refresh();
			thisMonthsCompletedTasksList.refresh();
			thisYearsCompletedTasksList.refresh();
			tooManyCompletedTasksList.refresh();
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public static Date getStartOfToday() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.add(Calendar.DATE, -1);
		Date startOfWeek = cal.getTime();
		return startOfWeek;
	}

	public static Date getStartOfThisWeek() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.add(Calendar.DATE, -7);
		Date startOfWeek = cal.getTime();
		return startOfWeek;
	}

	public static Date getStartOfThisMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

// get start of the month
		cal.add(Calendar.MONTH, -1);
		Date startOfMonth = cal.getTime();
		return startOfMonth;
	}

	public static Date getStartOfThisYear() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

// get start of the month
		cal.add(Calendar.YEAR, -1);
		Date startOfYear = cal.getTime();
		return startOfYear;
	}

	protected final List<DBQueryRow> getTasksToList() throws SQLException {
		if (task == null) {
			// non-recursive query is faster
			Task example = new Task();
			example.userID.permittedValues(getCurrentUserID());
			example.completionDate.excludedValues((Date) null);
			final DBQuery dbTable = getDatabase().getDBQuery(example);
			dbTable.setSortOrder(
					example.column(example.completionDate).descending(),
					example.column(example.name).ascending(),
					example.column(example.taskID).ascending()
			);
			return dbTable.getAllRows();
		} else {
			List<Task> descendants = minortask().getTasksOfProject(task == null ? null : task.taskID.getValue());

			List<Long> taskIDs = descendants
					.stream()
					.filter((t)
							-> t.completionDate.getValue() != null && !t.taskID.getValue().equals(task.taskID.getValue())
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
			query.setSortOrder(
					example.column(example.completionDate).descending(),
					example.column(example.name).ascending(),
					example.column(example.taskID).ascending()
			);
			List<DBQueryRow> list = query.getAllRows();
			return list;
		}
	}

	private void splitTasks(List<DBQueryRow> allTasks) throws SQLException {
		today.clear();
		week.clear();
		month.clear();
		year.clear();
		others.clear();
		
		List<DBQueryRow> tasksToList = allTasks;
		Date startOfToday = getStartOfToday();
		Date startOfWeek = getStartOfThisWeek();
		Date startOfMonth = getStartOfThisMonth();
		Date startOfYear = getStartOfThisYear();
		tasksToList.forEach((row) -> {
			Task t = row.get(new Task());
			if (t.completionDate.getValue() != null && t.completionDate.getValue().after(startOfToday)) {
				today.add(row);
			} else if (t.completionDate.getValue() != null && t.completionDate.getValue().after(startOfWeek)) {
				week.add(row);
			} else if (t.completionDate.getValue() != null && t.completionDate.getValue().after(startOfMonth)) {
				month.add(row);
			} else if (t.completionDate.getValue() != null && t.completionDate.getValue().after(startOfYear)) {
				year.add(row);
			} else {
				others.add(row);
			}
		});
	}

	public static class TodaysCompletedTasksList extends AbstractTaskListOfDBQueryRow.PreQueried {

		public TodaysCompletedTasksList(List<DBQueryRow> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "todays-completedtaskslist";
		}

		@Override
		protected Label getListCaption(List<DBQueryRow> tasks) {
			return new Label("" + (tasks == null ? 0 : tasks.size()) + " Tasks Completed Today (until " + AllCompletedTasksComponent.getStartOfToday() + ")");
		}

	}

	public static class WeeksCompletedTasksList extends AbstractTaskListOfDBQueryRow.PreQueried {

		public WeeksCompletedTasksList(List<DBQueryRow> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "weeks-completedtaskslist";
		}

		@Override
		protected Label getListCaption(List<DBQueryRow> tasks) {
			return new Label("" + (tasks == null ? 0 : tasks.size()) + " Tasks Completed This Week (until " + AllCompletedTasksComponent.getStartOfThisWeek() + ")");
		}

	}

	public static class ThisMonthsCompletedTasksList extends AbstractTaskListOfDBQueryRow.PreQueried {

		public ThisMonthsCompletedTasksList(List<DBQueryRow> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "months-completedtaskslist";
		}

		@Override
		protected Label getListCaption(List<DBQueryRow> tasks) {
			return new Label("" + tasks.size() + " Other Tasks Completed This Month (until " + AllCompletedTasksComponent.getStartOfThisMonth() + ")");
		}

	}

	public static class ThisYearsCompletedTasksList extends AbstractTaskListOfDBQueryRow.PreQueried {

		public ThisYearsCompletedTasksList(List<DBQueryRow> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "years-completedtaskslist";
		}

		@Override
		protected Label getListCaption(List<DBQueryRow> tasks) {
			return new Label("" + tasks.size() + " Other Tasks Completed This Year (until " + AllCompletedTasksComponent.getStartOfThisYear() + ")");
		}

	}

	public static class TooManyCompletedTasksList extends AbstractTaskListOfDBQueryRow.PreQueried {

		public TooManyCompletedTasksList(List<DBQueryRow> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "others-completedtaskslist";
		}

		@Override
		protected Label getListCaption(List<DBQueryRow> tasks) {
			return new Label("" + tasks.size() + " Older Completed Tasks");
		}

	}
}
