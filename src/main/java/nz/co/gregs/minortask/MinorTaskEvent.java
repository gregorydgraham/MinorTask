/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class MinorTaskEvent extends ComponentEvent<Component> {
	
	private final MinorTaskViews view;
	private final Task task;
	
	public MinorTaskEvent(Component source, MinorTaskViews viewRequested, boolean fromClient) {
		super(source, fromClient);
		this.view = viewRequested;
		this.task = null;
	}
	
	public MinorTaskEvent(Component source, MinorTaskViews viewRequested, Task task, boolean fromClient) {
		super(source, fromClient);
		this.view = viewRequested;
		this.task = task;
	}
	
	public MinorTaskEvent(Component source, Task task, boolean fromClient) {
		super(source, fromClient);
		this.view = MinorTaskViews.TASKDETAILS;
		this.task = task;
	}
	
	public MinorTaskEvent(Component source, boolean fromClient) {
		super(source, fromClient);
		this.view = MinorTaskViews.TASKDETAILS;
		this.task = null;
	}

	public MinorTaskViews getView() {
		return view;
	}

	public Task getTask() {
		return task;
	}
}
