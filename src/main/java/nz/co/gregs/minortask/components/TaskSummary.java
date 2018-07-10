/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import nz.co.gregs.minortask.datamodel.Task;

@Tag("task-summary")
public class TaskSummary extends VerticalLayout implements HasMinorTask, ClickNotifier<Component> {

	public TaskSummary(Long taskID, Task task) {

		Label name = new Label(task.name.getValue());
		Label desc = new Label(task.description.getValue());

		name.setSizeFull();
		desc.setSizeFull();
		desc.addClassName("tiny");

		final VerticalLayout summary = new VerticalLayout(name, desc);
		summary.setSpacing(false);
		summary.setWidth("30EM");
		summary.setDefaultHorizontalComponentAlignment(Alignment.START);

		Icon icon = VaadinIcon.ANGLE_RIGHT.create();
		Button arrow = new Button("" + minortask().getActiveSubtasks(task.taskID.longValue(), minortask().getUserID()).size(), icon);
		arrow.setIconAfterText(true);
		arrow.setSizeUndefined();
		arrow.setHeight("100%");
//		arrow.getElement().setAttribute("theme", "inline tertiary");
		final HorizontalLayout hlayout = new HorizontalLayout();
		hlayout.setSpacing(false);
		hlayout.setSizeFull();
		hlayout.addClassNames("card");

		hlayout.add(summary);
		hlayout.add(arrow);
		hlayout.setVerticalComponentAlignment(Alignment.CENTER, arrow);
		this.addClickListener((event) -> {
			minortask().chat("Opening " + task.name.stringValue());
			minortask().showTask(task.taskID.longValue());
		});
		this.add(hlayout);
		this.addClassName("tasksummary");
	}
}
