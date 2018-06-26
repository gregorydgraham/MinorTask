/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTaskUI;

/**
 *
 * @author gregorygraham
 */
public class LoggedoutPage extends MinorTaskPage {

	public LoggedoutPage(MinorTaskUI ui) {
		super(ui);
	}

	@Override
	public void handleDefaultButton() {
	}

	@Override
	public void show() {
		VerticalLayout layout = new VerticalLayout();
		
		Button button = new Button("Return to Login");
		button.addClickListener((event) -> {
			ui.LOGIN.show();
		});
		
		layout.addComponents(new Label("Thank you for using MinorTask"),new Link("return to the login page", new ExternalResource("/")));
		super.show(layout);
	}

	@Override
	public void handleEscapeButton() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
