/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.components.tasklists.RecentlyViewedTasks;
import nz.co.gregs.minortask.MinorTaskEventListener;

//@Route(value="recent", layout = MinortaskPage.class)
public class RecentTasksPage extends AuthorisedPage implements MinorTaskEventListener{

	@Override
	protected Component getInternalComponent() {
		final RecentlyViewedTasks recentlyViewedTasks = new RecentlyViewedTasks();
		recentlyViewedTasks.addMinorTaskEventListener(this);
		return recentlyViewedTasks;
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Recent";
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
			Globals.showTask(event.getTask());
	}
	
}
