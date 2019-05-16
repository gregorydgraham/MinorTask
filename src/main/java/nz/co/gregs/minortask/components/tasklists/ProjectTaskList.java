/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.html.Label;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
//@Tag("project-task-list")
public class ProjectTaskList extends AbstractTaskListOfDBQueryRow {

	public ProjectTaskList() {
		super();
		setTooltipText("I didn't think was used???");
	}

	@Override
	protected String getListClassName() {
		return "projecttasklist";
	}

	@Override
	protected Label getListCaption(List<DBQueryRow> tasks) {
		return new Label("" + tasks.size() + " Project Tasks");
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		System.out.println("nz.co.gregs.minortask.components.tasklists.ProjectTaskList.getTasksToList()");
		final Task example = new Task();
		example.projectID.permitOnlyNull();
		DBQuery query = getDatabase().getDBQuery(example).addOptional(new Task.Project());
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(getCurrentUserID())
						.or(
								example.column(example.assigneeID).is(getCurrentUserID())
						)
		);
		return query.getAllRows();
	}

}
