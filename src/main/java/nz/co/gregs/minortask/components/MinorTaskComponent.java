/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import nz.co.gregs.minortask.MinorTask;


public class MinorTaskComponent extends PublicComponent {

	private final Long taskID;

	public MinorTaskComponent(MinorTask minortask, Long taskID) {
		super(minortask);
		this.taskID = taskID;
	}

	/**
	 * @return the taskID
	 */
	public Long getTaskID() {
		return taskID;
	}
	
}
