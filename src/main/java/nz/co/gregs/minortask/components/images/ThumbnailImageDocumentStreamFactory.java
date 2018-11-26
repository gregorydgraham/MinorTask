/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.images;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import nz.co.gregs.minortask.components.upload.Document;

/**
 *
 * @author gregorygraham
 */
public class ThumbnailImageDocumentStreamFactory extends ImageDocumentStreamFactory {//implements InputStreamFactory {

	private double targetSize = 50d;

	public ThumbnailImageDocumentStreamFactory(Document doc) {
		super(doc);
	}
	
	public ThumbnailImageDocumentStreamFactory(Document doc, double targetSize) {
		super(doc);
		setTargetSize(targetSize);
	}
	
	public final void setTargetSize(double targetSize){
		this.targetSize = targetSize;
	}

	@Override
	protected BufferedImage transformImage(BufferedImage original) {
		double scale = Math.min(targetSize / original.getWidth(), targetSize / original.getHeight());
		final int width = (int) ((0d + original.getWidth()) * scale);
		final int height = (int) ((0d + original.getHeight()) * scale);
		BufferedImage after = new BufferedImage(width, height, original.getType());
		AffineTransform at = new AffineTransform();
		System.out.println("SCALE: "+(targetSize)+" / "+(original.getWidth())+", "+ targetSize+" / "+original.getHeight());
		System.out.println("SCALE: "+scale);
		at.scale(scale,scale);
		AffineTransformOp scaleOp
				= new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(original, after);
		return after;
	}

}
