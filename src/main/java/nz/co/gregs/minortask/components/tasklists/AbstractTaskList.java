/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.minortask.components.RequiresLogin;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public abstract class AbstractTaskList extends VerticalLayout implements RequiresLogin {
	
	protected final Long taskID;
	private final Grid<Task> grid = new Grid<Task>();
	private List<Task> allRows = new ArrayList<>(0);
	
	public AbstractTaskList(Long taskID) {
		this.taskID = taskID;
		buildComponent();
		this.addClassName("tasklist");
	}
	
	public final void buildComponent() {
		VerticalLayout well = new VerticalLayout();
		well.addClassName(getListClassName());
		well.setSpacing(false);
		well.addClassName("well");
		try {
			allRows = getTasksToList();
			final String caption = getListCaption(allRows);
			final Label label = new Label(caption);
			label.setWidth("100%");
			HorizontalLayout header = new HorizontalLayout();
			header.add(label);
			final Component[] headerExtras = getHeaderExtras();
			if (headerExtras.length > 0) {
				header.add(headerExtras);
			}
			header.setWidth("100%");
			well.add(header);
			
			setGridItems();
			setGridColumns();
			well.add(grid);
			
			HorizontalLayout footer = new HorizontalLayout();
			footer.setWidth("100%");
			final Component[] footerExtras = getFooterExtras();
			if (footerExtras.length > 0) {
				footer.add(footerExtras);
			}
			footer.addClassNames(getListClassName(), "footer");
			well.add(footer);
		} catch (SQLException ex) {
			minortask().sqlerror(ex);
		}
		add(well);
	}
	
	protected abstract String getListClassName();
	
	protected abstract String getListCaption(List<Task> tasks);
	
	protected abstract List<Task> getTasksToList() throws SQLException;
	
	protected Component[] getFooterExtras() {
		return new Component[]{};
	}
	
	private Component[] getHeaderExtras() {
		return new Component[]{};
	}
	
	private void setGridItems() {
		grid.setItems(allRows);
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
		desc.addClassName("tiny");
		
		final VerticalLayout summary = new VerticalLayout(name, desc);
		summary.setSpacing(false);
		Anchor anchor = new Anchor("task/"+task.taskID.getValue(), "");
		anchor.add(summary);
		
		return anchor;
	}
	
	private Component getSubTaskNumberComponent(Task task) {
		Icon icon = VaadinIcon.ANGLE_RIGHT.create();
		Button arrow = new Button("" + minortask().getActiveSubtasks(task.taskID.longValue(), minortask().getUserID()).size(), icon);
		arrow.setIconAfterText(true);
		arrow.addClickListener((event) -> {
			minortask().showTask(task.taskID.longValue());
		});
		return arrow;
	}
	
}
