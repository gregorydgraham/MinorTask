/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.components.tasklists.RecentlyViewedTasks;

@Route(value="recent", layout = MinortaskPage.class)
public class RecentTasksPage extends AuthorisedPage {

	@Override
	protected Component getInternalComponent() {
		return new RecentlyViewedTasks();
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Recent";
	}
	
}
