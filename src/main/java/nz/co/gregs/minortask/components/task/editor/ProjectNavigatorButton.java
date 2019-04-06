/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

import nz.co.gregs.minortask.MinorTaskEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.shared.Registration;
import nz.co.gregs.minortask.components.SecureButton;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.MinorTaskEventListener;

/**
 *
 * @author gregorygraham
 */
public class ProjectNavigatorButton extends SecureButton{

	/**
	 * Default constructor.Creates an empty button.
	 * @param currentTaskID
	 * @param task
	 * @param handler
	 */
	public ProjectNavigatorButton(Long currentTaskID, Task task, MinorTaskEventListener handler) {
		super();
		getElement().getStyle().set("cursor", "pointer");
		if (task == null) {
			setText("Projects");
			setTooltipText("Jump to the projects listing");
			addClickListener((ClickEvent<Button> event) -> {
						handler.handleMinorTaskEvent(new MinorTaskEvent(this, task, true));
					});
		} else if (task.taskID.getValue().equals(currentTaskID)) {
			// clicking the current task should go to the details page
			setText(task.name.getValue());
			addClickListener((ClickEvent<Button> event) -> {
//				final Long foundID = task.taskID.getValue();
						handler.handleMinorTaskEvent(new MinorTaskEvent(this, task, true));
			});
		} else {
			// jump to the same tab on the new task
			setText(task.name.getValue());
			setTooltipText("Jump to "+task.name.getValue());
			addClickListener((ClickEvent<Button> event) -> {
//				final Long foundID = task.taskID.getValue();
						handler.handleMinorTaskEvent(new MinorTaskEvent(this, task, true));
			});
		}
		formatButton();
		if ((task != null && task.taskID.getValue().equals(currentTaskID))
				|| (task == null && currentTaskID == null)) {
			this.addClassName("currenttask");
		}
	}

	protected final void formatButton() {
		setIcon(VaadinIcon.ANGLE_RIGHT.create());
		setIconAfterText(true);
		setSizeUndefined();
		addClassNames("small", "projectpath");
		getElement().setAttribute("theme", "small");
	}

	@Override
	public final void setText(String text) {
		super.setText(text);
	}
	
	public Registration addTaskMoveEventListener(
			ComponentEventListener<MinorTaskEvent> listener) {
		return addListener(MinorTaskEvent.class, listener);
	}
}
