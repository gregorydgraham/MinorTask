/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.datamodel.Task;

@StyleSheet("styles/completed-task-list.css")
public class CompletedTasksList extends AbstractTaskListOfDBQueryRow {

	public CompletedTasksList(Task task) {
		super(task);
		setTooltipText("When you complete a task it will be moved to here, so you can see your progress");
	}

	public CompletedTasksList() {
		super();
		setTooltipText("When you complete a task it will be moved to here, so you can see your progress");
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		Task example = new Task();
//		example.userID.permittedValues(minortask().getCurrentUserID());
		example.projectID.permittedValues(getTaskID());
		example.completionDate.permitOnlyNotNull();
		example.completionDate.setSortOrderDescending();
		final DBQuery query = getDatabase().getDBQuery(example).addOptional(new Task.Project());
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(getCurrentUserID())
						.or(
								example.column(example.assigneeID).is(getCurrentUserID())
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
		return "completedtasklist";
	}

	@Override
	protected Component getListCaption(List<DBQueryRow> tasks) {
		return new Label("" + tasks.size() + " Completed Tasks");
	}

	@Override
	protected Component getSubTaskNumberComponent(Task task, Long number) {
		return new Span();
	}
}
