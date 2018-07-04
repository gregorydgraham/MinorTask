/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.Task.Project;

public class ProjectPicker extends MinorTaskComponent {

	public ProjectPicker(MinorTaskUI ui, Long taskID) {
		super(ui, taskID);
		this.setCompositionRoot(getCurrentProjectComponent());
	}

	private Component getPickerComponent() {
		VerticalLayout projectList = new VerticalLayout();
		VerticalLayout subtaskList = new VerticalLayout();
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(projectList, subtaskList);

		addProjectsToLeftPanel(projectList);
		addPeerTasksToRightPanel(subtaskList);

		return splitPanel;
	}

	private Component getCurrentProjectComponent() {
		Button button = new Button("Projects >");
		try {
			Task task = new Task();
			Task.Project project = new Task.Project();
			task.taskID.permittedValues(getTaskID());
			task.userID.permittedValues(minortask().getUserID());
			List<DBQueryRow> allRows = Helper.getDatabase().getDBQuery(task).addOptional(project).getAllRows();
			if (allRows.size() == 1) {
				Task.Project projectFound = allRows.get(0).get(project);
				if (projectFound != null) {
					button = new Button(projectFound.name.stringValue());
				}
			}
		} catch (SQLException ex) {
			Helper.sqlerror(ex);
		}
		button.addClickListener((event) -> {
			this.setCompositionRoot(getPickerComponent());
		});
		button.setWidthUndefined();
		return button;
	}

	private void addProjectsToLeftPanel(VerticalLayout projectList) {
		try {
			final Long taskID = getTaskID();
			final long userID = minortask().getUserID();
			Task task = Helper.getTaskExample(taskID, userID);
			final Project project = new Project();
			List<DBQueryRow> rows = Helper.getDatabase().getDBQuery(task).addOptional(project).getAllRows();
			if (rows.size() == 1) {
				DBQueryRow row = rows.get(0);
				Project currentProject = row.get(project);
				if (currentProject == null) {
					addProjectSelectionButton(projectList, taskID, userID, null);
				} else {
					Task superProjectExample = Helper.getProjectExample(currentProject.projectID.getValue(), userID);
					List<Task> allProjects = Helper.getDatabase().getDBTable(superProjectExample).getAllRows();
					for (Task selectableTask : allProjects) {
						addProjectSelectionButton(projectList, taskID, userID, selectableTask);
					}
				}
			}
		} catch (SQLException ex) {
			Helper.sqlerror(ex);
		}
	}

	protected void addProjectSelectionButton(AbstractLayout projectList, final Long taskID, final long userID, Task allProject) {
		final Button button = new Button(allProject == null ? "Projects" : allProject.name.toString());
		button.addClickListener(
				new ProjectChosenListener(
						this,
						taskID,
						userID,
						allProject == null ? null : allProject.taskID.getValue()
				)
		);
		projectList.addComponent(button);
	}

	private void addPeerTasksToRightPanel(VerticalLayout subtaskList) {
		try {
			final Long taskID = getTaskID();
			final long userID = minortask().getUserID();
			Task task = Helper.getTask(taskID, userID);
			Task projectExample = Helper.getProjectExample(task.projectID.getValue(), userID);
			List<Task> subtasks = Helper.getDatabase().getDBTable(projectExample).getAllRows();
			for (Task subtask : subtasks) {
				if (!subtask.taskID.getValue().equals(taskID)) {
					addProjectSelectionButton(subtaskList, taskID, userID, subtask);
				}
			}
		} catch (SQLException ex) {
			Helper.sqlerror(ex);
		}
	}

	private static class ProjectChosenListener implements Button.ClickListener {

		private final Long taskID;
		private final Long userID;
		private final Long projectID;
		private final ProjectPicker picker;

		public ProjectChosenListener(ProjectPicker picker, Long taskID, Long userID, Long projectID) {
			this.picker = picker;
			this.taskID = taskID;
			this.userID = userID;
			this.projectID = projectID;
		}

		@Override
		public void buttonClick(Button.ClickEvent event) {
			try {
				Task task = Helper.getTask(taskID, userID);
				task.projectID.setValue(projectID);
				Helper.getDatabase().update(task);
				picker.setCompositionRoot(picker.getCurrentProjectComponent());
			} catch (SQLException ex) {
				Helper.sqlerror(ex);
			}
		}
	}

}
