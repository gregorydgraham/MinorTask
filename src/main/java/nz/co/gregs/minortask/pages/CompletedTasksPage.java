/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import nz.co.gregs.minortask.components.ActiveTaskList;
import nz.co.gregs.minortask.components.TaskTabs;


public class CompletedTasksPage extends MinorTaskPage {

	@Override
	public Component getInternalComponent(Long parameter) {
		return new ActiveTaskList(parameter);
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: Completed";
	}
	
}
