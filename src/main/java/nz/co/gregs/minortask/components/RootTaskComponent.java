/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import nz.co.gregs.minortask.components.tasklists.CompletedProjectsList;
import nz.co.gregs.minortask.components.tasklists.OpenProjectsList;

/**
 *
 * @author gregorygraham
 */
public class RootTaskComponent extends VerticalLayout implements RequiresLogin {

	public RootTaskComponent(Long taskID) {
		add(taskID == null ? getComponent() : new EditTask(taskID));
		setPadding(false);
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setPadding(false);
		layout.setMargin(false);
		layout.setSpacing(false);
		Label spacer = new Label("");
		spacer.setHeight("10px");
		layout.add(
				new ProjectPathNavigator.WithAddTaskButton(null),
				new OpenProjectsList(),
				spacer,
				new CompletedProjectsList()
		);
		return layout;
	}

}
