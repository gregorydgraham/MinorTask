/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

import nz.co.gregs.minortask.MinorTaskEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.components.RequiresLogin;
import static nz.co.gregs.minortask.Globals.getTaskExample;
import nz.co.gregs.minortask.components.task.SecureTaskDiv;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/project-path-navigator.css")
public class ProjectNavigator extends SecureTaskDiv implements MinorTaskComponent, RequiresLogin, MinorTaskEventListener, MinorTaskEventNotifier {

//	private Long taskID = 0l;
//	private EditorLayout page = null;
//	private final List<TaskEditorButton> taskEditorButtons = new ArrayList<>();

	public ProjectNavigator() {
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
		final ProjectNavigatorButton button = new ProjectNavigatorButton(getTaskID(), task, this);
		button.addTaskMoveEventListener(this);
		return button;
	}

	@Override
	public void setTask(Task newTask) {
		super.setTask(newTask);
		buildComponent();
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		fireEvent(event);
	}
}
