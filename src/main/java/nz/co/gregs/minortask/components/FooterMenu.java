/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTask;

/**
 *
 * @author gregorygraham
 */
public class FooterMenu extends MinorTaskComponent {

//	private final MinorTaskUI ui;
//	private final Long taskID;

	public FooterMenu(MinorTask minortask, Long taskID) {
		super(minortask, taskID);
		VerticalLayout layout = new VerticalLayout();
		final Label label = new Label("MinorTask is a simple system to help you manage projects and tasks.");
		label.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(label);
		final Label label1 = new Label("The key concept is that every project is just a collection of minor tasks.");
		label1.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(label1);
		final Label label2 = new Label("MinorTask provides you the tools to simplify all your tasks and projects into their component minor tasks and complete the successfully.");
		label2.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(label2);
		this.setCompositionRoot(layout);
	}

}
