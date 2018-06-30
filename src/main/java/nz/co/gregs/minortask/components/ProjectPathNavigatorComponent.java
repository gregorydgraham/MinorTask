/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.TaskWithSortColumns;

/**
 *
 * @author gregorygraham
 */
public class ProjectPathNavigatorComponent extends CustomComponent {
	
	private final MinorTaskUI ui;
	private final Long currentTaskID;
	
	public ProjectPathNavigatorComponent(MinorTaskUI ui, Long taskID) {
		this.ui = ui;
		this.currentTaskID = taskID;
		setCompositionRoot(getComponent());
	}
	
	public final Component getComponent() {
		try {
			HorizontalLayout hLayout = new HorizontalLayout();
			hLayout.addComponentAsFirst(getButtonForTaskID(null));
			final Task task = new Task();
			task.taskID.permittedValues(this.currentTaskID);
			DBQuery query = Helper.getDatabase().getDBQuery(task);
			DBRecursiveQuery<Task> recurse = new DBRecursiveQuery<Task>(query, task.column(task.projectID));
			List<Task> ancestors = recurse.getAncestors();
			for (Task ancestor : ancestors) {
				final Button label = getButtonForTaskID(ancestor);
				hLayout.addComponent(label, 1);
			}
			return hLayout;
		} catch (SQLException ex) {
			Logger.getLogger(ProjectPathNavigatorComponent.class.getName()).log(Level.SEVERE, null, ex);
			Helper.sqlerror(ex);
		}
		return new Label("Current Project: " + currentTaskID);
	}
	
	public Button getButtonForTaskID(Task task) {
		final Button button = new Button((task == null ? "All" : task.name.getValue()) + " > ", (event) -> {
			final Long taskID = task == null ? null : task.taskID.getValue();
//			TaskWithSortColumns example = new TaskWithSortColumns();
//			example.userID.permittedValues(ui.getUserID());
//			example.projectID.permittedValues(taskID);
			ui.showTask(taskID);
//			new TaskListComponent(ui, taskID, example).show();
		});
		button.addStyleNames("tiny", "friendly");
		return button;
	}
	
}
