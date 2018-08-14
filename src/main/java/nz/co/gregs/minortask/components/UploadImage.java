/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.shared.Registration;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.minortask.datamodel.Images;
import nz.co.gregs.minortask.events.ImageAddedEvent;

/**
 *
 * @author gregorygraham
 */
public class UploadImage extends HorizontalLayout implements RequiresLogin {

	MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
	Upload uploadButton = new Upload();
	private final Long taskID;

	public UploadImage(Long taskID) {
		super();
		this.taskID = taskID;
		uploadButton.setReceiver(buffer);
		uploadButton.setAcceptedFileTypes("image/*");
		uploadButton.addSucceededListener((event) -> {
			processSuccessfulUpload(event);
		});
		add(uploadButton);
	}

	private void processSuccessfulUpload(SucceededEvent event) {
		String fileName = event.getFileName();
		String mimeType = event.getMIMEType();
		System.out.println("fileID: " + fileName);
		Images image = new Images();
		image.mediaType.setValue(mimeType);
		image.filename.setValue(fileName);
		final InputStream inputStream = buffer.getInputStream(fileName);
		image.imageContents.setValue(inputStream);
		image.taskID.setValue(taskID);
		image.userID.setValue(minortask().getUserID());
		System.out.println("Image: " + image.toString());
		try {
			getDatabase().insert(image);
			fireEvent(new ImageAddedEvent(this, true));
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public Registration addImageAddedListener(
			ComponentEventListener<ImageAddedEvent> listener) {
		return addListener(ImageAddedEvent.class, listener);
	}
}
