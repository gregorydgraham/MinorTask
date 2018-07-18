/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Tag;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.minortask.datamodel.Task;

@Tag("completed-task-list")
public class AllCompletedTasksList extends AbstractTaskList{

//	public AllCompletedTasksList() {
//		add(buildComponent());
//		this.addClassNames("completed", "tasklist");
//	}

//	public final Component buildComponent() {
//
//		VerticalLayout layout = new VerticalLayout();
//		layout.setSpacing(false);
//		layout.addClassName("well");
//		try {
//
//			List<Task.WithSortColumns> tasks = getTasksToList();
//			
//			final String caption = tasks.size() + " Completed Tasks";
//			final Label label = new Label(caption);
//			label.addClassName("small");
//			layout.add(label);
//			
//			for (Task task : tasks) {
//				final TaskSummary taskSummary = new TaskSummary(task);
//				taskSummary.addClassName("completed");
//				layout.add(taskSummary);
//			}
//		} catch (SQLException ex) {
//			minortask().sqlerror(ex);
//		}
//		return layout;
//	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
		example.userID.permittedValues(minortask().getUserID());
		example.completionDate.excludedValues((Date) null);
		final DBTable<Task> dbTable = getDatabase().getDBTable(example);
		example.completionDate.setSortOrderDescending();
		dbTable.setSortOrder(
				example.column(example.completionDate),
				example.column(example.name),
				example.column(example.taskID)
		);
		List<Task> tasks =dbTable.getAllRows();
		return tasks;
	}

	@Override
	protected String getListClassName() {
		return "allcompletedtaskslist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return ""+tasks.size()+" Completed Tasks";
	}

}
