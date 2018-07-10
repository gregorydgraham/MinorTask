/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.minortask.datamodel.Task;

public class ActiveTaskList extends VerticalLayout implements HasMinorTask{

	private final AddTaskButton newTaskButton;
	private final Long selectedTask;

	public ActiveTaskList(Long selectedTask) {
		this.selectedTask = selectedTask;
		newTaskButton = new AddTaskButton(selectedTask);
		add(getComponent());
	}

	public final Component getComponent() {

		VerticalLayout layout = new VerticalLayout();
		layout.addClassName("activetasklist");
		layout.setSpacing(false);
		layout.addClassName("well");
		try {

			List<Task.WithSortColumns> tasks = getTasksToList();

			final String caption = tasks.size() + " Active Tasks";
//			layout.setCaption(caption);
			final Label label = new Label(caption);
			label.setSizeUndefined();

			Label spacer = new Label();
			spacer.setWidth("100%");

			HorizontalLayout header = new HorizontalLayout();
			header.add(label, spacer);
			header.setWidth("100%");

			layout.add(header);
			for (Task task : tasks) {
				layout.add(new TaskSummary(selectedTask, task));
			}

			HorizontalLayout footer = new HorizontalLayout();
			footer.setWidth("100%");
			footer.add(newTaskButton);
			footer.addClassNames("activelist", "footer");
			layout.add(footer);

		} catch (SQLException ex) {
			minortask().sqlerror(ex);
		}
		return layout;
	}

	protected List<Task.WithSortColumns> getTasksToList() throws SQLException {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		example.projectID.permittedValues(selectedTask);
		example.completionDate.permittedValues((Date) null);
		final DBTable<Task.WithSortColumns> dbTable = minortask().getDatabase().getDBTable(example);
		dbTable.setSortOrder(
				example.column(example.isOverdue),
				example.column(example.hasStarted),
				example.column(example.finalDate),
				example.column(example.startDate)
		);
		List<Task.WithSortColumns> tasks = dbTable.getAllRows();
		return tasks;
	}

	void disableNewButton() {
		this.newTaskButton.setEnabled(false);
	}
}
