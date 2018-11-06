/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

/**
 *
 * @author gregorygraham
 */
public class DocumentAddedEvent extends ComponentEvent<Component> {

	private final Document document;

	public DocumentAddedEvent(Component source, Document doc, boolean fromClient) {
		super(source, fromClient);
		this.document = doc;
	}

	/**
	 * @return the document
	 */
	public Document getValue() {
		return document;
	}
}
