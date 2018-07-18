/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public abstract class AbstractTaskList extends VerticalLayout implements RequiresLogin {

	public AbstractTaskList() {
		buildComponent();
		this.addClassName("tasklist");
	}

	public final void buildComponent() {
		VerticalLayout well = new VerticalLayout();
		well.addClassName(getListClassName());
		well.setSpacing(false);
		well.addClassName("well");
		try {
			List<Task> tasks = getTasksToList();
			final String caption = getListCaption(tasks);
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
			for (Task task : tasks) {
				well.add(new TaskSummary(task));
			}
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

}
