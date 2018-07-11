/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 *
 * @author gregorygraham
 */
public class LoggedoutComponent extends VerticalLayout implements MinorTaskComponent {

	public LoggedoutComponent() {
		super();
		add(getComponent());
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();

		layout.add(
				new Label("Thank you for using MinorTask"),
				new Anchor("", "return to the login page")
		);
		return layout;
	}

}
