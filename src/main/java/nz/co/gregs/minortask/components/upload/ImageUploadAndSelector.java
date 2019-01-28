/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import nz.co.gregs.minortask.components.images.ImageSelector;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.shared.Registration;
import nz.co.gregs.minortask.components.SecureDiv;

/**
 *
 * @author gregorygraham
 */
@Tag("document-upload")
public class ImageUploadAndSelector extends SecureDiv {

	protected ImageUpload uploader = new ImageUpload();
	protected ImageSelector selector = new ImageSelector();
	protected Long taskID;

	public ImageUploadAndSelector(Long taskID) {
		this.setTaskID(taskID);
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
		uploader.setTaskID(id);
		selector.setTaskID(id);
	}

	public Registration addDocumentAddedListener(
			ComponentEventListener<DocumentAddedEvent> listener) {
		return addListener(DocumentAddedEvent.class, listener);
	}

	private void handleDocumentEvent(DocumentAddedEvent event) {
		fireEvent(new DocumentAddedEvent(this, event.getValue(), false));
	}
	protected Document getDocumentExampleForSelector() {
		return selector.getDocumentExampleForSelector();
	}
}
