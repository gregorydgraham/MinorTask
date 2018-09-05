/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.events;

import com.vaadin.flow.component.ComponentEvent;
import nz.co.gregs.minortask.components.LocationSearchComponent;

/**
 *
 * @author gregorygraham
 */
public class LocationAddedEvent extends ComponentEvent<LocationSearchComponent> {

	public LocationAddedEvent(LocationSearchComponent source, boolean fromClient) {
		super(source, fromClient);
	}
}
