/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public abstract class TaskListComponent extends MinorTaskComponent {

	public TaskListComponent(MinorTaskUI ui, Long selectedTask) {
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
			
			final String caption = tasks.size() + " " + getTaskDescriptor() + " Tasks";
			layout.setCaption(caption);
			final Label label = new Label(caption);
			label.addStyleName("small");
			layout.addComponent(label);
			
			for (Task task : tasks) {
				layout.addComponent(new TaskSummary(minortask(), getTaskID(), task));
			}
		} catch (SQLException ex) {
			Helper.sqlerror(ex);
		}
		return layout;
	}

	protected abstract List<Task.WithSortColumns> getTasksToList() throws SQLException;

	protected abstract String getTaskDescriptor();

}
