/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTaskUI;

/**
 *
 * @author gregorygraham
 */
public class FooterMenu extends MinorTaskComponent {

//	private final MinorTaskUI ui;
//	private final Long taskID;

	public FooterMenu(MinorTaskUI ui, Long taskID) {
		super(ui, taskID);
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label("MinorTask is a simple system to help you manage projects and tasks."));
		layout.addComponent(new Label("The key concept is that every project is just a collection of minor tasks."));
		layout.addComponent(new Label("MinorTask provides you the tools to simplify all your tasks and projects into their component minor tasks and complete the successfully."));
		this.setCompositionRoot(layout);
	}

}
