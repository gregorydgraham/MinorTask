/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.components.tasklists.TodaysTasksList;

/**
 *
 * @author gregorygraham
 */
@Route("today")
public class TodaysTaskLayout extends MinorTaskPage{

	@Override
	protected Component getInternalComponent(Long parameter) {
		return new TodaysTasksList(taskID);
	}

	@Override
	public String getPageTitle() {
		return minortask().getApplicationName()+": Today's Tasks";
	}

}
