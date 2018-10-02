/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.tasklists.AbstractTaskList;
import nz.co.gregs.minortask.datamodel.Task;

@Route("ideas")
public class IdeasPage extends MinorTaskPage {

	@Override
	public Component getInternalComponent(Long parameter) {
		return new AbstractTaskList(parameter) {
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
				Task example = new Task();
				example.userID.permittedValues(minortask().getUserID());
				example.startDate.permitOnlyNull();
				example.preferredDate.permitOnlyNull();
				example.preferredDate.permitOnlyNull();
				example.completionDate.permitOnlyNull();
				final DBQuery query = Globals.getDatabase().getDBQuery(example);
				query.setSortOrder(
						example.column(example.name)
				);
				query.printAllRows();
				List<Task> tasks = query.getAllInstancesOf(example);
				return tasks;
			}
		};
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Ideas";
	}

}
