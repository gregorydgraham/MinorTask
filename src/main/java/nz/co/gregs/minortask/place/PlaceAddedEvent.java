/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.place;

import com.vaadin.flow.component.ComponentEvent;

/**
 *
 * @author gregorygraham
 */
public class PlaceAddedEvent extends ComponentEvent<OpenStreetMapPlaceGrid> {

	public PlaceAddedEvent(OpenStreetMapPlaceGrid source, boolean fromClient) {
		super(source, fromClient);
	}
}
