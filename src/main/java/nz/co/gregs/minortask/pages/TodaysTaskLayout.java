/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.tasklists.TodaysTasksList;

/**
 *
 * @author gregorygraham
 */
@Route(value="today", layout = MinortaskPage.class)
public class TodaysTaskLayout extends AuthorisedOptionalTaskPage{

	@Override
	protected Component getInternalComponent(Long taskID) {
		return new TodaysTasksList(taskID);
	}

	@Override
	public String getPageTitle() {
		return MinorTask.getApplicationName()+": Today's Tasks";
	}

}
