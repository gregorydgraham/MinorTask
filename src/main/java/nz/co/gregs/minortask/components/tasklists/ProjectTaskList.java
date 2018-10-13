/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
//@Tag("project-task-list")
public class ProjectTaskList extends AbstractTaskList {
	
	public ProjectTaskList() {
		super();
	}

	@Override
	protected String getListClassName() {
		return "projecttasklist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Project Tasks";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		final Task task = new Task();
		task.projectID.permitOnlyNull();
		DBQuery dbQuery = getDatabase().getDBQuery(task);
		DBRecursiveQuery<Task> rquery = new DBRecursiveQuery<>(dbQuery, task.column(task.projectID));
		return rquery.getDescendants();
	}
	
	
	
}
