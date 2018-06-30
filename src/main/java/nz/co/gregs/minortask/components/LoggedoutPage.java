/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTaskUI;

/**
 *
 * @author gregorygraham
 */
public class LoggedoutPage extends MinorTaskComponent {

	public LoggedoutPage(MinorTaskUI ui) {
		super(ui);
	}

	@Override
	public void handleDefaultButton() {
	}

	@Override
	public Component getComponent() {
		VerticalLayout layout = new VerticalLayout();
		
		Button button = new Button("Return to Login");
		button.addClickListener((event) -> {
			new LoginPage(ui).show();
		});
		
		layout.addComponents(new Label("Thank you for using MinorTask"),new Link("return to the login page", new ExternalResource("/")));
		return layout;
	}

	@Override
	public void handleEscapeButton() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
