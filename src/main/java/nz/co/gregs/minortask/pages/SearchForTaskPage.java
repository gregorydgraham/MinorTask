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
import nz.co.gregs.minortask.components.tasklists.SearchedTasksList;
import nz.co.gregs.minortask.MinorTaskEventListener;

//@Route(value="search", layout=MinortaskPage.class)
public class SearchForTaskPage extends AuthorisedPage implements MinorTaskEventListener{

	@Override
	public Component getInternalComponent() {
		final SearchedTasksList searchedTasksList = new SearchedTasksList();
		searchedTasksList.addMinorTaskEventListener(this);
		return searchedTasksList;
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Search";
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
			Globals.showTask(event.getTask());
	}
	
}
