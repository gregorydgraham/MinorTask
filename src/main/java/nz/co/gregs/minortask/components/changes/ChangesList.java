/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.changes;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.HasToolTip.Position;
import nz.co.gregs.minortask.components.banner.IconWithToolTip;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.components.generic.SecureSpan;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/changelist.css")
public class ChangesList extends SecureDiv implements MinorTaskEventListener, MinorTaskEventNotifier {

	private final Div gridDiv = new Div();
	private final SecureDiv label = new SecureDiv();
	private final Div footer = new Div();
	private final Div header = new Div();

	public ChangesList() {
		super();
		setupChangeList();
		buildComponent();
		this.addClassName("changelist");
	}

	public final void buildComponent() {
		Div well = new Div();
		well.addClassName(getListClassName());
		well.addClassName("changelist-well");
		try {
			add(getControlsAbove());
			List<Changes> allRows = getPermittedChanges();
			setLabel(allRows);
			header.removeAll();
			header.addClassName("changelist-header");
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
			footer.addClassNames(getListClassName(), "changelist-footer", getListClassName() + "-footer");
			well.add(footer);
		} catch (SQLException ex) {
			MinorTask.sqlerror(ex);
		}
		add(well);
	}

	private List<Changes> getPermittedChanges() throws SQLException {
		List<Changes> permittedChanges = new ArrayList<>(0);
		List<Changes> changes = getChangesToList();
		if (changes != null) {
			changes
					.stream()
					.forEachOrdered((t) -> {
						if (checkForPermission()) {
							permittedChanges.add(t);
						}
					});
		}
		return permittedChanges;
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

	protected String getListClassName() {
		return "recentchanges";
	}

	protected String getListCaption(List<Changes> changes) {
		return "Recent Changes";
	}

	protected List<Changes> getChangesToList() throws SQLException {
		Changes changes = new Changes();
		final User currentUser = getCurrentUser();
		if (currentUser != null) {
			changes.userid.permittedValues(currentUser.getUserID());
			DBQuery query = getDatabase().getDBQuery(changes, new Task());
			query.setSortOrder(
					changes.column(changes.createdDate).descending(),
					changes.column(changes.changeID).descending()
			);
			query.setPageSize(10);
			System.out.println("CHANGES: " + query.getSQLForQuery());
			return query.getPage(0)
					.stream()
					.map((t) -> t.get(changes))
					.collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	protected Component[] getFooterExtras() {
		return new Component[]{};
	}

	protected Component[] getHeaderExtras() {
		return new Component[]{};
	}

	private void setLabel(List<Changes> allRows) {
		final String caption = getListCaption(allRows);
		label.removeAll();
		label.add(new Label(caption));
	}

	private void setupGrid(List<Changes> allRows) {
		gridDiv.addClassName("task-list-grid-container");
		setGridItems(allRows);
	}

	private void setGridItems(List<Changes> allRows) {
		gridDiv.removeAll();
		allRows.stream().forEachOrdered(
				(t) -> {
					final Div div = new Div(
							getPrefixComponent(t),
							getDescriptionComponent(t),
							getSuffixComponent(t)
					);
					div.addClassName("changelist-grid-row");
					gridDiv.add(div);
				}
		);
	}

	private Component getPrefixComponent(Changes change) {
		final IconWithToolTip icon = new IconWithToolTip(VaadinIcon.CLIPBOARD_CHECK);
		icon.addClassName("changelist-entry-prefix");
		return icon;
	}

	private Component getDescriptionComponent(Changes change) {
		final ChangeDescriptionDiv desc = new ChangeDescriptionDiv(change);
		desc.addMinorTaskEventListener(this);
		return desc;
	}

	protected Component getSuffixComponent(Changes change) {
		SecureSpan layout = new SecureSpan();
		layout.addClassName("changelist-entry-suffix");
		return layout;
	}

	public final void refresh() {
		this.getUI().ifPresent((ui) -> {
			ui.access(() -> {
				try {
					if (thereAreRowsToShow()) {
						List<Changes> allRows = getPermittedChanges();
						setLabel(allRows);
						setGridItems(allRows);
					}
				} catch (SQLException ex) {
					Globals.sqlerror(ex);
				}
			});
		});
	}

	protected Component[] getControlsAbove() {
		return new Component[]{};
	}

	protected boolean thereAreRowsToShow() {
		return true;
	}

	protected void setupChangeList() {
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

//	@Override
//	public List<TaskMoveListener> getTaskMoveHandlers() {
//		return taskMoveHandlers;
//	}
	public static abstract class PreQueried extends ChangesList {

		private final List<Changes> list;

		public PreQueried(List<Changes> list) {
			super();
			this.list = list;
			refresh();
		}

		@Override
		protected final List<Changes> getChangesToList() throws SQLException {
			return list;
		}

	}

}
