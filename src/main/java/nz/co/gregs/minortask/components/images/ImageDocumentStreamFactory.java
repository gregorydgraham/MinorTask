/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.images;

import com.vaadin.flow.server.InputStreamFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import nz.co.gregs.minortask.components.upload.Document;

/**
 *
 * @author gregorygraham
 */
public class ImageDocumentStreamFactory implements InputStreamFactory {

	private final Document doc;

	public ImageDocumentStreamFactory(Document doc) {
		this.doc = doc;
	}

	public byte[] getByteArray() throws IOException {
		final InputStream inputStream = createInputStream();
		BufferedImage originalImage = ImageIO.read(inputStream);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		boolean wrote = ImageIO.write(originalImage, "png", outputStream);
		return outputStream.toByteArray();
	}

	@Override
	public InputStream createInputStream() {
		try {
			BufferedImage thumbnail = createImageFromOriginalRow();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(thumbnail, "png", os);
			InputStream fis = new ByteArrayInputStream(os.toByteArray());
			return fis;
		} catch (IOException ex) {
			Logger.getLogger(ImageDocumentStreamFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new ByteArrayInputStream(new byte[]{});
	}

	protected BufferedImage createImageFromOriginalRow() throws IOException {
		if (doc.documentContents.isNotNull()) {
			final InputStream inputStream = doc.documentContents.getInputStream();
			BufferedImage originalImage = ImageIO.read(inputStream);
			BufferedImage thumbnail = transformImage(originalImage);
			return thumbnail;
		} else {
			return transformImage(ImageIO.read(getClass().getClassLoader().getResourceAsStream("logo.png")));
		}
	}

	protected BufferedImage transformImage(BufferedImage original) {
		return original;
	}
}
