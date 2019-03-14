/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import nz.co.gregs.minortask.components.task.CreateTaskInline;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.expressions.BooleanExpression;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.SecureSpan;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/open-projects-list.css")
public class OpenProjectsList extends AbstractTaskList {

	private Button addButton;
	private CreateTaskInline createTask;
	private SecureSpan footerContents;

	public OpenProjectsList() {
		super();
		setTooltipText("Start a project to include your tasks");
	}

	@Override
	protected void setupTaskList() {
		setupAddButton();
		createTask = new CreateTaskInline(getTask());
		createTask.addCreateTaskListener((event) -> {
			refreshList();
		});
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		Task example = new Task();
		example.completionDate.permitOnlyNull();
		final DBQuery query = Globals.getDatabase().getDBQuery(example);
		query.addCondition(
				BooleanExpression.allOf(
						example.column(example.userID).is(minortask().getCurrentUserID()),
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
	protected Component[] getFooterExtras() {
		footerContents = new SecureSpan();
		footerContents.add(addButton);
		footerContents.setTooltipText("Add a new minor task to advance this project", Position.BOTTOM_LEFT);
		return new Component[]{footerContents};
	}

	@Override
	protected String getListClassName() {
		return "openprojectslist";
	}

	private void setupAddButton() {
		addButton = new Button();
		addButton.addClassName("openprojects-addminortaskbutton");
		addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE_O));
		addButton.setText("Add MinorTask Project");
		addButton.addClassNames("addtaskbutton");
		addButton.addClickListener((event) -> {
			createTask.addNewMinorTask(getFooter(), footerContents);
		});
	}

	public void disableNewButton() {
		addButton.setEnabled(false);
	}
}
