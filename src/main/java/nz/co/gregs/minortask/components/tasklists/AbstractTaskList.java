/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.HasToolTip.Position;
import nz.co.gregs.minortask.components.IconWithToolTip;
import nz.co.gregs.minortask.components.SecureDiv;
import nz.co.gregs.minortask.components.SecureSpan;
import nz.co.gregs.minortask.components.SecureTaskDiv;
import nz.co.gregs.minortask.components.task.SecureTaskSpan;
import nz.co.gregs.minortask.datamodel.FavouritedTasks;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/abstract-task-list.css")
public abstract class AbstractTaskList extends SecureTaskDiv {

	private final Div gridDiv = new Div();
	private final SecureDiv label = new SecureDiv();
	private final Div footer = new Div();
	private final Div header = new Div();

	public AbstractTaskList() {
		this((Long) null);
	}

	public AbstractTaskList(Long taskID) {
		super(taskID);
		setupTaskList();
		buildComponent();
		this.addClassName("tasklist");
	}

	public final void buildComponent() {
		Div well = new Div();
		well.addClassName(getListClassName());
		well.addClassName("tasklist-well");
		try {
			add(getControlsAbove());
			List<Task> allRows = getPermittedTasks();
			setLabel(allRows);
			header.removeAll();
			header.addClassName("tasklist-header");
			header.add(label);
			Div headerRight = new Div();
			headerRight.addClassName("right");
			final Component[] headerExtras = getHeaderExtras();
			if (headerExtras.length > 0) {
				headerRight.add(headerExtras);
			}
			header.add(headerRight);
			well.add(header);

			setupGrid(allRows);
			well.add(gridDiv);

			footer.removeAll();
			final Component[] footerExtras = getFooterExtras();
			if (footerExtras.length > 0) {
				footer.add(footerExtras);
			}
			footer.addClassNames(getListClassName(), "tasklist-footer", getListClassName() + "-footer");
			well.add(footer);
		} catch (SQLException ex) {
			MinorTask.sqlerror(ex);
		}
		add(well);
	}

	private List<Task> getPermittedTasks() throws SQLException {
		List<Task> permittedTasks = new ArrayList<>(0);
		List<Task> tasks = getTasksToList();
		if (tasks != null) {
			tasks.forEach((t) -> {
				if (checkForPermission(t)) {
					permittedTasks.add(t);
				}
			});
		}
		return permittedTasks;
	}

	@Override
	public final void setTooltipText(String text) {
		label.setTooltipText(text);
	}

	@Override
	public void setTooltipText(String text, Position posn) {
		label.setTooltipText(text, posn);
	}

	@Override
	public void setToolTipPosition(Position posn) {
		label.setToolTipPosition(posn);
	}

	protected abstract String getListClassName();

	protected abstract String getListCaption(List<Task> tasks);

	protected abstract List<Task> getTasksToList() throws SQLException;

	protected Component[] getFooterExtras() {
		return new Component[]{};
	}

	protected Component[] getHeaderExtras() {
		return new Component[]{};
	}

	private void setLabel(List<Task> allRows) {
		final String caption = getListCaption(allRows);
		label.removeAll();
		label.add(new Label(caption));
	}

	private void setupGrid(List<Task> allRows) {
		gridDiv.addClassName("task-list-grid-container");
		setGridItems(allRows);
	}

	private void setGridItems(List<Task> allRows) {
		gridDiv.removeAll();
		allRows.forEach(
				(t) -> {
					final Div div = new Div(
							getPrefixComponent(t),
							getDescriptionComponent(t),
							getSubTaskNumberComponent(t),
							getSuffixComponent(t)
					);
					div.addClassName("tasklist-grid-row");
					gridDiv.add(div);
				}
		);
	}

	private Component getPrefixComponent(Task task) {
		final IconWithToolTip heart = new IconWithToolTip(VaadinIcon.HEART);
		heart.addClassName("tasklist-entry-prefix");
		if (minortask().taskIsFavourited(task)) {
			heart.addClickListener((event) -> {
				removeFavourite(task);
				heart.removeClassName("favourited-task-heart");
				heart.addClassName("normal-task-heart");
			});
			heart.addClassName("favourited-task-heart");
		} else {
			heart.addClickListener((event) -> {
				addFavourite(task);
				heart.removeClassName("normal-task-heart");
				heart.addClassName("favourited-task-heart");
			});
			heart.addClassName("normal-task-heart");
		}
		return heart;
	}

	private Component getDescriptionComponent(Task task) {
		SecureSpan name = new SecureSpan();
		name.setText(task.name.getValue());
		SecureSpan desc = new SecureSpan();
		desc.setText(task.description.getValue());

		name.setSizeFull();
		name.addClassNames("tasklist-name");
		desc.setSizeFull();
		desc.addClassNames("tasklist-description");

		final SecureSpan summary = new SecureSpan(name, desc);

		SecureSpan anchor = new SecureSpan(summary);
		anchor.addClassName("tasklist-entry-summary");
		anchor.addClickListener((event) -> {
			Globals.showTask(task.taskID.getValue());
		});

		return anchor;
	}

	private Component getSubTaskNumberComponent(Task task) {
		SecureSpan layout = new SecureSpan();
		layout.addClassName("tasklist-subtask-count");
		Icon icon = VaadinIcon.ANGLE_RIGHT.create();
		final int numberOfSubTasks = MinorTask.getActiveSubtasks(task, minortask().getCurrentUser()).size();
		Label label1 = new Label("" + numberOfSubTasks);
		label1.add(icon);
		SecureSpan wrapped = new SecureSpan(label1);
		layout.add(wrapped);
		layout.addClickListener((event) -> {
			Globals.showTask(task.taskID.getValue());
		});
		return layout;
	}

	private Component getSuffixComponent(Task task) {
		SecureSpan layout = new SecureSpan();
		final int numberOfSubTasks = MinorTask.getActiveSubtasks(task, minortask().getCurrentUser()).size();

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
		layout.addClassName("tasklist-entry-suffix");
		return layout;
	}

//	private Anchor wrapInALinkToTheTask(final Task task, final Component summary) {
//		String url = VaadinService.getCurrent().getRouter().getUrl(TaskEditorLayout.class,
//				task.taskID.getValue());
//		Anchor anchor = new Anchor(url, "");
//		anchor.add(summary);
//		return anchor;
//	}

	protected final void refreshList() {
		try {
			if (thereAreRowsToShow()) {
				List<Task> allRows = getPermittedTasks();
				setLabel(allRows);
				setGridItems(allRows);
			}
		} catch (SQLException ex) {
			Globals.sqlerror(ex);
		}
	}

	protected Component[] getControlsAbove() {
		return new Component[]{};
	}

	protected boolean thereAreRowsToShow() {
		return true;
	}

	private void addFavourite(Task task) {
		try {
			final FavouritedTasks favour = new FavouritedTasks(task, minortask().getCurrentUser());
			Globals.getDatabase().insert(favour);
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

	protected void setupTaskList() {
	}

	protected Div getFooter() {
		return footer;
	}

	protected Div getHeader() {
		return header;
	}

	public static abstract class PreQueried extends AbstractTaskList {

		private final List<Task> list;

		public PreQueried(List<Task> list) {
			super(null);
			this.list = list;
			refreshList();
		}

		@Override
		protected final List<Task> getTasksToList() throws SQLException {
			return list;
		}

	}

}
