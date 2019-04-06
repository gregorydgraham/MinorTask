/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import nz.co.gregs.minortask.components.RootTaskComponent;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
//@Route(value="projects", layout = MinortaskPage.class)
public class ProjectsLayout extends AuthorisedOptionalTaskPage{

	@Override
	public String getPageTitle() {
		return "MinorTask: Projects";
	}

	@Override
	protected Component getInternalComponent(Task parameter) {
		return new RootTaskComponent(null);
	}

}
