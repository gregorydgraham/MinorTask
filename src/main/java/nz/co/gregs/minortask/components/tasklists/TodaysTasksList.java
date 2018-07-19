/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Tag;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.datamodel.Task;

@Tag("todays-task-list")
public class TodaysTasksList extends AbstractTaskList {

	public TodaysTasksList(Long taskID) {
		super(taskID);
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task.Project example = new Task.Project();
		example.userID.permittedValues(minortask().getUserID());
		example.completionDate.permittedValues((Date) null);
		final Task task = new Task();
		final DBQuery query = minortask().getDatabase().getDBQuery(example).addOptional(task);
		query.setSortOrder(
				example.column(example.finalDate),
				example.column(example.startDate)
		);
		// add the leaf requirement
		query.addCondition(task.column(task.taskID).isNull());
		List<Task> tasks =query.getAllInstancesOf(new Task.Project());
		return tasks;
	}

	@Override
	protected String getListClassName() {
		return "todaystasklist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return ""+tasks.size()+" for Today";
	}
}
