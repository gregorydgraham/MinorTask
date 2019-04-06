/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.components.tasklists.AllOpenTasksList;
import nz.co.gregs.minortask.datamodel.Task;

//@Route(value="open", layout = MinortaskPage.class)
public class AllOpenTasksPage extends AuthorisedOptionalTaskPage {

	@Override
	protected Component getInternalComponent(Task parameter) {
		return new AllOpenTasksList(parameter);
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Open";
	}
	
}
