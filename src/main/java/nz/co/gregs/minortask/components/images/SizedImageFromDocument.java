/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.images;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import nz.co.gregs.minortask.components.upload.Document;


public class SizedImageFromDocument extends Image{

	public SizedImageFromDocument(Document doc) {
		this(doc, 200d);
	}

	public SizedImageFromDocument(Document doc, double size) {
		super(
				new StreamResource(
						doc.filename.getValue()==null?"image.jpg":doc.filename.getValue(),
						new SizedImageDocumentStreamFactory(doc, size)
				), 
				doc.filename.getValue()==null?"image.jpg":doc.filename.getValue("image.jpg"));
	}
	
}
