/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.expressions.BooleanExpression;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

public class ProjectPicker extends HorizontalLayout implements RequiresLogin {

	private final Long taskID;
	private Task.TaskAndProject taskAndProject;

	public ProjectPicker(Long taskID) {
		this.taskID = taskID;
		try {
			taskAndProject = getTaskAndProject(taskID);
			this.add(getCurrentProjectComponent());
		} catch (MinorTask.InaccessibleTaskException ex) {
			this.add(new AccessDeniedComponent());
		}
	}

	private ComboBox<Task> getPickerComponent() {
		try {
			Task example = new Task();
			example.userID.permittedValues(minortask().getUserID());
			example.taskID.excludedValues(taskAndProject.getTask().taskID.getValue());
			example.name.setSortOrderAscending();
			final DBQuery query = getDatabase().getDBQuery(example);
			query.addCondition(
					BooleanExpression.anyOf(
							example.column(example.completionDate).isNull(),
							example.column(example.taskID).is(taskAndProject.getProject().taskID.getValue())
					)
			);
			query.setSortOrder(example.column(example.name).ascending());

			List<Task> listOfTasks = query.getAllInstancesOf(example);

			Task emptyTask = new Task();

			ComboBox<Task> taskList = new ProjectComboBox("Project", listOfTasks, emptyTask);
			listOfTasks.add(0, taskList.getEmptyValue());
			taskList.setDataProvider(new TasksDataProvider(listOfTasks));

			final Task.Project project = taskAndProject.getProject();
			taskList.setValue(project == null ? taskList.getEmptyValue() : project);

			taskList.addValueChangeListener(new ProjectChosenListener(minortask(), this, taskID));

			return taskList;
		} catch (SQLException ex) {
			Logger.getLogger(ProjectPicker.class.getName()).log(Level.SEVERE, null, ex);
			minortask().sqlerror(ex);
			return new ComboBox<Task>("Projects");
		}
	}

	private Component getCurrentProjectComponent() {
		return getPickerComponent();
	}

	private static class ProjectChosenListener implements HasValue.ValueChangeListener<HasValue.ValueChangeEvent<Task>> {

		private final Long taskID;
		private final ProjectPicker picker;
		private final MinorTask minortask;

		public ProjectChosenListener(MinorTask minortask, ProjectPicker picker, Long taskID) {
			this.minortask = minortask;
			this.picker = picker;
			this.taskID = taskID;
		}

		@Override
		public void valueChanged(HasValue.ValueChangeEvent<Task> event) {
			try {
				Task task = minortask.getTask(taskID);
				final Task selectedProject = event.getValue();
				if (selectedProject != null) {
					// prevent self-projects
					final Long newProjectID = selectedProject.taskID.getValue();
					if (newProjectID != null && taskID.equals(newProjectID)) {
						MinorTask.chat("No self-referential projects please");
					} else {
						final Long taskProjectID = task.projectID.getValue();
						final Date taskStartDate = task.startDate.getValue();
						final Date taskDeadlineDate = task.finalDate.getValue();
						// ensure the tree integrity by promoting any separated tasks
						List<Task> projectPathTasks = MinorTask.getProjectPathTasks(newProjectID, minortask.getUserID());
						for (Task projectPathTask : projectPathTasks) {
							final DBInteger projectID = projectPathTask.projectID;
							if (taskID.equals(projectID.longValue())) {
								projectPathTask.projectID.setValue(taskProjectID);
							}
							// force the starting date upwards
							if (taskStartDate != null && projectPathTask.startDate.dateValue() != null) {
								if (projectPathTask.startDate.dateValue().after(taskStartDate)) {
									projectPathTask.startDate.setValue(taskStartDate);
								}
							}
							// force the deadline downwards
							final Date projectPathTaskDeadlineDate = projectPathTask.finalDate.dateValue();
							if (taskDeadlineDate != null && projectPathTaskDeadlineDate != null) {
								if (projectPathTaskDeadlineDate.before(taskDeadlineDate)) {
									task.finalDate.setValue(projectPathTaskDeadlineDate);
								}
							}
							// save the project task if necessary
							if (projectPathTask.hasChangedSimpleTypes()) {
								MinorTask.getDatabase().update(projectPathTask);
							}
						}
						task.projectID.setValue(newProjectID);
						MinorTask.getDatabase().update(task);
						// enforce date constraints on tree
						MinorTask.enforceDateConstraintsOnTaskTree(task);
						picker.add(picker.getCurrentProjectComponent());
						MinorTask.showTask(taskID);
					}
				} else {
					task.projectID.setValueToNull();
					MinorTask.getDatabase().update(task);
					picker.add(picker.getCurrentProjectComponent());
					MinorTask.showTask(taskID);
				}
			} catch (SQLException | MinorTask.InaccessibleTaskException ex) {
				MinorTask.sqlerror(ex);
			}
		}
	}

	private static class TasksDataProvider extends ListDataProvider<Task> {

		public TasksDataProvider(Collection<Task> items) {
			super(items);
		}

		@Override
		public Object getId(Task item) {
			return item == null ? -1 : item.name.getValue();
		}
	}

	private static class ProjectComboBox extends ComboBox<Task> {

		private final Task emptyTask;

		public ProjectComboBox(String label, Collection<Task> items, Task emptyTask) {
			super(label, items);
			this.emptyTask = emptyTask;
			setItemLabelGenerator((item) -> {
				if (item == null || item.equals(emptyTask)) {
					return "Projects";
				} else {
					return item.name.stringValue();
				}
			});
		}

		@Override
		public Task getEmptyValue() {
			return emptyTask;
		}
	}

}
