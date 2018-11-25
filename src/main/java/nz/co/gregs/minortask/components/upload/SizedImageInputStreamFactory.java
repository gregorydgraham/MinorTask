/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

/**
 *
 * @author gregorygraham
 */
public class SizedImageInputStreamFactory extends ThumbnailInputStreamFactory{
	
	public SizedImageInputStreamFactory(Document doc, double targetSize) {
		super(doc, targetSize);
	}
	
	public SizedImageInputStreamFactory(Document doc) {
		super(doc, 200d);
	}	
}
