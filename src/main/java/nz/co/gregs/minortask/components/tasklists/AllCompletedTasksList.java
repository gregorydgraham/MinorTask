/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.Task;

@StyleSheet("styles/allcompleted-task-list.css")
public class AllCompletedTasksList extends AbstractTaskListOfDBQueryRow {

	public AllCompletedTasksList() {
		super();
		setToolTip();
	}

	public AllCompletedTasksList(Task project) {
		super(project);
		setToolTip();
	}

	private void setToolTip() {
		setTooltipText("All the tasks that have been done within this project");
	}

	@Override
	protected String getListClassName() {
		return "allcompletedtaskslist";
	}

	@Override
	protected Component getListCaption(List<DBQueryRow> tasks) {
		return new Label("" + tasks.size() + " Completed Tasks");
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		Task example = new Task();
		DBQuery query = Globals.getDatabase().getDBQuery(example).addOptional(new Task.Project());
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(minortask().getCurrentUserID())
						.or(
								example.column(example.assigneeID).is(minortask().getCurrentUserID())
						)
		);
		if (getProjectID() == null) {
			example.completionDate.permitOnlyNotNull();
			List<DBQueryRow> allRows = query.getAllRows();
			return allRows;
		} else {
			return minortask().getDBQueryRowOfProjectFiltered(
					getTaskID(),
					(t) -> t.completionDate.getValue() != null && !t.taskID.getValue().equals(getTaskID())
			);
		}
	}

	@Override
	public boolean checkForPermission(DBQueryRow item) {
		final Task task = item.get(new Task());
		return task.completionDate.isNotNull() && minortask().checkUserCanViewTask(task);
	}
}
