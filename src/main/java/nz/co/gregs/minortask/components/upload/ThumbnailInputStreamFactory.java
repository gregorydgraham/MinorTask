/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import com.vaadin.flow.server.InputStreamFactory;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author gregorygraham
 */
public class ThumbnailInputStreamFactory implements InputStreamFactory {
	
	private final Document doc;

	public ThumbnailInputStreamFactory(Document doc) {
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
			Logger.getLogger(DocumentIconStreamResource.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new ByteArrayInputStream(new byte[]{});
	}

	public BufferedImage createThumbnailFromOriginalRow() throws IOException {
		final InputStream inputStream = doc.documentContents.getInputStream();
		BufferedImage originalImage = ImageIO.read(inputStream);
		BufferedImage thumbnail = createThumbnail(originalImage);
		return thumbnail;
	}
	
	public byte[] getByteArray() throws IOException{
		final InputStream inputStream = doc.documentContents.getInputStream();
		BufferedImage originalImage = ImageIO.read(inputStream);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		boolean wrote = ImageIO.write(originalImage, "png", outputStream);
		return outputStream.toByteArray();
	}

	private BufferedImage createThumbnail(BufferedImage original) {
		final double targetSize = 50d;
		double scale = Math.min(targetSize / original.getWidth(), targetSize / original.getHeight());
		final int width = (int) ((0d + original.getWidth()) * scale);
		final int height = (int) ((0d + original.getHeight()) * scale);
		BufferedImage thumbnail = new BufferedImage(width, height, original.getType());
		Graphics2D g = thumbnail.createGraphics();
		g.drawImage(original, 0, 0, width, height, null);
		g.dispose();
		return thumbnail;
	}
	
}
