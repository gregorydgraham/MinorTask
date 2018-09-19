/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.minortask.components.AddTaskButton;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class OpenProjectsList extends AbstractTaskList {

	private AddTaskButton newTaskButton = new AddTaskButton(null);

	public OpenProjectsList() {
		super(null);
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
		example.userID.permittedValues(minortask().getUserID());
		example.projectID.permittedValues(taskID);
		example.completionDate.permittedValues((Date) null);
		final DBTable<Task> dbTable = minortask().getDatabase().getDBTable(example);
		dbTable.setSortOrder(
				example.column(example.finalDate).isLessThan(DateExpression.currentDate()).descending(),
				example.column(example.startDate).isLessThan(DateExpression.currentDate()).descending(),
				example.column(example.finalDate).ascending(),
				example.column(example.startDate).ascending()
		);
		List<Task> tasks = dbTable.getAllRows();
		return tasks;
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Open Projects";
	}

	@Override
	protected String getListClassName() {
		return "openprojectslist";
	}

	@Override
	protected Component[] getFooterExtras() {
		return new Component[]{getNewTaskButton()};
	}

	/**
	 * @return the newTaskButton
	 */
	public final AddTaskButton getNewTaskButton() {
		if (newTaskButton == null) {
			newTaskButton = new AddTaskButton(null);
		}
		return newTaskButton;
	}

	public void disableNewButton() {
		this.getNewTaskButton().setEnabled(false);
	}

}
