/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import static nz.co.gregs.minortask.Globals.getTaskExample;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.AuthorisedOptionalTaskPage;
import nz.co.gregs.minortask.pages.TaskEditorLayout;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/project-path-navigator.css")
public class ProjectPathNavigator extends Div implements MinorTaskComponent, RequiresLogin {

	private final Long taskID;
	private final Class<? extends AuthorisedOptionalTaskPage> targetPage;

	public ProjectPathNavigator() {
		this(null);
	}

	public ProjectPathNavigator(Long taskID) {
		this(TaskEditorLayout.class, taskID);
	}

	public ProjectPathNavigator(Class<? extends AuthorisedOptionalTaskPage> targetPage, Long taskID) {
		this.targetPage = targetPage;
		this.taskID = taskID;
		buildComponent();
		addClassName("project-path-navigator");
	}

	protected final void buildComponent() {
		removeAll();
//		add(new QuickLinks());
		List<Task> ancestors = getProjectPathTasks(getTaskID(), getCurrentUserID());
		Collections.reverse(ancestors);
		ancestors.stream()
				.forEachOrdered((ancestor) -> {
					add(getButtonForTaskID(ancestor));
				});
	}

	public void refresh() {
		buildComponent();
	}

	public List<Task> getProjectPathTasks(Long taskID, final long userID) {
		try {
			final Task task = getTaskExample(taskID, userID);
			DBQuery query = getDatabase().getDBQuery(task);
			DBRecursiveQuery<Task> recurse = new DBRecursiveQuery<Task>(query, task.column(task.projectID));
			List<Task> ancestors = recurse.getAncestors();
			ancestors.add(null);
			return ancestors;
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		return new ArrayList<>();
	}

	public Button getButtonForTaskID(Task task) {
		final GoToTaskButton button = new GoToTaskButton(taskID, task, targetPage);
		return button;
	}

	/**
	 * @return the taskID
	 */
	public Long getTaskID() {
		return taskID;
	}
}
