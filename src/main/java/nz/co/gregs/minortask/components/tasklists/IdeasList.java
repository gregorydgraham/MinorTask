 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class IdeasList extends AbstractTaskList {
	
//	private final IdeasPage list;

	public IdeasList(Long taskID) {
		super(taskID);
//		this.list = list;
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
		Task example = new Task.Project();
		example.userID.permittedValues(minortask().getUserID());
		example.startDate.permitOnlyNull();
		example.preferredDate.permitOnlyNull();
		example.preferredDate.permitOnlyNull();
		example.completionDate.permitOnlyNull();
		Task task = new Task();
		final DBQuery query = Globals.getDatabase().getDBQuery(example).addOptional(task);
		// add the leaf requirement
		query.addCondition(example.column(example.taskID).isNull());
		query.setSortOrder(example.column(example.name));
		query.printAllRows();
		List<Task> tasks = query.getAllInstancesOf(example);
		return tasks;
	}
	
}
