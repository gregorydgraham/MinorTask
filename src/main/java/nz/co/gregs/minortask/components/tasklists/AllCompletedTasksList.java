/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.datamodel.Task;
import org.joda.time.Period;

//@Tag("all-completed-task-list")
public class AllCompletedTasksList extends Div {

	public AllCompletedTasksList(Long parameter) {
		add(new ThisWeeksCompletedTasksList(parameter));
		add(AbstractTaskList.getSpacer());
		add(new ThisMonthsCompletedTasksList(parameter));
		add(AbstractTaskList.getSpacer());
		add(new TooManyCompletedTasksList(parameter));
		addClassName("all-completed-tasks-component");
	}

	public static class ThisWeeksCompletedTasksList extends AbstractTaskList {

		public ThisWeeksCompletedTasksList(Long taskID) {
			super(taskID);
		}

		@Override
		protected List<Task> getTasksToList() throws SQLException {
			Task example = new Task();
			example.userID.permittedValues(minortask().getUserID());
			example.completionDate.excludedValues((Date) null);
			example.completionDate.setSortOrderDescending();
			final DBQuery dbTable = getDatabase().getDBQuery(example);
			dbTable.addCondition(example.column(example.completionDate).weeksFrom(new Date()).isBetween(-1, 1));
			dbTable.setSortOrder(
					example.column(example.completionDate),
					example.column(example.name),
					example.column(example.taskID)
			);
			List<Task> tasks = dbTable.getAllInstancesOf(example);
			return tasks;
		}

		@Override
		protected String getListClassName() {
			return "allcompletedtaskslist";
		}

		@Override
		protected String getListCaption(List<Task> tasks) {
			return "" + tasks.size() + " Tasks Completed "
					+ " This Week";
		}

	}

	public static class ThisMonthsCompletedTasksList extends AbstractTaskList {

		public ThisMonthsCompletedTasksList(Long taskID) {
			super(taskID);
		}

		@Override
		protected List<Task> getTasksToList() throws SQLException {
			Task example = new Task();
			example.userID.permittedValues(minortask().getUserID());
			example.completionDate.excludedValues((Date) null);
			example.completionDate.setSortOrderDescending();
			final DBQuery dbTable = getDatabase().getDBQuery(example);
			dbTable.addCondition(
					example.column(example.completionDate).weeksFrom(new Date()).isBetween(-1, 1).not()
					.and(example.column(example.completionDate).plus(new Period().withDays(30)).isGreaterThan(new Date())));
			dbTable.setSortOrder(
					example.column(example.completionDate),
					example.column(example.name),
					example.column(example.taskID)
			);
			List<Task> tasks = dbTable.getAllInstancesOf(example);
			return tasks;
		}

		@Override
		protected String getListClassName() {
			return "allcompletedtaskslist";
		}

		@Override
		protected String getListCaption(List<Task> tasks) {
			return "" + tasks.size() + " Tasks Completed This Month";
		}

	}

	public static class TooManyCompletedTasksList extends AbstractTaskList {

		public TooManyCompletedTasksList(Long taskID) {
			super(taskID);
		}

		@Override
		protected List<Task> getTasksToList() throws SQLException {
			Task example = new Task();
			example.userID.permittedValues(minortask().getUserID());
			example.completionDate.excludedValues((Date) null);
			example.completionDate.setSortOrderDescending();
			final DBQuery dbTable = getDatabase().getDBQuery(example);
			dbTable.addCondition(example.column(example.completionDate).plus(new Period().withDays(30)).isLessThan(new Date()));
			dbTable.setSortOrder(
					example.column(example.completionDate),
					example.column(example.name),
					example.column(example.taskID)
			);
			List<Task> tasks = dbTable.getAllInstancesOf(example);
			return tasks;
		}

		@Override
		protected String getListClassName() {
			return "allcompletedtaskslist";
		}

		@Override
		protected String getListCaption(List<Task> tasks) {
			return "" + tasks.size() + " Completed Tasks";
		}

	}
}
