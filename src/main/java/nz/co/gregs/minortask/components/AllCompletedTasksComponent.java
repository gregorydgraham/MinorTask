/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.html.Div;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.tasklists.AbstractTaskList;
import nz.co.gregs.minortask.datamodel.Task;

//@Tag("all-completed-task-list")
public class AllCompletedTasksComponent extends Div implements MinorTaskComponent {

	private List<Task> allTasks = new ArrayList<>();
	private ArrayList<Task> today;
	private ArrayList<Task> week;
	private ArrayList<Task> month;
	private ArrayList<Task> others;
	private ArrayList<Task> year;
	private Long taskID = null;

	public AllCompletedTasksComponent() {
		this(null);
	}

	public AllCompletedTasksComponent(Long parameter) {
		this.taskID = parameter;
		try {
			addClassName("all-completed-tasks-component");
			this.allTasks = getTasksToList();

			splitTasks(allTasks);

			add(new TodaysCompletedTasksList(today));
			add(Globals.getSpacer());
			add(new WeeksCompletedTasksList(week));
			add(Globals.getSpacer());
			add(new ThisMonthsCompletedTasksList(month));
			add(Globals.getSpacer());
			add(new ThisYearsCompletedTasksList(year));
			add(Globals.getSpacer());
			add(new TooManyCompletedTasksList(others));
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

	protected final List<Task> getTasksToList() throws SQLException {
		if (taskID == null) {
			// non-recursive query is faster
			Task example = new Task();
			example.userID.permittedValues(getCurrentUserID());
			example.completionDate.excludedValues((Date) null);
			example.completionDate.setSortOrderDescending();
			final DBQuery dbTable = getDatabase().getDBQuery(example);
			dbTable.setSortOrder(
					example.column(example.completionDate).descending(),
					example.column(example.name).ascending(),
					example.column(example.taskID).ascending()
			);
			List<Task> tasks = dbTable.getAllInstancesOf(example);
			return tasks;
		} else {
			Task example = new Task();
			example.taskID.permittedValues(taskID);
//			example.userID.permittedValues(getCurrentUserID());
			DBQuery query = getDatabase().getDBQuery(example);
			DBRecursiveQuery<Task> recurse = getDatabase().getDBRecursiveQuery(query, example.column(example.projectID), example);
			List<Task> descendants = recurse.getDescendants();
			List<Task> tasks = new ArrayList<>();
			descendants.stream().filter((t) -> {
				return t.completionDate.getValue() != null && !t.taskID.getValue().equals(taskID);
			}).forEach(tasks::add);
			return tasks;
		}
	}

	private void splitTasks(List<Task> allTasks) throws SQLException {
		today = new ArrayList<Task>();
		week = new ArrayList<Task>();
		month = new ArrayList<Task>();
		year = new ArrayList<Task>();
		others = new ArrayList<Task>();
		List<Task> tasksToList = allTasks;
		Date startOfToday = getStartOfToday();
		Date startOfWeek = getStartOfThisWeek();
		Date startOfMonth = getStartOfThisMonth();
		Date startOfYear = getStartOfThisYear();
		for (Task task : tasksToList) {
			if (task.completionDate.getValue() != null && task.completionDate.getValue().after(startOfToday)) {
				today.add(task);
			} else if (task.completionDate.getValue() != null && task.completionDate.getValue().after(startOfWeek)) {
				week.add(task);
			} else if (task.completionDate.getValue() != null && task.completionDate.getValue().after(startOfMonth)) {
				month.add(task);
			} else if (task.completionDate.getValue() != null && task.completionDate.getValue().after(startOfYear)) {
				year.add(task);
			} else {
				others.add(task);
			}
		}
	}

	public static class TodaysCompletedTasksList extends AbstractTaskList.PreQueried {

		public TodaysCompletedTasksList(List<Task> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "todays-completedtaskslist";
		}

		@Override
		protected String getListCaption(List<Task> tasks) {
			return "" + (tasks == null ? 0 : tasks.size()) + " Tasks Completed Today (until " + AllCompletedTasksComponent.getStartOfToday() + ")";
		}

	}

	public static class WeeksCompletedTasksList extends AbstractTaskList.PreQueried {

		public WeeksCompletedTasksList(List<Task> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "weeks-completedtaskslist";
		}

		@Override
		protected String getListCaption(List<Task> tasks) {
			return "" + (tasks == null ? 0 : tasks.size()) + " Tasks Completed This Week (until " + AllCompletedTasksComponent.getStartOfThisWeek() + ")";
		}

	}

	public static class ThisMonthsCompletedTasksList extends AbstractTaskList.PreQueried {

		public ThisMonthsCompletedTasksList(List<Task> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "months-completedtaskslist";
		}

		@Override
		protected String getListCaption(List<Task> tasks) {
			return "" + tasks.size() + " Other Tasks Completed This Month (until " + AllCompletedTasksComponent.getStartOfThisMonth() + ")";
		}

	}

	public static class ThisYearsCompletedTasksList extends AbstractTaskList.PreQueried {

		public ThisYearsCompletedTasksList(List<Task> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "years-completedtaskslist";
		}

		@Override
		protected String getListCaption(List<Task> tasks) {
			return "" + tasks.size() + " Other Tasks Completed This Year (until " + AllCompletedTasksComponent.getStartOfThisYear() + ")";
		}

	}

	public static class TooManyCompletedTasksList extends AbstractTaskList.PreQueried {

		public TooManyCompletedTasksList(List<Task> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "others-completedtaskslist";
		}

		@Override
		protected String getListCaption(List<Task> tasks) {
			return "" + tasks.size() + " Older Completed Tasks";
		}

	}
}
