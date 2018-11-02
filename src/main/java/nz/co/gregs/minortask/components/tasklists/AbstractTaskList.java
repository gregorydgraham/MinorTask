/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.RequiresLogin;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.TaskEditorLayout;

/**
 *
 * @author gregorygraham
 */
public abstract class AbstractTaskList extends VerticalLayout implements RequiresLogin {

	protected final Long taskID;
	private final Grid<Task> grid = new Grid<Task>();
	private final Label label = new Label();
	private List<Task> list = new ArrayList<>(0);

	public AbstractTaskList() {
		this((Long) null);
	}

	public AbstractTaskList(Long taskID) {
		this.taskID = taskID;
		buildComponent();
		this.setSpacing(false);
		this.addClassName("tasklist");
	}

	protected AbstractTaskList(List<Task> list) {
		this.taskID = null;
		this.list = list;
		buildComponent();
		this.addClassName("tasklist");
	}

	public final void buildComponent() {
		VerticalLayout well = new VerticalLayout();
		well.addClassName(getListClassName());
		well.setSpacing(false);
		well.addClassName("well");
		try {
			add(getControlsAbove());
			List<Task> allRows = getTasksToList();
			setLabel(allRows);
			HorizontalLayout header = new HorizontalLayout();
			header.add(label);
			final Component[] headerExtras = getHeaderExtras();
			if (headerExtras.length > 0) {
				header.add(headerExtras);
			}
			well.add(header);

			setGridItems(allRows);
			setGridColumns();
			well.add(grid);

			HorizontalLayout footer = new HorizontalLayout();
			final Component[] footerExtras = getFooterExtras();
			if (footerExtras.length > 0) {
				footer.add(footerExtras);
			}
			footer.addClassNames(getListClassName(), "footer");
			well.add(footer);
		} catch (SQLException ex) {
			MinorTask.sqlerror(ex);
		}
		add(well);
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

	private void setGridItems(List<Task> allRows) {
		grid.setItems(allRows);
	}

	private void setLabel(List<Task> allRows) {
		final String caption = getListCaption(allRows);
		label.setText(caption);
	}

	private void setGridColumns() {
		grid.setHeightByRows(true);
		grid.addComponentColumn((Task source) -> getDescriptionComponent(source)
		).setFlexGrow(20);
		grid.addComponentColumn((Task source) -> getSubTaskNumberComponent(source));
	}

	private Component getDescriptionComponent(Task task) {
		Label name = new Label(task.name.getValue());
		Label desc = new Label(task.description.getValue());

		name.setSizeFull();
		desc.setSizeFull();
		desc.addClassNames("tiny", "tasklist-description");

		final VerticalLayout summary = new VerticalLayout(name, desc);
		summary.setSpacing(false);

		Component anchor = wrapInALinkToTheTask(task, summary);

		return anchor;
	}

	private Component getSubTaskNumberComponent(Task task) {
		Icon icon = VaadinIcon.ANGLE_RIGHT.create();
		Label label1 = new Label("" + MinorTask.getActiveSubtasks(task.taskID.longValue(), minortask().getUserID()).size());
		label1.add(icon); 
		return wrapInALinkToTheTask(task, label1);
	}

	private Component wrapInALinkToTheTask(final Task task, final Component summary) {
		String url = VaadinService.getCurrent().getRouter().getUrl(TaskEditorLayout.class, task.taskID.getValue());
		Anchor anchor = new Anchor(url, "");
		anchor.add(summary);
		return anchor;
	}

	protected void refreshList() {
		try {
			if (thereAreRowsToShow()) {
				List<Task> allRows = getTasksToList();
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

	public static abstract class PreQueried extends AbstractTaskList {

		public PreQueried(List<Task> list) {
			super(list);
		}

		@Override
		protected List<Task> getTasksToList() throws SQLException {
			return super.list;
		}

	}

}
