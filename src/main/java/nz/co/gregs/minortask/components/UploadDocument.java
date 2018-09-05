/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.shared.Registration;
import java.io.InputStream;
import java.sql.SQLException;
import nz.co.gregs.minortask.datamodel.TaskDocument;
import nz.co.gregs.minortask.events.DocumentAddedEvent;

/**
 *
 * @author gregorygraham
 */
public class UploadDocument extends HorizontalLayout implements RequiresLogin {

	MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
	Upload uploader = new Upload();
	private final Long taskID;

	public UploadDocument(Long taskID) {
		super();
		this.taskID = taskID;
		uploader.setUploadButton(new Button("Add Documents..."));
		uploader.setReceiver(buffer);
//		uploader.setAcceptedFileTypes("application/*", "text/*");
		uploader.addSucceededListener((event) -> {
			processSuccessfulUpload(event);
		});
		add(uploader);
	}

	private void processSuccessfulUpload(SucceededEvent event) {
		String fileName = event.getFileName();
		String mimeType = event.getMIMEType();
		System.out.println("fileID: " + fileName);
		TaskDocument doc = new TaskDocument();
		doc.mediaType.setValue(mimeType);
		doc.filename.setValue(fileName);
		final InputStream inputStream = buffer.getInputStream(fileName);
		doc.documentContents.setValue(inputStream);
		doc.taskID.setValue(taskID);
		doc.userID.setValue(minortask().getUserID());
		System.out.println("Document: " + doc.toString());
		try {
			getDatabase().insert(doc);
			fireEvent(new DocumentAddedEvent(this, true));
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public Registration addDocumentAddedListener(
			ComponentEventListener<DocumentAddedEvent> listener) {
		return addListener(DocumentAddedEvent.class, listener);
	}
}
