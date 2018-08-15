/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.events;

import com.vaadin.flow.component.ComponentEvent;
import nz.co.gregs.minortask.components.UploadDocument;

/**
 *
 * @author gregorygraham
 */
public class DocumentAddedEvent extends ComponentEvent<UploadDocument> {

	public DocumentAddedEvent(UploadDocument source, boolean fromClient) {
		super(source, fromClient);
	}
}
