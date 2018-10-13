/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.components.tasklists.AbstractTaskList;
import nz.co.gregs.minortask.datamodel.Task;

@Route("overdue")
public class OverdueTasksPage extends AuthorisedPage {

	@Override
	public Component getInternalComponent() {
		return new AbstractTaskList() {
			@Override
			protected String getListClassName() {
				return "overduetaskslist";
			}

			@Override
			protected String getListCaption(List<Task> tasks) {
				return tasks.size() + " Overdue Tasks";
			}

			@Override
			protected List<Task> getTasksToList() throws SQLException {
				Task.Project.WithSortColumns example = new Task.Project.WithSortColumns();
				example.userID.permittedValues(minortask().getUserID());
				example.finalDate.permittedRangeExclusive(null, new Date());
				example.completionDate.permittedValues((Date) null);
				final Task task = new Task();
				final DBQuery query = minortask().getDatabase().getDBQuery(example).addOptional(task);
				query.addCondition(task.column(task.taskID).isNull());
				query.setSortOrder(
						example.column(example.isOverdue),
						example.column(example.hasStarted),
						example.column(example.finalDate),
						example.column(example.startDate)
				);
				query.printAllRows();
				List<Task> tasks = query.getAllInstancesOf(example);
				return tasks;
			}
		};
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Overdue";
	}

}
