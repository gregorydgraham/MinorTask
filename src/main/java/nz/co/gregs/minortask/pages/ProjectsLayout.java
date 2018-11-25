/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.components.RootTaskComponent;

/**
 *
 * @author gregorygraham
 */
@Route(value="projects", layout = MinortaskPage.class)
public class ProjectsLayout extends AuthorisedPage{

	@Override
	public String getPageTitle() {
		return "MinorTask: Projects";
	}

	@Override
	public Component getInternalComponent() {
		return new RootTaskComponent(null);
	}

}
