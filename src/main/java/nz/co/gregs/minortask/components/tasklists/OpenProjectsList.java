/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.expressions.BooleanExpression;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.minortask.components.AddTaskButton;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class OpenProjectsList extends AbstractTaskList {

	private AddTaskButton newTaskButton = null;

	public OpenProjectsList() {
		super();
		setTooltipText("Start a project to include your tasks");
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
		example.completionDate.permitOnlyNull();
		final DBQuery query = getDatabase().getDBQuery(example);
		query.addCondition(
				BooleanExpression.allOf(
						example.column(example.userID).is(getCurrentUserID()),
						example.column(example.projectID).isNull()
				).or(
						example.column(example.assigneeID).is(getCurrentUserID())
				)
		);
		query.setSortOrder(
				example.column(example.finalDate).isLessThan(DateExpression.currentDate()).descending(),
				example.column(example.startDate).isLessThan(DateExpression.currentDate()).descending(),
				example.column(example.finalDate).ascending(),
				example.column(example.startDate).ascending(),
				example.column(example.name).ascending()
		);
		System.out.println("OPEN PROJECTS:");
		System.out.println(query.getSQLForQuery());
		List<Task> tasks = query.getAllInstancesOf(example);
		return tasks;
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Open Projects";
	}

	@Override
	protected Component[] getHeaderExtras() {
		return new Component[]{getNewTaskButton()};
	}

	@Override
	protected String getListClassName() {
		return "openprojectslist";
	}

	/**
	 * @return the newTaskButton
	 */
	public final AddTaskButton getNewTaskButton() {
		if (newTaskButton == null) {
			newTaskButton = new AddTaskButton("Add Project...");
			newTaskButton.addClassNames("small", "openprojectslist-addproject");
		}
		return newTaskButton;
	}

	public void disableNewButton() {
		this.getNewTaskButton().setEnabled(false);
	}

}
