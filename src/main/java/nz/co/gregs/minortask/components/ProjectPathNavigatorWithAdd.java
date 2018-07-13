/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;


public class ProjectPathNavigatorWithAdd extends ProjectPathNavigator {

	public ProjectPathNavigatorWithAdd(Long taskID) {
		super(taskID);
	}

	@Override
	protected void buildComponent() {
		super.buildComponent();
		
		final AddTaskButton addTaskButton = new AddTaskButton(getTaskID());
		addTaskButton.addClassNames("small", "projectpath");
		addTaskButton.getElement().setAttribute("theme", "small");
		add(addTaskButton);
	}
	
	
	
}
