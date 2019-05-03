/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.GeneratedVaadinComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.shared.Registration;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.expressions.BooleanExpression;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.MinorTaskEventNotifier;
import nz.co.gregs.minortask.components.AccessDeniedComponent;
import nz.co.gregs.minortask.components.task.SecureTaskDiv;
import nz.co.gregs.minortask.datamodel.Task;

public class TaskProjectPicker extends SecureTaskDiv implements MinorTaskEventNotifier {

	private Task.TaskAndProject taskAndProject = null;
	private final Task emptyTask = new Task();
	private final List<Task> listOfTasks = new ArrayList<>();
	private final ProjectComboBox taskList = new ProjectComboBox("Part Of", listOfTasks, emptyTask);
	private final Div container = new Div();
	private boolean updating = false;

	public TaskProjectPicker() {
		super();
		taskList.addValueChangeListener(new ProjectChosenListener(minortask(), this));
		setTooltipText("Part of this project, you can move it to another from here");
//		label.setLabel("Part Of:");
		add(taskList);
	}

	@Override
	public void setTask(Task task) {
		try {
			setTask(minortask().getTaskAndProject(task));
		} catch (MinorTask.InaccessibleTaskException ex) {
			this.container.add(new AccessDeniedComponent());
		}
	}

	@Override
	public void setTask(Task.TaskAndProject newTaskAndProject) {
		super.setTask(newTaskAndProject);
		try {
			taskAndProject = getTaskAndProject();
			updateProjectList();
		} catch (MinorTask.InaccessibleTaskException ex) {
			this.container.add(new AccessDeniedComponent());
		}
	}

	private void updateProjectList() {
		try {
			updating = true;
			Task example = new Task();
			example.userID.permittedValues(minortask().getCurrentUserID());
			example.taskID.excludedValues(getTaskID());
			example.name.setSortOrderAscending();
			final DBQuery query = getDatabase().getDBQuery(example);
			query.addCondition(
					BooleanExpression.anyOf(
							example.column(example.completionDate).isNull(),
							example.column(example.taskID).is(getProjectID())
					)
			);
			query.setSortOrder(example.column(example.name).ascending());

			listOfTasks.clear();
			listOfTasks.addAll(query.getAllInstancesOf(example));

			listOfTasks.add(0, taskList.getEmptyValue());
			taskList.setDataProvider(new TasksDataProvider(listOfTasks));

			final Task.Project project = taskAndProject.getProject();
			taskList.setValue(project == null ? taskList.getEmptyValue() : project);
		} catch (SQLException ex) {
			sqlerror(ex);
		} finally {
			updating = false;
		}
	}

	private static class ProjectChosenListener implements HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<Task>, Task>> {

//		private final Long taskID;
		private final TaskProjectPicker picker;
		private final MinorTask minortask;

		public ProjectChosenListener(MinorTask minortask, TaskProjectPicker picker) {
			this.minortask = minortask;
			this.picker = picker;
//			this.taskID = taskID;
		}

		@Override
		public void valueChanged(AbstractField.ComponentValueChangeEvent<ComboBox<Task>, Task> event) {
			if (!picker.updating) {
				try {
					final Task task = picker.getTask();
					final Task selectedProject = event.getSource().getValue();
					System.out.println("MOVE TO PROJECT: " + selectedProject.name.getValue());
					if (selectedProject != null) {
						// prevent self-projects
						final Long newProjectID = selectedProject.taskID.getValue();
						if (newProjectID != null && task.taskID.longValue().equals(newProjectID)) {
							MinorTask.chat("No self-referential projects please");
						} else {
							final Long taskProjectID = task.projectID.getValue();
							final Date taskStartDate = task.startDate.getValue();
							final Date taskDeadlineDate = task.finalDate.getValue();
							// ensure the tree integrity by promoting any separated tasks
							List<Task> projectPathTasks = MinorTask.getProjectPathTasks(newProjectID, minortask.getCurrentUserID());
							for (Task projectPathTask : projectPathTasks) {
								final DBInteger projectID = projectPathTask.projectID;
								if (task.taskID.longValue().equals(projectID.longValue())) {
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
							picker.fireEvent(new MinorTaskEvent(picker, picker.getTask(), true));
						}
					} else if (task.projectID.getValue() != null) {
						System.out.println("MOVING PROJECT TO TOP LEVEL.");
						task.projectID.setValueToNull();
						MinorTask.getDatabase().update(task);
						picker.fireEvent(new MinorTaskEvent(picker, picker.getTask(), true));
					} else {
						System.out.println("NOTHING TO CHANGE:");
					}
				} catch (SQLException ex) {
					MinorTask.sqlerror(ex);
				}
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
