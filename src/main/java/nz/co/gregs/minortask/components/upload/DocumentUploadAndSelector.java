/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.shared.Registration;
import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@Tag("document-upload")
public class DocumentUploadAndSelector extends SecureDiv {

	protected DocumentUpload uploader = new DocumentUpload();
	protected DocumentSelector selector = new DocumentSelector();
	protected Long taskID;

	public DocumentUploadAndSelector(Long taskID) {
		this();
		this.taskID = taskID;
	}

	public DocumentUploadAndSelector() {
		uploader.addDocumentAddedListener((event) -> {
			handleDocumentEvent(event);
		});
		selector.addDocumentAddedListener((event) -> {
			handleDocumentEvent(event);
		});
		add(uploader);
		add(selector);
	}

	public final void setTaskID(Long id) {
		this.taskID = id;
	}

	public final void setTask(Task task) {
		this.taskID = task!=null?task.taskID.getValue():null;
	}

	public Registration addDocumentAddedListener(
			ComponentEventListener<DocumentAddedEvent> listener) {
		return addListener(DocumentAddedEvent.class, listener);
	}

	private void handleDocumentEvent(DocumentAddedEvent event) {
		fireEvent(event);
	}
	protected Document getDocumentExampleForSelector() {
		return selector.getDocumentExampleForSelector();
	}
}
