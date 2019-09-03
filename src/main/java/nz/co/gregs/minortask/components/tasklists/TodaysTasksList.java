/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.html.Label;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.datamodel.Task;

public class TodaysTasksList extends AbstractTaskListOfTasks {

	public TodaysTasksList(Task.TaskAndProject task) {
		super(task);
		setTooltipText("Set a start date on the task to have it appear here when you need to start it");
	}

	public TodaysTasksList() {
		super();
		setTooltipText("Set a start date on the task to have it appear here when you need to start it");
	}

	@Override
	protected List<Task.TaskAndProject> getTasksToList() throws SQLException {
		if (getTaskID() == null) {
			final Task.Project project = new Task.Project();
			Task task = new Task();
			project.startDate.permitOnlyPast();
			project.completionDate.permitOnlyNull();
			final DBQuery query = getDatabase().getDBQuery(project).addOptional(task);
			// add user requirement
			query.addCondition(
					project.column(project.userID).is(getCurrentUserID())
							.or(
									project.column(project.assigneeID).is(getCurrentUserID())
							)
			);
			// add the leaf requirement
			query.addCondition(task.column(task.taskID).isNull());
			query.setSortOrder(
					project.column(project.finalDate).ascending().nullsLast(),
					project.column(project.startDate).ascending().nullsLast(),
					project.column(project.name).ascending()
			);
			return query.getAllInstancesOf(project)
					.stream()
					.map((t)-> new Task.TaskAndProject(t, t.project))
					.collect(Collectors.toList());
		} else {
			final Date now = new Date();
			return minortask().getLeafTaskAndProjectsOfProjectFiltered(
					getTaskID(),
					(t) -> t.completionDate.getValue() == null
					&& !t.taskID.getValue().equals(getTaskID())
					&& t.startDate.getValue() != null
					&& t.startDate.getValue().before(now)
			);
		}
	}

	@Override
	protected String getListClassName() {
		return "todaystasklist";
	}

	@Override
	protected Label getListCaption(List<Task.TaskAndProject> tasks) {
		return new Label("" + tasks.size() + " for Today (open tasks with a start date  in the past)");
	}
}
