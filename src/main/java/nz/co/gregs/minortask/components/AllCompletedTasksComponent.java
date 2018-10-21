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
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.tasklists.AbstractTaskList;
import nz.co.gregs.minortask.datamodel.Task;

//@Tag("all-completed-task-list")
public class AllCompletedTasksComponent extends Div implements MinorTaskComponent {

	private List<Task> allTasks = new ArrayList<>();
	private ArrayList<Task> week;
	private ArrayList<Task> month;
	private ArrayList<Task> others;
	private ArrayList<Task> year;

	public AllCompletedTasksComponent() {
		try {
			addClassName("all-completed-tasks-component");
			this.allTasks = getTasksToList();

			splitTasks(allTasks);

			add(new WeeksTaskList(week));
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

	public static Date getStartOfThisWeek() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
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
		cal.set(Calendar.DAY_OF_MONTH, 1);
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
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 0);
		Date startOfYear = cal.getTime();
		return startOfYear;
	}

	protected final List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
		example.userID.permittedValues(getUserID());
		example.completionDate.excludedValues((Date) null);
		example.completionDate.setSortOrderDescending();
		final DBQuery dbTable = getDatabase().getDBQuery(example);
		dbTable.setSortOrder(
				example.column(example.completionDate),
				example.column(example.name),
				example.column(example.taskID)
		);
		List<Task> tasks = dbTable.getAllInstancesOf(example);
		return tasks;
	}

	private void splitTasks(List<Task> allTasks) throws SQLException {
		week = new ArrayList<Task>();
		month = new ArrayList<Task>();
		year = new ArrayList<Task>();
		others = new ArrayList<Task>();
		List<Task> tasksToList = allTasks;
		Date startOfWeek = getStartOfThisWeek();
		Date startOfMonth = getStartOfThisMonth();
		Date startOfYear = getStartOfThisYear();
		for (Task task : tasksToList) {
			if (task.completionDate.getValue() != null && task.completionDate.getValue().after(startOfWeek)) {
				week.add(task);
			} else if (task.completionDate.getValue() != null && task.completionDate.getValue().after(startOfMonth)) {
				month.add(task);
			} else if (task.completionDate.getValue() != null && task.completionDate.getValue().after(startOfYear)) {
				month.add(task);
			} else {
				others.add(task);
			}
		}
	}

	public static class WeeksTaskList extends AbstractTaskList.PreQueried {

		public WeeksTaskList(List<Task> list) {
			super(list);
		}

		@Override
		protected String getListClassName() {
			return "weeks-completedtaskslist";
		}

		@Override
		protected String getListCaption(List<Task> tasks) {
			return "" + (tasks == null ? 0 : tasks.size()) + " Tasks Completed This Week";
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
			return "" + tasks.size() + " Other Tasks Completed This Month";
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
			return "" + tasks.size() + " Other Tasks Completed This Year";
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
