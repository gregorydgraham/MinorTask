/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.imageprocessing;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import java.io.InputStream;
import nz.co.gregs.minortask.datamodel.Images;

/**
 *
 * @author gregorygraham
 */
public class ImageStreamResource extends StreamResource {
	
	private final Images originalImage;

	public ImageStreamResource(Images image) {
		super(image.filename.getValue(), new ImageInputStreamFactory(image));
		originalImage = image;
	}

	public Images getOriginalImageRow() {
		return originalImage;
	}

	public static class ImageInputStreamFactory implements InputStreamFactory {

		private final Images image;

		public ImageInputStreamFactory(Images image) {
			this.image = image;
		}

		@Override
		public InputStream createInputStream() {
			return image.imageContents.getInputStream();
		}
	}
	
}
