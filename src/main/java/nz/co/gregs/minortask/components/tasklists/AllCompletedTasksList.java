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
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.Task;

public class AllCompletedTasksList extends AbstractTaskList {

	public AllCompletedTasksList() {
		super();
		setToolTip();
	}

	public AllCompletedTasksList(Task project) {
		super(project);
		setToolTip();
	}

	private void setToolTip() {
		setTooltipText("All the tasks that have been done within this project");
	}

	@Override
	protected String getListClassName() {
		return "allcompletedtaskslist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Completed Tasks";
	}

	Long getProjectID() {
		return getTaskID();
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
		example.completionDate.permitOnlyNotNull();
//		example.userID.permittedValues(minortask().getCurrentUserID());
		final DBQuery query = Globals.getDatabase().getDBQuery(example);
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(minortask().getCurrentUserID())
						.or(
								example.column(example.assigneeID).is(minortask().getCurrentUserID())
						)
		);
		if (getProjectID() == null) {
			List<Task> list = query.getAllInstancesOf(example);
			return list;
		} else {
			example.taskID.permittedValues(getProjectID());
			DBRecursiveQuery<Task> recurse = Globals.getDatabase().getDBRecursiveQuery(query, example.column(example.projectID), example);
			List<Task> descendants = recurse.getDescendants();
			List<Task> tasks = new ArrayList<>();
			descendants.stream().filter((t) -> {
				return t.completionDate.getValue() == null && !t.taskID.getValue().equals(getProjectID());
			}).forEach(tasks::add);
			return tasks;
		}
	}

}
