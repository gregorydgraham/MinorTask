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
		final Task example = new Task();
		example.projectID.permitOnlyNull();
		DBQuery query = getDatabase().getDBQuery(example);
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(getUserID())
						.or(
								example.column(example.assigneeID).is(getUserID())
						)
		);
		DBRecursiveQuery<Task> rquery = new DBRecursiveQuery<>(query, example.column(example.projectID));
		return rquery.getDescendants();
	}

}
