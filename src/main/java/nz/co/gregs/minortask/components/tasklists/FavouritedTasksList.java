/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.datamodel.FavouritedTasks;
import nz.co.gregs.minortask.datamodel.Task;


public class FavouritedTasksList extends AbstractTaskList {

	public FavouritedTasksList() {
		setTooltipText("Tick the heart to include a task in this list, then you be able to find it easily");
	}

	@Override
	protected String getListClassName() {
		return "favouritedtasks";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return ""+tasks.size()+" favourited tasks";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		final FavouritedTasks favourite = new FavouritedTasks();
		favourite.userID.permittedValues(getCurrentUserID());
		final Task task = new Task();
		DBQuery query = getDatabase().getDBQuery(task, favourite);
		query.setSortOrder(task.column(task.name).ascending());
		return query.getAllInstancesOf(task);
	}
	
}
