/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.documentupload;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.shared.Registration;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.minortask.components.RequiresLogin;

/**
 *
 * @author gregorygraham
 */
@Tag("document-upload")
public class DocumentUpload extends Div implements RequiresLogin {

	MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
	Upload uploader = new Upload();
	Button addExistingDoc = new Button("Attach Existing ...");
	ComboBox<Document> existingDocSelector = new ComboBox<>();
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

		addExistingDoc.addClickListener((event) -> {
			showSelector(event);
		});
		add(addExistingDoc);

		existingDocSelector.setItemLabelGenerator((Document item) -> item.filename+": "+item.description);
		existingDocSelector.getStyle().set("display", "none");
		existingDocSelector.addValueChangeListener((event) -> {
			addSelectedItem(event);
		});
		add(existingDocSelector);
	}

	public final void setTaskID(Long id) {
		this.taskID = id;
	}

	protected final void processSuccessfulUpload(SucceededEvent event) {
		String fileName = event.getFileName();
		String mimeType = event.getMIMEType();
		System.out.println("fileID: " + fileName);
		Document doc = new Document();
		doc.mediaType.setValue(mimeType);
		doc.filename.setValue(fileName);
		final InputStream inputStream = buffer.getInputStream(fileName);
		doc.documentContents.setValue(inputStream);
		doc.taskID.setValue(taskID);
		doc.userID.setValue(minortask().getUserID());
		System.out.println("Document: " + doc.toString());
		try {
			getDatabase().insert(doc);
			insertLinkToDocument(doc);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public Registration addDocumentAddedListener(
			ComponentEventListener<DocumentAddedEvent> listener) {
		return addListener(DocumentAddedEvent.class, listener);
	}

	private void showSelector(ClickEvent<Button> event) {
		existingDocSelector.clear();
		Document docExample = getDocumentExampleForSelector();
		TaskDocumentLink linkExample = new TaskDocumentLink();
		linkExample.taskID.permittedValues(taskID);
		try {
			final DBQuery query = getDatabase().getDBQuery(docExample).addOptional(linkExample);
			query.addCondition(linkExample.column(linkExample.taskDocumentLinkID).isNull());
			List<Document> instances = query.getAllInstancesOf(docExample);
			getDatabase().print(instances);
			existingDocSelector.setItems(instances);
			existingDocSelector.getStyle().set("display", "inline-block");
		} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
			Logger.getLogger(DocumentUpload.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	protected Document getDocumentExampleForSelector() {
		Document docExample = new Document();
		docExample.userID.permittedValues(getUserID());
		docExample.mediaType.excludedPattern("image/%");
		return docExample;
	}

	private void addSelectedItem(AbstractField.ComponentValueChangeEvent<ComboBox<Document>, Document> event) {
		Document doc = event.getValue();
		insertLinkToDocument(doc);
	}

	private void insertLinkToDocument(Document doc) {
		if (doc != null) {
			TaskDocumentLink link = new TaskDocumentLink();
			link.documentID.setValue(doc.documentID);
			link.taskID.setValue(taskID);
			link.ownerID.setValue(getUserID());
			try {
				getDatabase().insert(link);
			} catch (SQLException ex) {
				sqlerror(ex);
			}
			existingDocSelector.clear();
			existingDocSelector.getStyle().set("display", "none");
			fireEvent(new DocumentAddedEvent(this, true));
		}
	}
}
