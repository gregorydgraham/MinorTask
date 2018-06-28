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
		
		show(layout);
	}
}
