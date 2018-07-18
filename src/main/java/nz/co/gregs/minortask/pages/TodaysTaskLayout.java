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
import nz.co.gregs.minortask.components.ActiveTaskList;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("today")
@RouteAlias("todays")
@RouteAlias("todaystasks")
@Theme(Lumo.class)
public class TodaysTaskLayout extends MinorTaskPage{

	@Override
	protected Component getInternalComponent(Long parameter) {
		return new ActiveTaskList(parameter);
	}

	@Override
	public String getPageTitle() {
		return minortask().getApplicationName()+": Today's Tasks";
	}

}
