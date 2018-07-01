/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public abstract class TaskListComponent extends MinorTaskComponent {

	public TaskListComponent(MinorTaskUI ui, Long selectedTask) {
		super(ui, selectedTask);
		this.setCompositionRoot(getComponent());
	}

	public final Component getComponent() {

		VerticalLayout layout = new VerticalLayout();
		try {

			List<Task.WithSortColumns> tasks = getTasksToList();
			final String caption = tasks.size() + " "+getTaskDescriptor()+" Tasks";
			layout.addComponent(addTasksToLayout(caption, tasks));
		} catch (SQLException ex) {
			Helper.sqlerror(ex);
		}
		return layout;
	}

	public AbstractLayout addTasksToLayout(String caption, List<Task.WithSortColumns> tasks) {
		VerticalLayout listLayout = new VerticalLayout();
		listLayout.addStyleName("well");
		listLayout.setCaption(caption);
		for (Task task : tasks) {
			TaskClickListener taskClickListener = new TaskClickListener(task);

			Label name = new Label(task.name.getValue());
			Label desc = new Label(task.description.getValue());

			name.setWidth(10, Sizeable.Unit.CM);
			desc.setWidth(20, Sizeable.Unit.CM);
			desc.setHeight(3, Sizeable.Unit.EM);
			desc.addStyleName("tiny");

			final VerticalLayout summary = new VerticalLayout(name, desc);
			summary.setWidth(10, Sizeable.Unit.CM);
			summary.setDefaultComponentAlignment(Alignment.TOP_LEFT);

			summary.addLayoutClickListener(taskClickListener);
			summary.addStyleName("card");

			listLayout.addComponent(summary);
		}
		return listLayout;
	}

	protected abstract List<Task.WithSortColumns> getTasksToList() throws SQLException;

	protected abstract String getTaskDescriptor() ;

	private class TaskClickListener implements LayoutEvents.LayoutClickListener, FieldEvents.FocusListener {

		private final Task task;

		public TaskClickListener(Task task) {
			this.task = task;
		}

		@Override
		public void layoutClick(LayoutEvents.LayoutClickEvent event) {
			handleEvent(event);
		}

		@Override
		public void focus(FieldEvents.FocusEvent event) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void handleEvent(LayoutEvents.LayoutClickEvent event) {
			//Helper.chat("Switching to " + task.name.getValue());
			if (event.getButton() == MouseEventDetails.MouseButton.LEFT) {
				final Long taskID = task.taskID.getValue();
				minortask().showTask(taskID);
			}
		}

	}

}
