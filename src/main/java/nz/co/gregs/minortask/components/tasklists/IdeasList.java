/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.datamodel.FavouritedTasks;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class IdeasList extends AbstractTaskListOfDBQueryRow {

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
	protected String getListCaption(List<DBQueryRow> tasks) {
		return tasks.size() + " Ideas";
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		System.out.println("TASKID: " + getTaskID());
		if (getTaskID() == null) {
			Task example = new Task();
			example.startDate.permitOnlyNull();
			example.finalDate.permitOnlyNull();
			example.completionDate.permitOnlyNull();
			DBQuery query = getDatabase().getDBQuery(example).addOptional(new Task.Project(), new FavouritedTasks());
			// add user requirement
			query.addCondition(
					example.column(example.userID).is(getCurrentUserID())
							.or(
									example.column(example.assigneeID).is(getCurrentUserID())
							)
			);
			query.setSortOrder(example.column(example.name).ascending());
			return query.getAllRows();
		} else {
			return minortask().getDBQueryRowOfProjectFiltered(
					getTaskID(),
					(t) -> t.startDate.getValue() == null 
							&&  t.finalDate.getValue() == null 
							&&  t.completionDate.getValue() == null 
			);
		}
	}

}
