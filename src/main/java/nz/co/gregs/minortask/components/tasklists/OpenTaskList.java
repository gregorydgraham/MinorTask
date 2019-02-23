/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.minortask.components.SecureSpan;
import nz.co.gregs.minortask.datamodel.Task;

@StyleSheet("styles/open-task-list.css")
public class OpenTaskList extends AbstractTaskList {

	private Button newAddButton;

	public OpenTaskList(Long taskID) {
		super(taskID);
		setTooltipText("All the tasks that are still be done");
	}

	@Override
	protected void setupTaskList() {
		setupAddButton();
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
		SecureSpan span = new SecureSpan();
		span.add(newAddButton);
		span.setTooltipText("Add a new minor task to advance this project", Position.BOTTOM_LEFT);
		return new Component[]{span};
	}

	private void setupAddButton() {
		newAddButton = new Button();
		newAddButton.addClassName("opentasks-addminortaskbutton");
		newAddButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE_O));
		newAddButton.setText("Add MinorTask");
		newAddButton.addClassNames("addtaskbutton");
		newAddButton.addClickListener((event) -> {
			addNewMinorTask();
		});
	}

	private void addNewMinorTask() {
		newAddButton.setVisible(false);
		Div footer = getFooter();
		Span span = new Span();
		span.addClassName("opentasks-newminortask");
		
		final TextField textArea = new TextField();
		textArea.setPlaceholder("Task Name");
		textArea.setSizeFull();
		final Button saveButton = new Button("Save");
		saveButton.addClassName("save");
		saveButton.setSizeUndefined();
		saveButton.addClickListener((event) -> {
			footer.remove(span);
			createNewMinorTaskFromName(textArea.getValue());
		});
		final Button cancelButton = new Button("Cancel");
		cancelButton.addClassName("cancel");
		cancelButton.setSizeUndefined();
		cancelButton.addClickListener((event) -> {
			footer.remove(span);
			cancelCreateNewMinorTask();
		});

		span.add(textArea, saveButton, cancelButton);
		footer.add(span);
		textArea.focus();
	}

	private void createNewMinorTaskFromName(String name) {
		newAddButton.setVisible(true);
		if (name != null && !name.isEmpty()) {
			Task task = new Task();
			task.name.setValue(name);
			task.userID.setValue(getCurrentUserID());
			task.projectID.setValue(getTaskID());
			task.startDate.setValue(new Date());;
			task.finalDate.setValue(getTask().finalDate.getValue());
			try {
				getDatabase().insert(task);
			} catch (SQLException ex) {
				sqlerror(ex);
			}finally{
				this.refreshList();
			}
		}
	}

	public void disableNewButton() {
		newAddButton.setEnabled(false);
	}

	private void cancelCreateNewMinorTask() {
		newAddButton.setVisible(true);
	}
}
