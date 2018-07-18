/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import nz.co.gregs.minortask.components.ProjectTaskList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import nz.co.gregs.minortask.components.AbstractTaskList;


@HtmlImport("styles/shared-styles.html")
@Route("projecttasks")
@RouteAlias("projecttasklist")
@Theme(Lumo.class)
public class ProjectTaskListPage extends MinorTaskPage {

	@Override
	protected Component getInternalComponent(Long parameter) {
		AbstractTaskList abstractTaskList = new ProjectTaskList(parameter);
		return abstractTaskList;
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Project Listing";
	}

	
}
