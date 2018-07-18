/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import nz.co.gregs.minortask.components.UrgentTasksList;


@HtmlImport("styles/shared-styles.html")
@Route("urgent")
@RouteAlias("urgenttasks")
@Theme(Lumo.class)
public class UrgentTasksPage extends MinorTaskPage {

	@Override
	public Component getInternalComponent(Long parameter) {
		return new UrgentTasksList(parameter);
	}

	@Override
	public String getPageTitle() {
		return "MinorTask: URGENT";
	}
	
}
