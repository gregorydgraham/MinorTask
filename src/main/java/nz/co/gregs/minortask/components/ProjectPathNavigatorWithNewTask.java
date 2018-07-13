/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.button.Button;


public class ProjectPathNavigatorWithNewTask extends ProjectPathNavigator {

	public ProjectPathNavigatorWithNewTask(Long taskID) {
		super(taskID);
	}

	@Override
	protected void buildComponent() {
		super.buildComponent();
		
		final Button newTaskButton = new Button("New Task...");
		formatButton(newTaskButton);
//		newTaskButton.setEnabled(false);
		add(newTaskButton);
	}
	
}
