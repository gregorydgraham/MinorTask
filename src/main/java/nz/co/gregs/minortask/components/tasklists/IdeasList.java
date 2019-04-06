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

	public IdeasList() {
		super();
		setTooltipText("Never lose an idea, just create a task and don't set any dates, you'll find it here when you're ready");
	}
	
	public IdeasList(Task parameter) {
		super(parameter);
		setTooltipText("Never lose an idea, just create a task and don't set any dates, you'll find it here when you're ready");
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
		System.out.println("TASKID: "+getTaskID());
		if (getTaskID() == null) {
			Task.Project example = new Task.Project();
//			example.userID.permittedValues(getCurrentUserID());
			example.startDate.permitOnlyNull();
			example.preferredDate.permitOnlyNull();
			example.preferredDate.permitOnlyNull();
			example.completionDate.permitOnlyNull();
			Task task = new Task();
			final DBQuery query = getDatabase().getDBQuery(example).addOptional(task);
			// add user requirement
			query.addCondition(
					example.column(example.userID).is(getCurrentUserID())
							.or(
									example.column(example.assigneeID).is(getCurrentUserID())
							)
			);
			query.setSortOrder(example.column(example.name).ascending());
			query.printAllRows();
			List<Task> tasks = query.getAllInstancesOf(example);
			return tasks;
		} else {
			List<Task> descendants = minortask().getTasksOfProject(getTaskID());
			List<Task> tasks = new ArrayList<>();
			descendants.stream().filter((t) -> {
				return t.completionDate.getValue() == null 
						&& t.startDate.getValue()==null
						&& t.preferredDate.getValue()==null
						&& t.finalDate.getValue()==null
						&& !t.taskID.getValue().equals(getTaskID());
			}).forEach(tasks::add);
			return tasks;
		}
	}

}
