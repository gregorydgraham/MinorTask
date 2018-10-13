/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.components.tasklists.AllOpenTasksList;

@Route("open")
public class AllOpenTasksPage extends AuthorisedPage {

	@Override
	public Component getInternalComponent() {
		return new AllOpenTasksList();
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Open";
	}
	
}
