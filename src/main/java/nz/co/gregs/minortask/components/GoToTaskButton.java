/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.AuthorisedOptionalTaskPage;
import nz.co.gregs.minortask.pages.TaskEditorLayout;

/**
 *
 * @author gregorygraham
 */
public class GoToTaskButton extends SecureButton {

	/**
	 * Default constructor.Creates an empty button.
	 * @param currentTaskID
	 * @param task
	 * @param targetPage
	 */
	public GoToTaskButton(Long currentTaskID, Task task, Class<? extends AuthorisedOptionalTaskPage> targetPage) {
		super();
		if (task == null) {
			setText("Projects");
			setTooltipText("Jump to the projects listing");
			addClickListener(
					(ClickEvent<Button> event) -> {
						if (currentTaskID == null || targetPage == null || targetPage.equals(TaskEditorLayout.class)) {
							// if Projects is the current task or we're on the details page
							MinorTask.showProjects();
						} else {
							MinorTask.showPage(targetPage, null);
						}
					});
		} else if (task.taskID.getValue().equals(currentTaskID)) {
			// clicking the current task should go to the details page
			setText(task.name.getValue());
			setTooltipText("Jump to "+task.name.getValue());
			addClickListener((ClickEvent<Button> event) -> {
				final Long foundID = task.taskID.getValue();
				MinorTask.showTask(foundID);
			});
		} else {
			// jump to the same tab on the new task
			setText(task.name.getValue());
			setTooltipText("Jump to "+task.name.getValue());
			addClickListener((ClickEvent<Button> event) -> {
				final Long foundID = task.taskID.getValue();
				MinorTask.showPage(targetPage, foundID);
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
}
