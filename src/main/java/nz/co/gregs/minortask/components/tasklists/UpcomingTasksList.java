/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.html.Label;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

//@Tag("upcoming-task-list")
public class UpcomingTasksList extends AbstractTaskListOfDBQueryRow {

	private static final int DAYS_AHEAD = +3;

	public UpcomingTasksList() {
		super();
		setTooltipText("You'll need to be doing these soon, but you've got a little time yet");
	}
	
	public UpcomingTasksList(Long task) {
		super(task);
		setTooltipText("You'll need to be doing these soon, but you've got a little time yet");
	}
	
	public UpcomingTasksList(Task task) {
		super(task);
		setTooltipText("You'll need to be doing these soon, but you've got a little time yet");
	}

	@Override
	public String getListClassName() {
		return "upcomingtasklist";
	}

	@Override
	protected Label getListCaption(List<DBQueryRow> tasks) {
		return new Label(tasks.size() + " Tasks will start within " + DAYS_AHEAD + " days");
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		LocalDate nowLocalDate = LocalDate.now();
		Date now = Globals.asDate(nowLocalDate);
		final LocalDate plusDays = nowLocalDate.plusDays(DAYS_AHEAD);
		Date threeDaysHence = Globals.asDate(plusDays);
		if (getTaskID() == null) {
			Task.Project.WithSortColumns example = new Task.Project.WithSortColumns();
			example.startDate.permittedRange(now, threeDaysHence);
			example.completionDate.permitOnlyNull();
			final Task task = new Task();
			final DBQuery query = MinorTask.getDatabase().getDBQuery(example).addOptional(task);
			// add user requirement
			query.addCondition(
					example.column(example.userID).is(getCurrentUserID())
							.or(
									example.column(example.assigneeID).is(getCurrentUserID())
							)
			);
			query.addCondition(task.column(task.taskID).isNull());
			query.setSortOrder(
					example.column(example.isOverdue).descending(),
					example.column(example.hasStarted).descending(),
					example.column(example.finalDate).ascending(),
					example.column(example.startDate).ascending(),
					example.column(example.name).ascending()
			);
			return query.getAllRows();
//			List<Task> tasks = query.getAllInstancesOf(example);
//			return tasks;
		} else {
			List<Task> descendants = minortask().getTasksOfProject(getTaskID());

			List<Long> taskIDs = descendants
					.stream()
					.filter((t) -> 
							t.completionDate.getValue() == null
						&& !t.taskID.getValue().equals(getTaskID())
						&& t.startDate.getValue() != null
						&& t.startDate.getValue().after(now)
						&& t.startDate.getValue().before(threeDaysHence)
					)
					.map((t) -> t.taskID.getValue())
					.collect(Collectors.toList());
			Task example = new Task();
			example.taskID.permittedValues(taskIDs.toArray(new Long[]{}));
			DBQuery query = getDatabase().getDBQuery(example);
			query.addCondition(
					example.column(example.userID).is(minortask().getCurrentUserID())
							.or(
									example.column(example.assigneeID).is(minortask().getCurrentUserID())
							)
			);
			List<DBQueryRow> list = query.getAllRows();
			return list;
//			List<Task> descendants = minortask().getTasksOfProject(getTaskID());
//			List<Task> tasks = new ArrayList<>();
//			descendants.stream().filter((t) -> {
//				return t.completionDate.getValue() == null
//						&& !t.taskID.getValue().equals(getTaskID())
//						&& t.startDate.getValue() != null
//						&& t.startDate.getValue().after(now)
//						&& t.startDate.getValue().before(threeDaysHence);
//			}).forEach(tasks::add);
//			return tasks;
		}
	}
}
