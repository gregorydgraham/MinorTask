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
public class ImageFromDocument extends Image{
	
	public ImageFromDocument(Document doc){
		super(new DocumentImageStreamResource(doc), doc.filename.getValue());
	}
	
}
