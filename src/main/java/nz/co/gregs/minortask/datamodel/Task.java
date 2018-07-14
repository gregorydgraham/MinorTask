/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.datamodel;

import java.io.Serializable;
import java.util.Objects;
import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.datatypes.DBBoolean;
import nz.co.gregs.dbvolution.datatypes.DBDate;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBString;
import nz.co.gregs.dbvolution.expressions.DateExpression;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class Task extends DBRow {

	@DBColumn(value = "taskid")
	@DBPrimaryKey
	@DBAutoIncrement
	public final DBInteger taskID = new DBInteger();

	@DBColumn
	@DBForeignKey(User.class)
	public final DBInteger userID = new DBInteger();

	@DBColumn
	@DBForeignKey(Task.Project.class)
	public final DBInteger projectID = new DBInteger();

	@DBColumn
	public final DBString name = new DBString();

	@DBColumn
	public final DBString description = new DBString();

	@DBColumn
	public final DBDate startDate = new DBDate();

	@DBColumn
	public final DBDate preferredDate = new DBDate();

	@DBColumn
	public final DBDate finalDate = new DBDate();

//	@DBColumn
//	public final DBStringEnum<Task.Status> status = new DBStringEnum<Task.Status>();
	@DBColumn
	public final DBDate completionDate = new DBDate();

//	@DBColumn
//	public DBLargeText notes = new DBLargeText();
	@Override
	public String toString() {
		return name.getValue();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task otherTask = (Task) obj;
			if (this.getDefined() && otherTask.getDefined()) {
				return this.taskID.longValue().equals(otherTask.taskID.longValue());
			}
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 37 * hash + Objects.hashCode(this.taskID);
		hash = 37 * hash + Objects.hashCode(this.userID);
		hash = 37 * hash + Objects.hashCode(this.projectID);
		hash = 37 * hash + Objects.hashCode(this.name);
		hash = 37 * hash + Objects.hashCode(this.description);
		hash = 37 * hash + Objects.hashCode(this.startDate);
		hash = 37 * hash + Objects.hashCode(this.preferredDate);
		hash = 37 * hash + Objects.hashCode(this.finalDate);
		hash = 37 * hash + Objects.hashCode(this.completionDate);
		return hash;
	}

	/**
	 *
	 * @author gregorygraham
	 */
	public static class Project extends Task {
	}

	/**
	 *
	 * @author gregorygraham
	 */
	public static class WithSortColumns extends Task {

		@DBColumn
		public DBBoolean hasStarted = new DBBoolean(this.column(this.startDate).isLessThan(DateExpression.currentDate()));
		@DBColumn
		public DBBoolean isOverdue = new DBBoolean(this.column(this.finalDate).isLessThan(DateExpression.currentDate()));

		{
			this.hasStarted.setSortOrderDescending();
			this.isOverdue.setSortOrderDescending();
			this.startDate.setSortOrderAscending();
			this.preferredDate.setSortOrderAscending();
			this.finalDate.setSortOrderAscending();
		}
	}

	public static class TaskAndProject implements Serializable {

		private final Task task;
		private final Project project;

		public TaskAndProject(Task task, Task.Project project) {
			this.task = task;
			this.project = project;
		}

		/**
		 * @return the task
		 */
		public Task getTask() {
			return task;
		}

		/**
		 * @return the project
		 */
		public Project getProject() {
			return project;
		}
	}
}
