/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

public class ActiveTaskList extends MinorTaskComponent {
	
	public ActiveTaskList(MinorTaskUI ui, Long selectedTask) {
		super(ui, selectedTask);
		Panel panel = new Panel();
		panel.setContent(getComponent());
		this.setCompositionRoot(panel);
	}
	
	public final Component getComponent() {
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.addStyleName("well");
		try {
			
			List<Task.WithSortColumns> tasks = getTasksToList();
			
			final String caption = tasks.size() + " Active Tasks";
			layout.setCaption(caption);
			final Label label = new Label(caption);
			label.addStyleName("small");
			layout.addComponent(label);
			final Button button = new Button("New Task");
			button.addStyleName("friendly");
			button.setWidth(100, Unit.PERCENTAGE);
			button.addClickListener((event) -> {
				minortask().showTaskCreation(this.getTaskID());
			});
			layout.addComponent(button);
			for (Task task : tasks) {
				layout.addComponent(new TaskSummary(minortask(), getTaskID(), task));
			}
		} catch (SQLException ex) {
			Helper.sqlerror(ex);
		}
		return layout;
	}
	
	protected List<Task.WithSortColumns> getTasksToList() throws SQLException {
		Task.WithSortColumns example = new Task.WithSortColumns();
		example.userID.permittedValues(minortask().getUserID());
		example.projectID.permittedValues(getTaskID());
		example.completionDate.permittedValues((Date) null);
		final DBTable<Task.WithSortColumns> dbTable = Helper.getDatabase().getDBTable(example);
		dbTable.setSortOrder(
				example.column(example.isOverdue),
				example.column(example.hasStarted),
				example.column(example.finalDate),
				example.column(example.startDate)
		);
		List<Task.WithSortColumns> tasks = dbTable.getAllRows();
		return tasks;
	}	
}
