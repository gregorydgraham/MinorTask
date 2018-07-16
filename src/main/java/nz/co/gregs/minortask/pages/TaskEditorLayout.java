/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.dependency.HtmlImport;
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
import nz.co.gregs.minortask.components.AuthorisedBannerMenu;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.EditorTask;
import nz.co.gregs.minortask.datamodel.Task;

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
		if (minortask().getNotLoggedIn()) {
			minortask().showLogin();
		} else {
			add(new AuthorisedBannerMenu(parameter));
			add(new EditorTask(parameter));
			add(new FooterMenu());
		}
	}

	@Override
	public String getPageTitle() {
		try {
			final Task task = getTask(taskID);
			if (taskID == null || task == null) {
				return "MinorTask: Projects";
			} else {
				return "MinorTask: " + task.name.stringValue();
			}
		} catch (MinorTask.InaccessibleTaskException ex) {
			Logger.getLogger(TaskEditorLayout.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "MinorTask: Access Denied";
	}

}
