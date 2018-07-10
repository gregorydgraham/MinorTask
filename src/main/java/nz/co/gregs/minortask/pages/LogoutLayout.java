/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.LoggedoutComponent;


/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("bye")
@RouteAlias("loggedout")
public class LogoutLayout extends VerticalLayout{

	LoggedoutComponent component = new LoggedoutComponent();
		
	public LogoutLayout() {
		final MinorTaskTemplate minorTaskTemplate = new MinorTaskTemplate();
		add(minorTaskTemplate);
		add(component);
	}
	
}
