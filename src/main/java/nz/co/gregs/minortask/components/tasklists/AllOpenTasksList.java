/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.minortask.datamodel.Task;

public class AllOpenTasksList extends AbstractTaskList {

	public AllOpenTasksList() {
		super();
		setToolTip();
	}

	public AllOpenTasksList(Long projectID) {
		super(projectID);
		setToolTip();
	}

	private void setToolTip() {
		setTooltipText("All the tasks that are still be done");
	}

	@Override
	protected String getListClassName() {
		return "allopentaskslist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Open Tasks";
	}

	Long getProjectID() {
		return taskID;
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
		example.completionDate.permitOnlyNull();
//		example.userID.permittedValues(minortask().getUserID());
		final DBQuery query = getDatabase().getDBQuery(example);
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(getUserID())
						.or(
								example.column(example.assigneeID).is(getUserID())
						)
		);
		if (getProjectID() == null) {
			List<Task> list = query.getAllInstancesOf(example);
			return list;
		} else {
			example.taskID.permittedValues(getProjectID());
			DBRecursiveQuery<Task> recurse = getDatabase().getDBRecursiveQuery(query, example.column(example.projectID), example);
			List<Task> descendants = recurse.getDescendants();
			List<Task> tasks = new ArrayList<>();
			descendants.stream().filter((t) -> {
				return t.completionDate.getValue() == null && !t.taskID.getValue().equals(getProjectID());
			}).forEach(tasks::add);
			return tasks;
		}
	}

}
