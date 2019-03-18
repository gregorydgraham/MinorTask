/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.minortask.components.generic.SecureSpan;
import nz.co.gregs.minortask.components.task.CreateTaskInline;
import nz.co.gregs.minortask.datamodel.Task;

@StyleSheet("styles/open-task-list.css")
public class OpenTaskList extends AbstractTaskList {

	private Button addButton;
	private CreateTaskInline createTaskSpan;
	private SecureSpan footerContents;

	public OpenTaskList(Long taskID) {
		super(taskID);
		this.addClassName("opentaskslist");
		setTooltipText("All the tasks that are still be done");
	}

	@Override
	protected void setupTaskList() {
		setupAddButton();
		createTaskSpan = new CreateTaskInline(getTask());
		createTaskSpan.addCreateTaskListener((event) -> {
			refreshList();
		});
	}

	@Override
	protected String getListClassName() {
		return "opentasks";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "" + tasks.size() + " Open Subtasks";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task.WithSortColumns();
		example.projectID.permittedValues(getTaskID());
		example.completionDate.permittedValues((Date) null);
		final DBQuery query = getDatabase().getDBQuery(example);
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(getCurrentUserID())
						.or(
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
		List<Task> tasks = query.getAllInstancesOf(example);
		return tasks;
	}

	@Override
	protected Component[] getFooterExtras() {
		footerContents = new SecureSpan();
		footerContents.add(addButton);
		footerContents.setTooltipText("Add a new minor task to advance this project", Position.BOTTOM_RIGHT);
		return new Component[]{footerContents};
	}

	private void setupAddButton() {
		addButton = new Button();
		addButton.addClassName("opentasks-addminortaskbutton");
		addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE_O));
		addButton.setText("Add MinorTask");
		addButton.addClassNames("addtaskbutton");
		addButton.addClickListener((event) -> {
			createTaskSpan.addNewMinorTask(getFooter(), footerContents);
		});
	}

	public void disableNewButton() {
		addButton.setEnabled(false);
	}
}
