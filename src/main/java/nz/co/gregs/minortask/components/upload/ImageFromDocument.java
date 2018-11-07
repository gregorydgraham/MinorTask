/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import com.vaadin.flow.component.html.Image;

/**
 *
 * @author gregorygraham
 */
public class ImageFromDocument extends Image { 

	private boolean userOriginalSize = true;
	private long maxWidth;

	public ImageFromDocument(Document doc) {
		super(new DocumentImageStreamResource(doc), doc.filename.getValue());
	}

	public ImageFromDocument(Document doc, long maxWidth) {
		super(new DocumentImageStreamResource(doc, maxWidth), doc.filename.getValue());
		userOriginalSize = false;
		this.maxWidth = maxWidth;
	}

	public ImageFromDocument() {
		super();
	}

	public ImageFromDocument(long maxWidth) {
		super();
		userOriginalSize = false;
		this.maxWidth = maxWidth;
	}

	public void setSrc(Document doc) {
		if (doc != null) {
			if (userOriginalSize) {
				super.setSrc(new DocumentImageStreamResource(doc));
			} else {
				super.setSrc(new DocumentImageStreamResource(doc, maxWidth));
			}
			setAlt(doc.filename.getValue());
		} else {
			setAlt("No Image");
		}
	}

}
