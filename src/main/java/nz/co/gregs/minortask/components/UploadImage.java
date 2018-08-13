/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.datamodel.Images;

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
		List<Images> images = new ArrayList<>();
		for (String fileID : buffer.getFiles()) {
			System.out.println("fileID: "+fileID);
			FileData fileData = buffer.getFileData(fileID);
			Images image = new Images();
			image.mediaType.setValue(fileData.getMimeType());
			image.filename.setValue(fileData.getFileName());
			final InputStream inputStream = buffer.getInputStream(fileData.getFileName());
//			try {
//				System.out.println("AVAILABLE: "+inputStream.available());
//			} catch (IOException ex) {
//				Logger.getLogger(UploadImage.class.getName()).log(Level.SEVERE, null, ex);
//			}
			image.imageContents.setValue(inputStream);
			image.taskID.setValue(taskID);
			image.userID.setValue(minortask().getUserID());
			System.out.println("Image: "+image.toString());
			images.add(image);
		}
		try {
			getDatabase().insert(images);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}
}
