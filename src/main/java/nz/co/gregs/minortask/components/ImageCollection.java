/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.datamodel.Images;

/**
 *
 * @author gregorygraham
 */
public class ImageCollection extends HorizontalLayout implements RequiresLogin{

	private final Long taskID;

	public ImageCollection(Long taskID) {
		this.taskID = taskID;
		Images example = new Images();
		example.taskID.permittedValues(this.taskID);
		example.userID.permittedValues(minortask().getUserID());
		try {
			List<Images> allRows = getDatabase().getDBTable(example).getAllRows();
			for (Images imageRow : allRows) {
				Image img = new Image(new ImageStreamResource(imageRow), "Task Image");
				add(img);
			}
			add(new UploadImage(taskID));
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}
	
	public static class ImageStreamResource extends StreamResource{

		private final Images image;

		public ImageStreamResource(Images image) {
			super(image.filename.getValue(), new InputStreamFactory() {
				@Override
				public InputStream createInputStream() {
					return image.imageContents.getInputStream();
				}
			});
			this.image = image;
		}
	}
	
}
