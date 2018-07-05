/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.MouseEvents;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import com.vaadin.icons.VaadinIcons;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

public class TaskSummary extends MinorTaskComponent {

	public TaskSummary(MinorTask minortask, Long taskID, Task task) {
		super(minortask, taskID);

		Label name = new Label(task.name.getValue());
		Label desc = new Label(task.description.getValue());

		name.setWidthUndefined();
		desc.setWidth(100, Unit.PERCENTAGE);
		desc.addStyleName("tiny");

		final VerticalLayout summary = new VerticalLayout(name, desc);
		summary.setSpacing(false);
		summary.setWidth(30, Unit.EM);
		summary.setDefaultComponentAlignment(Alignment.TOP_LEFT);

		Label arrow = new Label("" + minortask.getActiveSubtasks(task.taskID.longValue(), minortask.getUserID()).size());
		arrow.setIcon(VaadinIcons.ANGLE_RIGHT);
		arrow.setSizeUndefined();
		arrow.setHeight(100, Unit.PERCENTAGE);
		final HorizontalLayout hlayout = new HorizontalLayout();
		hlayout.setSpacing(false);
		hlayout.setWidth(100, Unit.PERCENTAGE);
		hlayout.addStyleName("card");

		hlayout.addComponent(summary);
		hlayout.addComponent(arrow);
		hlayout.setComponentAlignment(arrow, Alignment.MIDDLE_RIGHT);
		hlayout.addLayoutClickListener(new TaskClickListener(task));

//		Panel panel = new Panel();
//		panel.setIcon(VaadinIcons.ANGLE_RIGHT);
//		panel.setContent(summary);
//		panel.addClickListener(new TaskClickListener(task));
		this.setCompositionRoot(hlayout);
	}

	private class TaskClickListener implements LayoutEvents.LayoutClickListener, FieldEvents.FocusListener, MouseEvents.ClickListener {

		private final Task task;

		public TaskClickListener(Task task) {
			this.task = task;
		}

		@Override
		public void layoutClick(LayoutEvents.LayoutClickEvent event) {
			if (event.getButton() == MouseEventDetails.MouseButton.LEFT) {
				handleEvent();
			}
		}

		@Override
		public void focus(FieldEvents.FocusEvent event) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void handleEvent() {
			final Long taskID = task.taskID.getValue();
			minortask().showTask(taskID);
		}

		@Override
		public void click(MouseEvents.ClickEvent event) {
			handleEvent();
		}

	}
}
