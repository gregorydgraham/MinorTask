/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.ComponentEventListener;

/**
 *
 * @author gregorygraham
 */
public interface MinorTaskEventListener extends ComponentEventListener<MinorTaskEvent>{

	@Override
	default void onComponentEvent(MinorTaskEvent event) {
		handleMinorTaskEvent(event);
	}
	public void handleMinorTaskEvent(MinorTaskEvent event);
	
}
