/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

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
