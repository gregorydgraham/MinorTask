/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.documentupload;

import com.vaadin.flow.component.button.Button;
import nz.co.gregs.minortask.components.RequiresLogin;

/**
 *
 * @author gregorygraham
 */
public class ImageUpload extends DocumentUpload implements RequiresLogin {

	public ImageUpload(Long taskID) {
		this();
		setTaskID(taskID);
	}

	public ImageUpload() {
		super();
		uploader.setUploadButton(new Button("Add Images..."));
		uploader.setAcceptedFileTypes("image/*");
	}

	@Override
	protected Document getDocumentExampleForSelector() {
		Document docExample = new Document();
		docExample.userID.permittedValues(getUserID());
		docExample.mediaType.permittedPattern("image/%");
		return docExample;
	}
}
