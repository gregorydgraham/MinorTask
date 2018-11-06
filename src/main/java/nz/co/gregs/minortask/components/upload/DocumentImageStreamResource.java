/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author gregorygraham
 */
public class DocumentImageStreamResource extends StreamResource {
	
	public DocumentImageStreamResource(Document doc) {
		super(doc.filename.getValue(), new ImageInputStreamFactory(doc));
	}

	public static class ImageInputStreamFactory implements InputStreamFactory {

		private final Document doc;

		public ImageInputStreamFactory(Document doc) {
			this.doc = doc;
		}

		@Override
		public InputStream createInputStream() {
			try {
				BufferedImage thumbnail = createThumbnailFromOriginalRow();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(thumbnail, "png", os);
				InputStream fis = new ByteArrayInputStream(os.toByteArray());
				return fis;
			} catch (IOException ex) {
				Logger.getLogger(DocumentImageStreamResource.class.getName()).log(Level.SEVERE, null, ex);
			}
			return new ByteArrayInputStream(new byte[]{});
		}

		private BufferedImage createThumbnailFromOriginalRow() throws IOException {
			final InputStream inputStream = doc.documentContents.getInputStream();
			BufferedImage originalImage = ImageIO.read(inputStream);
			return originalImage;
		}
	}
	
}
