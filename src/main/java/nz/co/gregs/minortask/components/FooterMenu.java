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
public class FooterMenu extends VerticalLayout implements HasMinorTask {

	private final Long taskID;

//	private final MinorTaskUI ui;
//	private final Long taskID;
	public FooterMenu(Long taskID) {
		this.taskID=taskID;
		final Label label = new Label("MinorTask is a simple system to help you manage projects and tasks.");
		label.setSizeFull();
		add(label);
		final Label label1 = new Label("The key concept is that every project is just a collection of minor tasks.");
		label1.setSizeFull();
		add(label1);
		final Label label2 = new Label("MinorTask provides you the tools to simplify all your tasks and projects into their component minor tasks and complete the successfully.");
		label2.setSizeFull();
		add(label2);
	}

}
