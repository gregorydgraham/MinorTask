/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.components.banner.IconWithToolTip;
import nz.co.gregs.minortask.components.changes.Changes;
import nz.co.gregs.minortask.components.generic.SecureSpan;
import nz.co.gregs.minortask.components.task.TaskOverviewSpan;
import nz.co.gregs.minortask.datamodel.FavouritedTasks;
import nz.co.gregs.minortask.datamodel.Task;

public abstract class AbstractTaskListOfDBQueryRow extends AbstractTaskList<DBQueryRow> {

	public AbstractTaskListOfDBQueryRow() { 
		super();
	}

	public AbstractTaskListOfDBQueryRow(long taskID) {
		super(taskID);
	}

	public AbstractTaskListOfDBQueryRow(Task task) {
		super(task);
	}

	@Override
	protected Component getLeftComponent(DBQueryRow row) {
		final IconWithToolTip heart = new IconWithToolTip(VaadinIcon.HEART);
		heart.addClassName("tasklist-entry-prefix");
		Task gotTask = row.get(new Task());
		FavouritedTasks gotFav = row.get(new FavouritedTasks());
		if (gotTask != null && gotFav != null && gotFav.favouritedDate.getValue() != null) {
			heart.addClickListener((event) -> {
				removeFavourite(gotTask);
				heart.removeClassName("favourited-task-heart");
				heart.addClassName("normal-task-heart");
			});
			heart.addClassName("favourited-task-heart");
		} else {
			heart.addClickListener((event) -> {
				addFavourite(gotTask);
				heart.removeClassName("normal-task-heart");
				heart.addClassName("favourited-task-heart");
			});
			heart.addClassName("normal-task-heart");
		}
		return heart;
	}

	@Override
	protected Component getCentralComponent(DBQueryRow row) {
		Task gotTask = row.get(new Task());
		Task.Project gotProject = row.get(new Task.Project());
		if (gotTask != null) {
			final TaskOverviewSpan taskOverviewSpan = new TaskOverviewSpan(gotTask, gotProject);
			taskOverviewSpan.addMinorTaskEventListener(this);
			return taskOverviewSpan;
		} else {
			return new Label("Unknown");
		}
	}

	protected Component getSubTaskNumberComponent(Task task, Long numberOfSubTasks) {
		Span layout = new Span();
			Icon icon = VaadinIcon.ANGLE_RIGHT.create();
			Label label1 = new Label("" + numberOfSubTasks);
			label1.add(icon);
			SecureSpan wrapped = new SecureSpan(label1);
			layout.add(wrapped);
			layout.addClickListener((event) -> {
				fireEvent(new MinorTaskEvent(event.getSource(), task, true));
			});
		return layout;
	}

	@Override
	protected Component getRightComponent(DBQueryRow row) {
		SecureSpan layout = new SecureSpan();
		layout.addClassName("tasklist-entry-suffix");
		Task task = row.get(new Task());
		if (task != null) {
			final Long numberOfSubTasks = MinorTask.getActiveSubtaskCount(task, minortask().getCurrentUser());
			layout.add(getSubTaskNumberComponent(task, numberOfSubTasks));
			final IconWithToolTip checkIcon = new IconWithToolTip(VaadinIcon.CHECK);
			checkIcon.addClickListener((event) -> {
				if (task.completionDate.isNull()) {
					minortask().completeTaskWithCongratulations(task);
					checkIcon.removeClassName("tasklist-complete-tick");
					checkIcon.addClassName("tasklist-reopen-tick");
				} else {
					minortask().reopenTask(task);
					checkIcon.removeClassName("tasklist-reopen-tick");
					checkIcon.addClassName("tasklist-complete-tick");
				}
			});
			if (numberOfSubTasks == 0) {
				checkIcon.addClassName("tasklist-endtask");
			} else {
				checkIcon.addClassName("tasklist-project");
			}
			if (task.completionDate.isNull()) {
				checkIcon.addClassName("tasklist-complete-tick");
			} else {
				checkIcon.addClassName("tasklist-reopen-tick");
			}
			layout.add(checkIcon);
		}
		return layout;
	}

	private void addFavourite(Task task) {
		try {
			final FavouritedTasks favour = new FavouritedTasks(task, minortask().getCurrentUser());
			Globals.getDatabase().insert(favour);
			getDatabase().insert(new Changes(getCurrentUser(), task, "Added " + task.name.getValue() + " to Favourites"));
		} catch (SQLException ex) {
			Globals.sqlerror(ex);
		}
	}

	private void removeFavourite(Task task) {
		try {
			final FavouritedTasks favour = new FavouritedTasks();
			favour.taskID.setValue(task.taskID);
			favour.userID.setValue(minortask().getCurrentUserID());
			Globals.getDatabase().delete(favour);
		} catch (SQLException ex) {
			Globals.sqlerror(ex);

		}
	}

	@Override
	protected Component getRowHeaderComponent(DBQueryRow row) {
		return null;
	}

	@Override
	protected Component getRowFooterComponent(DBQueryRow row) {
		return null;
	}

	@Override
	public boolean checkForPermission(DBQueryRow item) {
		return minortask().checkUserCanViewTask(item.get(new Task()));
	}

	public static abstract class PreQueried extends AbstractTaskListOfDBQueryRow {

		private final List<DBQueryRow> list;

		public PreQueried(List<DBQueryRow> list) {
			super(null);
			this.list = list;
			refresh();
		}

		@Override
		protected final List<DBQueryRow> getTasksToList() throws SQLException {
			return list;
		}

	}
}
