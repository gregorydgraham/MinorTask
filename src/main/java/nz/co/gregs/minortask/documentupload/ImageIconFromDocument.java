/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.documentupload;

import com.vaadin.flow.component.html.Image;

/**
 *
 * @author gregorygraham
 */
public class ImageIconFromDocument extends Image{
	
	public ImageIconFromDocument(Document doc){
		super(new DocumentIconStreamResource(doc), doc.filename.getValue());
	}
	
}
