/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.minortask.datamodel.Task;


public class AllOpenTasksList extends AbstractTaskList {

	public AllOpenTasksList() {
		super();
	}

	@Override
	protected String getListClassName() {
		return "allopentaskslist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return ""+ tasks.size()+" Open Tasks";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task.WithSortColumns();
		example.completionDate.permitOnlyNull();
		example.userID.permittedValues(minortask().getUserID());
		List<Task> list = getDatabase().getByExample(example);
		return list;
	}
	
}
