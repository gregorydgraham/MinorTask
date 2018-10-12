/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import nz.co.gregs.minortask.components.tasklists.ProjectTaskList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.components.tasklists.AbstractTaskList;

@Deprecated
@Route("projecttasks")
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
