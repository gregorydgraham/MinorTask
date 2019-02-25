/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import com.vaadin.flow.component.ComponentEvent;

/**
 *
 * @author gregorygraham
 */
public class TaskCreatedEvent extends ComponentEvent<CreateTaskInline> {

	public TaskCreatedEvent(CreateTaskInline comp) {
		super(comp, false);
	}
	
}
