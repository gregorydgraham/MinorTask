/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import com.vaadin.flow.server.StreamResource;

/**
 *
 * @author gregorygraham
 */
public class DocumentIconStreamResource extends StreamResource {
	
	public DocumentIconStreamResource(Document doc) {
		super(doc.filename.getValue(), new ThumbnailInputStreamFactory(doc));
	}	
}
