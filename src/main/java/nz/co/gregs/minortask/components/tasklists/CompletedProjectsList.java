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
import nz.co.gregs.minortask.datamodel.Task;

@StyleSheet("styles/completed-projects-list.css")
public class CompletedProjectsList extends AbstractTaskListOfDBQueryRow {

	public CompletedProjectsList() {
		super();
		setTooltipText("When you've finished a project it will be shown here");
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		Task example = new Task();
		example.projectID.permittedValues(getTaskID());
		example.completionDate.permitOnlyNotNull();
		example.completionDate.setSortOrderDescending();
		final DBQuery query = getDatabase().getDBQuery(example).addOptional(new Task.Project());
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(minortask().getCurrentUserID())
						.or(
								example.column(example.assigneeID).is(minortask().getCurrentUserID())
						)
		);
		query.setSortOrder(
				example.column(example.completionDate).descending(),
				example.column(example.name).ascending(),
				example.column(example.taskID).ascending()
		);
		return query.getAllRows();
	}

	@Override
	protected String getListClassName() {
		return "completedprojectslist";
	}

	@Override
	protected Component getListCaption(List<DBQueryRow> tasks) {
		return new Label("" + tasks.size() + " Completed Projects");
	}

	@Override
	public boolean checkForPermission(DBQueryRow item) {
		return minortask().checkUserCanViewTask(item.get(new Task()));
	}

}
