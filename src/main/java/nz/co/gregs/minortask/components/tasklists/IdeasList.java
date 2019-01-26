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
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class IdeasList extends AbstractTaskList {

	public IdeasList(Long parameter) {
		super(parameter);
	}

	@Override
	protected String getListClassName() {
		return "ideaslist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return tasks.size() + " Ideas";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		System.out.println("TASKID: "+taskID);
		if (taskID == null) {
			Task.Project example = new Task.Project();
//			example.userID.permittedValues(getUserID());
			example.startDate.permitOnlyNull();
			example.preferredDate.permitOnlyNull();
			example.preferredDate.permitOnlyNull();
			example.completionDate.permitOnlyNull();
			Task task = new Task();
			final DBQuery query = getDatabase().getDBQuery(example).addOptional(task);
			// add user requirement
			query.addCondition(
					example.column(example.userID).is(getUserID())
							.or(
									example.column(example.assigneeID).is(getUserID())
							)
			);
			query.setSortOrder(example.column(example.name).ascending());
			query.printAllRows();
			List<Task> tasks = query.getAllInstancesOf(example);
			return tasks;
		} else {
			List<Task> descendants = minortask().getTasksOfProject(taskID);
			List<Task> tasks = new ArrayList<>();
			descendants.stream().filter((t) -> {
				return t.completionDate.getValue() == null 
						&& t.startDate.getValue()==null
						&& t.preferredDate.getValue()==null
						&& t.finalDate.getValue()==null
						&& !t.taskID.getValue().equals(taskID);
			}).forEach(tasks::add);
			return tasks;
		}
	}

}
