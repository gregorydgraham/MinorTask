/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.Component;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public enum MinorTaskViews {
	TASKDETAILS,
	RECENT,
	FAVOURITES,
	CLUSTER,
	PROFILE,
	COLLEAGUES,
	SEARCH, 
	TODAY;

	public static MinorTaskEvent getEventFor(Component source, String intendedView) {
		return getEventFor(source, intendedView, null);
	}

	public static MinorTaskEvent getEventFor(Component source, String intendedView, Task task) {
		for (MinorTaskViews view : MinorTaskViews.values()) {
			if (view.name().toLowerCase().equals(intendedView.toLowerCase())){
				return new MinorTaskEvent(source, view, task, true);
			}
		}
		return new MinorTaskEvent(source, task, true);
	}
	
	public boolean equals(MinorTaskViews other){
		return this.name().toLowerCase().equals(other.name().toLowerCase());
	}
	
}
