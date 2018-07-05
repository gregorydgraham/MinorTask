/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

public class ProjectPicker extends MinorTaskComponent {

	public ProjectPicker(MinorTask minortask, Long taskID) {
		super(minortask, taskID);
		this.setCompositionRoot(getCurrentProjectComponent());
	}

	private Component getPickerComponent() {
		try {
			Task example = new Task();
			example.userID.permittedValues(minortask().getUserID());
			example.name.setSortOrderAscending();
			
			List<Task> listOfTasks = getDatabase().getDBTable(example).getAllRows();
			
			ComboBox<Task> taskList = new ComboBox<Task>("Project", listOfTasks);
			taskList.setDataProvider(new TasksDataProvider(listOfTasks));
//			taskList.setItems(listOfTasks);
			taskList.setSelectedItem(getTask());
			return taskList;
		} catch (SQLException ex) {
			Logger.getLogger(ProjectPicker.class.getName()).log(Level.SEVERE, null, ex);
			minortask().sqlerror(ex);
		}
		return new Button("Oops");
	}

	private Component getCurrentProjectComponent() {
		Button button = new Button("Projects");
		try {
			Task task = new Task();
			Task.Project project = new Task.Project();
			task.taskID.permittedValues(getTaskID());
			task.userID.permittedValues(minortask().getUserID());
			List<DBQueryRow> allRows = getDatabase().getDBQuery(task).addOptional(project).getAllRows();
			if (allRows.size() == 1) {
				Task.Project projectFound = allRows.get(0).get(project);
				if (projectFound != null) {
					final String projectName = projectFound.name.stringValue();
					if (!projectName.isEmpty()) {
						button = new Button(projectName);
					}
				}
			}
		} catch (SQLException ex) {
			minortask.sqlerror(ex);
		}
		button.addClickListener((event) -> {
			this.setCompositionRoot(getPickerComponent());
		});
		button.setWidthUndefined();
		return button;
	}

//	private void addProjectsToLeftPanel(VerticalLayout projectList) {
//		try {
//			final Long taskID = getTaskID();
//			final long userID = minortask().getUserID();
//			Task task = MinorTask.getTaskExample(taskID, userID);
//			final Project project = new Project();
//			List<DBQueryRow> rows = MinorTask.getDatabase().getDBQuery(task).addOptional(project).getAllRows();
//			if (rows.size() == 1) {
//				DBQueryRow row = rows.get(0);
//				Project currentProject = row.get(project);
//				if (currentProject == null) {
//					addProjectSelectionButton(projectList, taskID, userID, null);
//				} else {
//					Task superProjectExample = MinorTask.getProjectExample(currentProject.projectID.getValue(), userID);
//					List<Task> allProjects = MinorTask.getDatabase().getDBTable(superProjectExample).getAllRows();
//					for (Task selectableTask : allProjects) {
//						addProjectSelectionButton(projectList, taskID, userID, selectableTask);
//					}
//				}
//			}
//		} catch (SQLException ex) {
//			MinorTask.sqlerror(ex);
//		}
//	}

//	protected void addProjectSelectionButton(AbstractLayout projectList, final Long taskID, final long userID, Task allProject) {
//		final Button button = new Button(allProject == null ? "Projects" : allProject.name.toString());
//		button.addClickListener(
//				new ProjectChosenListener(
//						this,
//						taskID,
//						userID,
//						allProject == null ? null : allProject.taskID.getValue()
//				)
//		);
//		projectList.addComponent(button);
//	}

//	private void addPeerTasksToRightPanel(VerticalLayout subtaskList) {
//		try {
//			final Long taskID = getTaskID();
//			final long userID = minortask().getUserID();
//			Task task = MinorTask.getTask(taskID, userID);
//			Task projectExample = MinorTask.getProjectExample(task.projectID.getValue(), userID);
//			List<Task> subtasks = MinorTask.getDatabase().getDBTable(projectExample).getAllRows();
//			for (Task subtask : subtasks) {
//				if (!subtask.taskID.getValue().equals(taskID)) {
//					addProjectSelectionButton(subtaskList, taskID, userID, subtask);
//				}
//			}
//		} catch (SQLException ex) {
//			MinorTask.sqlerror(ex);
//		}
//	}

	private static class ProjectChosenListener implements Button.ClickListener {

		private final Long taskID;
		private final Long userID;
		private final Long projectID;
		private final ProjectPicker picker;
		private final MinorTask minortask;

		public ProjectChosenListener(MinorTask minortask, ProjectPicker picker, Long taskID, Long userID, Long projectID) {
			this.minortask = minortask;
			this.picker = picker;
			this.taskID = taskID;
			this.userID = userID;
			this.projectID = projectID;
		}

		@Override
		public void buttonClick(Button.ClickEvent event) {
			try {
				Task task = minortask.getTask(taskID, userID);
				task.projectID.setValue(projectID);
				minortask.getDatabase().update(task);
				picker.setCompositionRoot(picker.getCurrentProjectComponent());
			} catch (SQLException ex) {
				minortask.sqlerror(ex);
			}
		}
	}

	private static class TasksDataProvider extends ListDataProvider<Task> {

		public TasksDataProvider(Collection<Task> items) {
			super(items);
		}

		@Override
		public Object getId(Task item) {
			return item.taskID.getValue();
		}
		
		
	}

}
