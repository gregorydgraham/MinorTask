/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.AccessDeniedComponent;
import nz.co.gregs.minortask.components.CreateTask;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("create")
@RouteAlias("new")
public class TaskCreatorLayout extends MinorTaskPage {

	@Override
	public Component getInternalComponent(Long parameter) {
		try {
			return new CreateTask(parameter);
		} catch (MinorTask.InaccessibleTaskException ex) {
			Logger.getLogger(TaskCreatorLayout.class.getName()).log(Level.SEVERE, null, ex);
			return new AccessDeniedComponent();
		}
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Create";
	}

}
