/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.HasToolTip;
import nz.co.gregs.minortask.components.IconWithClickHandler;
import nz.co.gregs.minortask.components.RequiresLogin;
import nz.co.gregs.minortask.components.SecureDiv;
import nz.co.gregs.minortask.components.SecureTaskDiv;
import nz.co.gregs.minortask.datamodel.FavouritedTasks;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.TaskEditorLayout;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/abstract-task-list.css")
public abstract class AbstractTaskList extends SecureTaskDiv implements RequiresLogin, HasToolTip {

	protected final Long taskID;
	private final Grid<Task> grid = new Grid<Task>();
	private final Div gridDiv = new Div(grid);
	private final Label label = new Label();
//	private List<Task> list = new ArrayList<>(0);

	public AbstractTaskList() {
		this((Long) null);
	}

	public AbstractTaskList(Long taskID) {
		super(taskID);
		this.taskID = taskID;
		buildComponent();
//		this.setSpacing(false);
		this.addClassName("tasklist");
	}

	public final void buildComponent() {
		VerticalLayout well = new VerticalLayout();
		well.addClassName(getListClassName());
		well.setSpacing(false);
		well.addClassName("well");
		try {
			add(getControlsAbove());
			List<Task> allRows = getPermittedTasks();
			setLabel(allRows);
			HorizontalLayout header = new HorizontalLayout();
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

			Div footer = new Div();
			final Component[] footerExtras = getFooterExtras();
			if (footerExtras.length > 0) {
				footer.add(footerExtras);
			}
			footer.addClassNames(getListClassName(), "footer", getListClassName() + "-footer");
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
		label.addClassName("tooltip");
		Div span = new Div(new Paragraph(text));
		label.getElement().insertChild(0, span.getElement());
		span.addClassName("tooltiptext");

		gridDiv.addClassName("tooltip");
		Div span2 = new Div(new Paragraph(text));
		gridDiv.getElement().insertChild(0, span2.getElement());
		span2.addClassName("tooltiptext");
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
		label.setText(caption);
	}

	private void setupGrid(List<Task> allRows) {
		setGridColumns();
		gridDiv.addClassName("task-list-grid-container");
		setGridItems(allRows);
	}
	
	private void setGridItems(List<Task> allRows) {
		grid.setItems();//clear it first
		grid.setItems(allRows);
	}

	private void setGridColumns() {
		grid.setHeightByRows(true);
		grid.addComponentColumn((Task source) -> getPrefixComponent(source)).setWidth("30px").setFlexGrow(0);
		grid.addComponentColumn((Task source) -> getDescriptionComponent(source)).setFlexGrow(20);
		grid.addComponentColumn((Task source) -> getSubTaskNumberComponent(source)).setWidth("4em");
		grid.addComponentColumn((Task source) -> getSuffixComponent(source)).setWidth("50px").setFlexGrow(0);
	}

	private Component getPrefixComponent(Task task) {
		final IconWithClickHandler heart = new IconWithClickHandler(VaadinIcon.HEART);
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
		Div name = new Div();
		name.setText(task.name.getValue());
		Div desc = new Div();
		desc.setText(task.description.getValue());

		name.setSizeFull();
		name.addClassNames("tasklist-name");
		desc.setSizeFull();
		desc.addClassNames("tasklist-description");

		final Div summary = new Div(name, desc);

		Anchor anchor = wrapInALinkToTheTask(task, summary);
		anchor.addClassName("tasklist-entry-summary");

		return anchor;
	}

	private Component getSubTaskNumberComponent(Task task) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addClassName("tasklist-subtask-count");
		Icon icon = VaadinIcon.ANGLE_RIGHT.create();
		final int numberOfSubTasks = MinorTask.getActiveSubtasks(task, minortask().getCurrentUser()).size();
		Label label1 = new Label("" + numberOfSubTasks);
		label1.add(icon);
		Component wrapped = wrapInALinkToTheTask(task, label1);
		layout.add(wrapped);

		return layout;
	}

	private Component getSuffixComponent(Task task) {
		SecureDiv layout = new SecureDiv();
		final int numberOfSubTasks = MinorTask.getActiveSubtasks(task, minortask().getCurrentUser()).size();

		final IconWithClickHandler checkIcon = new IconWithClickHandler(VaadinIcon.CHECK);
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

	private Anchor wrapInALinkToTheTask(final Task task, final Component summary) {
		String url = VaadinService.getCurrent().getRouter().getUrl(TaskEditorLayout.class,
				task.taskID.getValue());
		Anchor anchor = new Anchor(url, "");
		anchor.add(summary);
		return anchor;
	}

	protected void refreshList() {
		try {
			if (thereAreRowsToShow()) {
				List<Task> allRows = getPermittedTasks();
				setLabel(allRows);
				setGridItems(allRows);
			}
		} catch (SQLException ex) {
			sqlerror(ex);
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
			final FavouritedTasks favour = new FavouritedTasks(task, getCurrentUser());
			getDatabase().insert(favour);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void removeFavourite(Task task) {
		try {
			final FavouritedTasks favour = new FavouritedTasks();
			favour.taskID.setValue(task.taskID);
			favour.userID.setValue(getCurrentUserID());
			getDatabase().delete(favour);
		} catch (SQLException ex) {
			sqlerror(ex);

		}
	}

	public static abstract class PreQueried extends AbstractTaskList {

		private final List<Task> list;

		public PreQueried(List<Task> list) {
			super(null);
			this.list = list;
			buildComponent();
			this.addClassName("tasklist");
		}

		@Override
		protected List<Task> getTasksToList() throws SQLException {
			return list;
		}

	}

}
