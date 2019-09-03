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
import com.vaadin.flow.component.html.Span;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.HasToolTip.Position;
import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.components.task.SecureTaskDiv;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 * @param <S>
 */
@StyleSheet("styles/abstract-task-list.css")
public abstract class AbstractTaskList<S> extends SecureTaskDiv implements MinorTaskEventListener, MinorTaskEventNotifier {

	private final Div gridDiv = new Div();
	private final SecureDiv label = new SecureDiv(new Label("List"));
	private final Div footer = new Div();
	private final Div header = new Div();

	public AbstractTaskList() {
		this((Task) null);
	}
	public AbstractTaskList(Task task) {
		super();
		setTask(task);
		initTaskList();
	}

	private void initTaskList() {
		setupTaskList();
		buildComponent();
		this.addClassName("tasklist");
		this.addClassName(this.getListClassName());
		this.addAttachListener((event) -> refresh());
	}

	public final void buildComponent() {
		add(getControlsAbove());

		Div well = createWell();
		setupWell(well);
		add(well);
	}

	private void setupWell(Div well) {
		setupHeader(header);
		well.add(header);

		setupGrid(gridDiv);
		well.add(gridDiv);

		setupFooter(footer);
		well.add(footer);
	}

	private void setupFooter(Div footer) {
		footer.removeAll();
		final Component[] footerExtras = getFooterExtras();
		if (footerExtras.length > 0) {
			footer.add(footerExtras);
		}
		footer.addClassNames(getListClassName(), "tasklist-footer", getListClassName() + "-footer");
	}

	private Div createWell() {
		Div well = new Div();
		well.addClassName(getListClassName());
		well.addClassName("tasklist-well");
		return well;
	}

	private void setupHeader(Div header) {
		header.removeAll();
		header.addClassName("tasklist-header");
		setDefaultLabel();
		header.add(label);
		Div headerRight = new Div();
		headerRight.addClassName("right");
		final Component[] headerExtras = getHeaderExtras();
		if (headerExtras.length > 0) {
			headerRight.add(headerExtras);
		}
		header.add(headerRight);
	}

	private List<S> getPermittedTasks() throws SQLException {
		List<S> permittedTasks = new ArrayList<>(0);
		List<S> tasks = getTasksToList();
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

	protected abstract Component getListCaption(List<S> tasks);

	protected abstract List<S> getTasksToList() throws SQLException;

	protected Component[] getFooterExtras() {
		return new Component[]{};
	}

	protected Component[] getHeaderExtras() {
		return new Component[]{};
	}

	private void setLabel(List<S> allRows) {
		final Component caption = getListCaption(allRows);
		label.removeAll();
		label.add(caption);
	}

	private void setDefaultLabel() {
		final Component caption = getDefaultLabel();
		label.removeAll();
		label.add(caption);
	}

	private void setupGrid(Div gridDiv) {
		gridDiv.addClassName("task-list-grid-container");
	}

	private void setGridItems(List<S> allRows) {
		gridDiv.removeAll();
		allRows.forEach(
				(row) -> {
					if (row != null) {
						final Div div = new Div(
								protect(getLeftComponent(row), "tasklist-entry-prefix"),
								protect(getCentralComponent(row), "tasklist-entry-content"),
								protect(getRightComponent(row), "tasklist-entry-suffix"));
						div.addClassName("tasklist-grid-row");
						gridDiv.add(div);
					}
				}
		);
	}

	private Component protect(Component comp, String classname) {
		final Span span = new Span();
		if (comp != null) {
			span.add(comp);
		}
		span.addClassName(classname);
		return span;
	}

	abstract protected Component getRowHeaderComponent(S row);

	abstract protected Component getRowFooterComponent(S row);

	abstract protected Component getLeftComponent(S row);

	abstract protected Component getCentralComponent(S row);
	
	abstract protected Component getRightComponent(S row);

	public final void refresh() {
		this.getUI().ifPresent((t) -> {
			t.access(() -> {
				try {
					if (thereAreRowsToShow()) {
						List<S> allRows = getPermittedTasks();
						setLabel(allRows);
						setGridItems(allRows);
					}
				} catch (SQLException ex) {
					Globals.sqlerror(ex);
				}
			});
		});
	}

	@Override
	public void setTask(Task newTask) {
		super.setTask(newTask);
		refresh();
	}

	protected Component[] getControlsAbove() {
		return new Component[]{};
	}

	protected boolean thereAreRowsToShow() {
		return true;
	}

	protected void setupTaskList() {
	}

	protected Div getFooter() {
		return footer;
	}

	protected Div getHeader() {
		return header;
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		fireEvent(event);
	}

	public abstract boolean checkForPermission(S item);

	public Component getDefaultLabel() {
		return new Label("");
	}

	public static abstract class PreQueried<L> extends AbstractTaskList<L> {

		private final List<L> list;

		public PreQueried(List<L> list) {
			super(null);
			this.list = list;
			refresh();
		}

		@Override
		protected final List<L> getTasksToList() throws SQLException {
			return list;
		}

	}

}
