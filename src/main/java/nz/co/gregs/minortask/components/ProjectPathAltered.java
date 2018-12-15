/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class ProjectPathAltered extends ComponentEvent<Component> {

	private final Task task;

	public ProjectPathAltered(Component source, Task task, boolean fromClient) {
		super(source, fromClient);
		this.task = task;
	}

	/**
	 * @return the document
	 */
	public Task getValue() {
		return task; 
	}
}
