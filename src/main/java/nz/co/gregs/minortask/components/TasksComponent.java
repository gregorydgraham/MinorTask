/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTaskUI;

/**
 *
 * @author gregorygraham
 */
public class TasksComponent extends AuthorisedComponent {

	public TasksComponent(MinorTaskUI loginUI) {
		super(loginUI, null);
	}

	@Override
	public Component getAuthorisedComponent() {
		VerticalLayout layout = new VerticalLayout();
		
		return layout;
	}
}
