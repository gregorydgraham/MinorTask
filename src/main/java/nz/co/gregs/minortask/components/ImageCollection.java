/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.datamodel.Images;

/**
 *
 * @author gregorygraham
 */
public class ImageCollection extends HorizontalLayout implements RequiresLogin {

	private final Long taskID;

	public ImageCollection(Long taskID) {
		this.taskID = taskID;
		makeComponent();
	}

	private void makeComponent() {
		removeAll();
		Images example = new Images();
		example.taskID.permittedValues(this.taskID);
		example.userID.permittedValues(minortask().getUserID());
		try {
			List<Images> allRows = getDatabase().getDBTable(example).getAllRows();
			for (Images imageRow : allRows) {
				ImageControl img = new ImageControl(taskID, imageRow);
				add(img);
				img.addImageDeletedListener((event) -> {
					removeImage(img);
				});
				img.addImageChangedListener((event) -> {
					makeComponent();
				});
			}
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		final UploadImage uploader = new UploadImage(taskID);
		add(uploader);
		uploader.addImageAddedListener((event) -> {
			makeComponent();
		});
	}

	private void removeImage(ImageControl img) {
		this.remove(img);
	}
}
