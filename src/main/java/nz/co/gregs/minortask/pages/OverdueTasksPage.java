/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import nz.co.gregs.minortask.components.tasklists.OverdueTasksList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;

@Route("overdue")
public class OverdueTasksPage extends AuthorisedOptionalTaskPage {

	@Override
	public Component getInternalComponent(Long taskID) {
		return new OverdueTasksList(taskID);
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Overdue";
	}


}
