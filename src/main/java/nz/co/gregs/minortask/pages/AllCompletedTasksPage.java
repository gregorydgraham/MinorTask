/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.components.tasklists.AllCompletedTasksList;

@Route("complete")
public class AllCompletedTasksPage extends MinorTaskPage {

	@Override
	public Component getInternalComponent(Long parameter) {
		return new AllCompletedTasksList(parameter);
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Completed";
	}
	
}
