/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import nz.co.gregs.minortask.components.tasklists.UpcomingTasksList;
import nz.co.gregs.minortask.datamodel.Task;


//@Route(value="upcoming", layout = MinortaskPage.class)
public class UpcomingTasksPage extends AuthorisedOptionalTaskPage{

	@Override
	public Component getInternalComponent(Task task) {
		return new UpcomingTasksList(task);
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Upcoming";
	}
	
}
