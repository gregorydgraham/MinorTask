/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 *
 * @author gregorygraham
 */
public class FooterMenu extends VerticalLayout implements MinorTaskComponent {

//	private final MinorTaskUI ui;
//	private final Long taskID;
	public FooterMenu() {
		final Label label = new Label("MinorTask turns every project into a collection of minor tasks.");
		label.setSizeFull();
		add(label);
		final Label label2 = new Label("MinorTask provides you with the tools to simplify all your tasks and projects and complete them successfully.");
		label2.setSizeFull();
		add(label2);
	}

}
