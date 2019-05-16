/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.datamodel.FavouritedTasks;
import nz.co.gregs.minortask.datamodel.Task;


public class FavouritedTasksList extends AbstractTaskListOfDBQueryRow {

	public FavouritedTasksList() {
		setTooltipText("Tick the heart to include a task in this list, then you be able to find it easily");
	}

	@Override
	protected String getListClassName() {
		return "favouritedtasks";
	}

	@Override
	protected Component getListCaption(List<DBQueryRow> tasks) {
		return new Label(""+tasks.size()+" favourited tasks");
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		final FavouritedTasks favourite = new FavouritedTasks();
		favourite.userID.permittedValues(getCurrentUserID());
		final Task task = new Task();
		DBQuery query = getDatabase().getDBQuery(task, favourite).addOptional(new Task.Project());
		query.setSortOrder(task.column(task.name).ascending());
		return query.getAllRows();
	}
	
}
