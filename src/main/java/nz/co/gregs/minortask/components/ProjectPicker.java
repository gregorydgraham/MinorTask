/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

public class ProjectPicker extends HorizontalLayout implements RequiresLogin{

	private final Long taskID;

	public ProjectPicker(Long taskID) {
		this.taskID = taskID;
		this.add(getCurrentProjectComponent());
	}

	private Component getPickerComponent() {
		try {
			Task example = new Task();
			example.userID.permittedValues(minortask().getUserID());
			example.completionDate.permittedValues((Date) null);
			example.name.setSortOrderAscending();

			List<Task> listOfTasks = getDatabase().getDBTable(example).getAllRows();

			ComboBox<Task> taskList = new ComboBox<Task>("Project", listOfTasks);
			taskList.setDataProvider(new TasksDataProvider(listOfTasks));

			taskList.addValueChangeListener(new ProjectChosenListener(minortask(), this, taskID, minortask().getUserID()));
			taskList.addBlurListener((event) -> {
				this.removeAll();
				this.add(getCurrentProjectComponent());
			});
			taskList.focus();
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
			task.taskID.permittedValues(taskID);
			task.userID.permittedValues(minortask().getUserID());
			task.completionDate.permittedValues((Date) null);
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
			minortask().sqlerror(ex);
		}
		button.addClickListener((event) -> {
			this.removeAll();
			this.add(getPickerComponent());
		});
//		button.setCaption("Project");
		button.setSizeUndefined();
		return button;
	}

	private static class ProjectChosenListener implements HasValue.ValueChangeListener<HasValue.ValueChangeEvent<Task>> {

		private final Long taskID;
		private final Long userID;
		private final ProjectPicker picker;
		private final MinorTask minortask;

		public ProjectChosenListener(MinorTask minortask, ProjectPicker picker, Long taskID, Long userID) {
			this.minortask = minortask;
			this.picker = picker;
			this.taskID = taskID;
			this.userID = userID;
		}

		

		@Override
		public void valueChanged(HasValue.ValueChangeEvent<Task> event) {
			//public void valueChange(HasValue.ValueChangeEvent<Task> event) {
			try {
				Task task = minortask.getTask(taskID, userID);
				final Task selectedProject = event.getValue();
				if (selectedProject != null) {
					// prevent self-projects
					final Long newProjectID = selectedProject.taskID.getValue();
					if (newProjectID != null && taskID.equals(newProjectID)) {
						minortask.chat("No self-contained projects please");
					} else {
						final Long taskProjectID = task.projectID.getValue();
						final Date taskStartDate = task.startDate.getValue();
						final Date taskDeadlineDate = task.finalDate.getValue();
						// ensure the tree integrity by promoting any separated tasks
						List<Task> projectPathTasks = minortask.getProjectPathTasks(newProjectID, minortask.getUserID());
						for (Task projectPathTask : projectPathTasks) {
							final DBInteger projectID = projectPathTask.projectID;
							if (taskID.equals(projectID.longValue())) {
								projectPathTask.projectID.setValue(taskProjectID);
							}
							// force the starting date upwards
							if (projectPathTask.startDate.dateValue().after(taskStartDate)) {
								projectPathTask.startDate.setValue(taskStartDate);
							}
							// force the deadline downwards
							final Date projectPathTaskDeadlineDate = projectPathTask.finalDate.dateValue();
							if (projectPathTaskDeadlineDate.before(taskDeadlineDate)) {
								task.finalDate.setValue(projectPathTaskDeadlineDate);
							}
							// save the project task if necessary
							if (projectPathTask.hasChangedSimpleTypes()) {
								minortask.getDatabase().update(projectPathTask);
							}
						}
						task.projectID.setValue(newProjectID);
						minortask.getDatabase().update(task);
						// enforce date constraints on tree
						minortask.enforceDateConstraintsOnTaskTree(task);
						picker.add(picker.getCurrentProjectComponent());
						minortask.showCurrentTask();
					}
				}
			} catch (SQLException|MinorTask.InaccessibleTaskException ex) {
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
			return item.name.getValue();
		}

	}

}
