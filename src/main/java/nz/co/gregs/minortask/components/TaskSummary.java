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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.Task;

public class TaskSummary extends MinorTaskComponent {

	public TaskSummary(MinorTaskUI ui, Long taskID) {
		super(ui, taskID);
	}

	TaskSummary(MinorTaskUI minortask, Long taskID, Task task) {
		super(minortask, taskID);
		
		Label name = new Label(task.name.getValue());
		Label desc = new Label(task.description.getValue());

		name.setWidth(10, Sizeable.Unit.CM);
		desc.setWidth(20, Sizeable.Unit.CM);
		desc.setHeight(3, Sizeable.Unit.EM);
		desc.addStyleName("tiny");
		
		final VerticalLayout summary = new VerticalLayout(name, desc);
		summary.setSpacing(false);
		summary.setWidth(10, Sizeable.Unit.CM);
		summary.setDefaultComponentAlignment(Alignment.TOP_LEFT);
		summary.addLayoutClickListener(new TaskClickListener(task));
		summary.addStyleName("card");
		this.setCompositionRoot(summary);
	}

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
