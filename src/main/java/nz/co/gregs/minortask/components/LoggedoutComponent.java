/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTask;

/**
 *
 * @author gregorygraham
 */
public class LoggedoutComponent extends PublicComponent {

	public LoggedoutComponent(MinorTask minortask) {
		super(minortask);
		setCompositionRoot(getComponent());
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();

		layout.addComponents(
				new Label("Thank you for using MinorTask"), 
				new Link("return to the login page", new ExternalResource("/")));
		return layout;
	}

}
