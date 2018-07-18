/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.ListDataProvider;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

public class ProjectSelector extends ComboBox<Task> implements RequiresLogin {

	private final Long taskID;
	private Task.TaskAndProject taskAndProject;

	public ProjectSelector(Long taskID) {
		this.taskID = taskID;
		try {
			taskAndProject = getTaskAndProject(taskID);

			Task example = new Task();
			example.userID.permittedValues(minortask().getUserID());
			example.completionDate.permittedValues((Date) null);
			example.name.setSortOrderAscending();
			List<Task> listOfTasks = getDatabase().getDBTable(example).getAllRows();
			setDataProvider(new TasksDataProvider(listOfTasks));
		} catch (SQLException | MinorTask.InaccessibleTaskException ex) {
			Logger.getLogger(ProjectSelector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

//	private Component getPickerComponent() {
//		try {
//			Task example = new Task();
//			example.userID.permittedValues(minortask().getUserID());
//			example.completionDate.permittedValues((Date) null);
//			example.name.setSortOrderAscending();
//
//			List<Task> listOfTasks = getDatabase().getDBTable(example).getAllRows();
////
//			ComboBox<Task> taskList = new ComboBox<Task>("Project", listOfTasks);
//			taskList.setDataProvider(new TasksDataProvider(listOfTasks));
//
//			taskList.setValue(taskAndProject.getProject());
//
//			taskList.addValueChangeListener(new ProjectChosenListener(minortask(), this, taskID));
//			taskList.addBlurListener((event) -> {
//				this.removeAll();
//				this.add(getCurrentProjectComponent());
//			});
//
//			return taskList;
//		} catch (SQLException ex) {
//			Logger.getLogger(ProjectSelector.class.getName()).log(Level.SEVERE, null, ex);
//			minortask().sqlerror(ex);
//			return new ComboBox("Projects");
//		}
//	}
//	private Component getCurrentProjectComponent() {
//		try {
//			TextField button = new TextField("Project");
//			taskAndProject = getTaskAndProject(taskID);
//			Task.Project projectFound = taskAndProject.getProject();
//			if (projectFound == null) {
//				button.setValue("Projects");
//			} else {
//				final String projectName = projectFound.name.stringValue();
//				if (!projectName.isEmpty()) {
//					button.setValue(projectName);
//				}
//			}
//
//			button.addFocusListener(
//					(event) -> {
//						removeAll();
//						add(getPickerComponent());
//					}
//			);
//			button.setSizeUndefined();
//			return button;
//		} catch (MinorTask.InaccessibleTaskException ex) {
//			Logger.getLogger(ProjectSelector.class.getName()).log(Level.SEVERE, null, ex);
//			return new AccessDeniedComponent();
//		}
//	}
	private static class ProjectChosenListener implements HasValue.ValueChangeListener<HasValue.ValueChangeEvent<Task>> {

		private final Long taskID;
		private final ProjectSelector picker;
		private final MinorTask minortask;

		public ProjectChosenListener(MinorTask minortask, ProjectSelector picker, Long taskID) {
			this.minortask = minortask;
			this.picker = picker;
			this.taskID = taskID;
		}

		@Override
		public void valueChanged(HasValue.ValueChangeEvent<Task> event) {
		}
	}

	private static class TasksDataProvider extends ListDataProvider<Task> {

		public TasksDataProvider(Collection<Task> items) {
			super(items);
		}

		@Override
		public Object getId(Task item) {
			return item.name.getValue();
		}

	}

}
