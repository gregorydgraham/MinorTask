/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import nz.co.gregs.minortask.MinorTaskUI;

/**
 *
 * @author gregorygraham
 */
public class TasksPage extends AuthorisedPage {

	public TasksPage(MinorTaskUI loginUI) {
		super(loginUI, null);
	}

	@Override
	public void show() {
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label("MinorTask is a simple system to help you create projects and tasks."));
		layout.addComponent(new Label("The key concept is that every task or project is just a series of minor tasks."));
		layout.addComponent(new Label("MinorTask provides you the tools to break all your tasks and projects into their component minor tasks and complete the successfully."));

		show(layout);
	}
}
