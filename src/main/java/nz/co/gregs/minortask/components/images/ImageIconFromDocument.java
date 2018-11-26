/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.images;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import nz.co.gregs.minortask.components.upload.Document;

/**
 *
 * @author gregorygraham
 */
public class ImageIconFromDocument extends Image {

	public ImageIconFromDocument(Document doc) {
		super(
				new StreamResource(
						doc.filename.getValue()==null?"image.jpg":doc.filename.getValue("image.jpg"),
						new ThumbnailImageDocumentStreamFactory(doc)
				),
				doc.filename.getValue()==null?"image.jpg":doc.filename.getValue("image.jpg"));
	}

}
