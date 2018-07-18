/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.EditTask;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("task")
@RouteAlias("edit")
@Theme(Lumo.class)
public class TaskEditorLayout extends MinorTaskPage implements HasDynamicTitle{


	public TaskEditorLayout() {
	}


	@Override
	public Component getInternalComponent(Long parameter) {
		return new EditTask(parameter);
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
