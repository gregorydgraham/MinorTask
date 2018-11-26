/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.images;

import nz.co.gregs.minortask.components.upload.Document;

/**
 *
 * @author gregorygraham
 */
public class SizedImageDocumentStreamFactory extends ThumbnailImageDocumentStreamFactory{
	
	public SizedImageDocumentStreamFactory(Document doc, double targetSize) {
		super(doc, targetSize);
	}
	
	public SizedImageDocumentStreamFactory(Document doc) {
		super(doc, 200d);
	}	
}
