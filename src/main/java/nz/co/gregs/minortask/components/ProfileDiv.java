/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.components.images.SizedImageFromDocument;
import nz.co.gregs.minortask.components.polymer.PaperInput;
import nz.co.gregs.minortask.components.polymer.PaperTextArea;
import nz.co.gregs.minortask.components.upload.Document;
import nz.co.gregs.minortask.components.upload.DocumentAddedEvent;
import nz.co.gregs.minortask.components.upload.ImageUpload;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@Tag("user-profile")
@StyleSheet("styles/user-profile.css")
public class ProfileDiv extends SecureDiv {

	private final ImageUpload imageUpload = new ImageUpload();
	private final PaperInput usernameInput = new PaperInput();
	private final PaperInput emailInput = new PaperInput();
	private final PaperTextArea blurb = new PaperTextArea();
	private final H2 greeting = new H2();
	private Component imageDiv = new Div();

	public ProfileDiv() {
		super();
		setValues();
		setLabels();
		setStyles();
		addListeners();

		final Div imageStuff = new Div(imageDiv, imageUpload);
		imageStuff.setId("image-container");

		setId("user-profile-contents");
		add(
				greeting,
				imageStuff,
				usernameInput,
				emailInput,
				blurb
		);
	}

	private void addListeners() {
		imageUpload.addDocumentAddedListener((event) -> {
			profileImageUploaded(event);
		});
		usernameInput.addBlurListener((event) -> {
			saveUser();
		});
		emailInput.addBlurListener((event) -> {
			saveUser();
		});
		blurb.addBlurListener((event) -> {
			saveUser();
		});
	}

	private void setValues() {
		User user = minortask().getCurrentUser();
		if (user != null && user.getUsername()!=null) {
			greeting.setText("@" + user.getUsername() + " Profile");
			usernameInput.setValue(user.getUsername() == null ? "" : user.getUsername());
			emailInput.setValue(user.getEmail() == null ? "" : user.getEmail());
			blurb.setValue(user.getBlurb() == null ? "" : user.getBlurb());
			setProfileImage();
		}
	}

	private void setLabels() {
		usernameInput.setLabel("Username");
		emailInput.setLabel("Email");
		blurb.setLabel("Biography");
	}

	private void setProfileImage() {
		User user = getCurrentUser();
		if (user != null && user.profileImage == null && user.getProfileImageID() != null) {
			try {
				List<Document> docs = getDatabase().getDBQuery(user, new Document()).getAllInstancesOf(new Document());
				if (docs.size() == 1) {
					user.profileImage = docs.get(0);
				} else {
					chat("Couldn't find the picture");
				}
			} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
				sqlerror(ex);
			}
		}
		if (user.profileImage != null) {
			imageDiv = new SizedImageFromDocument(user.profileImage, 200d);
		}
	}

	public void saveUser() {
		System.out.println("VALUE CHANGED!");
		System.out.println("USERNAME: " + usernameInput.getValue());
		System.out.println("EMAIL: " + emailInput.getValue());
		System.out.println("BLURB: " + blurb.getValue());
		User user = getCurrentUser();
		user.setUsername(usernameInput.getValue());
		user.setEmail(emailInput.getValue());
		user.setBlurb(blurb.getValue());
		try {
			getDatabase().update(user);
			Globals.savedNotice();
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void profileImageUploaded(DocumentAddedEvent event) {
		final Document doc = event.getValue();
		User user = getCurrentUser();
		user.setProfileImageID(doc.documentID.getValue());
		user.profileImage = doc;
		try {
			getDatabase().update(user);
			Globals.savedNotice();
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void setStyles() {
		blurb.setId("user-profile-bio");
		blurb.setRows(5);
		emailInput.setId("user-profile-email");
		greeting.setId("user-profile-greeting");
		imageDiv.setId("profile-image-div");
		imageUpload.setId("user-profile-imageupload");
		usernameInput.setId("user-profile-username");
	}

	public void refresh() {
		setValues();
	}
}
