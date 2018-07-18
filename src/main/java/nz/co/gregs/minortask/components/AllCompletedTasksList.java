/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.minortask.datamodel.Task;

@Tag("completed-task-list")
public class AllCompletedTasksList extends VerticalLayout implements RequiresLogin{

	public AllCompletedTasksList() {
		add(buildComponent());
		this.addClassNames("completed", "tasklist");
	}

	public final Component buildComponent() {

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.addClassName("well");
		try {

			List<Task.WithSortColumns> tasks = getTasksToList();
			
			final String caption = tasks.size() + " Completed Tasks";
			final Label label = new Label(caption);
			label.addClassName("small");
			layout.add(label);
			
			for (Task task : tasks) {
				final TaskSummary taskSummary = new TaskSummary(task);
				taskSummary.addClassName("completed");
				layout.add(taskSummary);
			}
		} catch (SQLException ex) {
			minortask().sqlerror(ex);
		}
		return layout;
	}

	protected List<Task.WithSortColumns> getTasksToList() throws SQLException {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		example.completionDate.excludedValues((Date) null);
		final DBTable<Task.WithSortColumns> dbTable = getDatabase().getDBTable(example);
		example.completionDate.setSortOrderDescending();
		dbTable.setSortOrder(
				example.column(example.completionDate),
				example.column(example.name),
				example.column(example.taskID)
		);
		List<Task.WithSortColumns> tasks = dbTable.getAllRows();
		return tasks;
	}

}
