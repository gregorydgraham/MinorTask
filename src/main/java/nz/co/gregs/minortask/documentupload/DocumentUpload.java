/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.documentupload;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.shared.Registration;
import java.io.InputStream;
import java.sql.SQLException;
import nz.co.gregs.minortask.components.RequiresLogin;

/**
 *
 * @author gregorygraham
 */
public class DocumentUpload extends Div implements RequiresLogin {

	MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
	Upload uploader = new Upload();
	protected Long taskID;

	public DocumentUpload(Long taskID) {
		this();
		this.taskID = taskID;
	}

	public DocumentUpload() {
		uploader.setUploadButton(new Button("Add Documents..."));
		uploader.setReceiver(buffer);
		uploader.addSucceededListener((event) -> {
			processSuccessfulUpload(event);
		});
		add(uploader);
	}
	
	public final void setTaskID(Long id){
		this.taskID = id;
	}

	protected final void processSuccessfulUpload(SucceededEvent event) {
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
