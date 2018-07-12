/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.BannerMenu;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.TaskEditor;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("task")
@RouteAlias("edit")
public class TaskEditorLayout extends VerticalLayout implements ChecksLogin, HasDynamicTitle {

	private Long taskID = null;

	public TaskEditorLayout() {
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
		removeAll();
		taskID = parameter;
		add(new MinorTaskTemplate());
		add(new BannerMenu(parameter));
		try {
			add(new TaskEditor(parameter));
		} catch (MinorTask.InaccessibleTaskException ex) {
			Logger.getLogger(TaskEditorLayout.class.getName()).log(Level.SEVERE, null, ex);
			add(new Label("Access Denied"));
		}
		add(new FooterMenu(parameter));
	}

	@Override
	public String getPageTitle() {
		try {
			if (taskID == null) {
				return "MinorTask: Projects";
			} else {
				return "MinorTask: " + getTask(taskID);
			}
		} catch (MinorTask.InaccessibleTaskException ex) {
			Logger.getLogger(TaskEditorLayout.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "MinorTask: Access Denied";
	}

}
