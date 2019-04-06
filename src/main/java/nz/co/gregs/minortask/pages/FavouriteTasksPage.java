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
import nz.co.gregs.minortask.components.tasklists.FavouritedTasksList;
import nz.co.gregs.minortask.MinorTaskEventListener;

//@Route(value = "favourites", layout = MinortaskPage.class)
public class FavouriteTasksPage extends AuthorisedPage implements MinorTaskEventListener {

	@Override
	protected Component getInternalComponent() {
		final FavouritedTasksList favouritedTasksList = new FavouritedTasksList();
		favouritedTasksList.addMinorTaskEventListener(this);
		return favouritedTasksList;
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Favourites";
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		Globals.showTask(event.getTask());
	}

}
