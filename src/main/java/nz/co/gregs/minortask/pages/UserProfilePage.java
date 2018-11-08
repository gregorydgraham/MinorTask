/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.polymertemplate.EventHandler;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import static nz.co.gregs.minortask.Globals.*;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.polymer.PaperInput;
import nz.co.gregs.minortask.components.polymer.PaperTextArea;
import nz.co.gregs.minortask.components.upload.Document;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.components.upload.DocumentAddedEvent;
import nz.co.gregs.minortask.components.upload.ImageUpload;

/**
 *
 * @author gregorygraham
 */
@Route("profile")
@Tag("user-profile")
@StyleSheet("styles/user-profile-page.css")
public class UserProfilePage extends AuthorisedPage {

	private final ImageUpload imageUpload = new ImageUpload();
	private final PaperInput usernameDiv = new PaperInput();
	private final PaperInput emailInput = new PaperInput();
	private final PaperTextArea blurb = new PaperTextArea();
	private final H2 greeting = new H2();
	private final Div imageDiv = new Div();

	public UserProfilePage() {
		super();
	}

	@Override
	protected Component getInternalComponent() {
		setValues();
		setStyles();
		addListeners();

		final Div imageStuff = new Div(imageDiv, imageUpload);
		imageStuff.setId("image-container");

		Div component = new Div();
		component.setId("user-profile-contents");
		component.add(greeting, imageStuff, usernameDiv, emailInput, blurb);

		return component;
	}

	@Override
	public String getPageTitle() {
		if (minortask().isLoggedIn()) {
			return "Profile: @" + getUser().getUsername();
		}
		return "Profile Page";
	}

	private void addListeners() {
		imageUpload.addDocumentAddedListener((event) -> {
			profileImageUploaded(event);
		});
		usernameDiv.addBlurListener((event) -> {
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
		User user = minortask().getUser();
		usernameDiv.setValue(user.getUsername() == null ? "" : user.getUsername());
		emailInput.setValue(user.getEmail() == null ? "" : user.getEmail());
		blurb.setValue(user.getBlurb() == null ? "" : user.getBlurb());
		greeting.setText("Welcome to the User Profile page.");
		minortask().setBackgroundToImage(imageDiv, user.profileImage);
		setProfileImage();
	}

	private void setProfileImage() {
		User user = getUser();
		if (user.profileImage != null) {
			minortask().setBackgroundToImage(imageDiv, user.profileImage);
		} else {
			if (user.getProfileImageID() != null) {
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
			} else {
				chat("image not provided");
			}
		}
	}

	@EventHandler
	public void returnToProjects() {
		MinorTask.showProjects();
	}

	@EventHandler
	public void saveUser() {
		System.out.println("VALUE CHANGED!");
		System.out.println("USERNAME: " + usernameDiv.getValue());
		System.out.println("EMAIL: " + emailInput.getValue());
		System.out.println("BLURB: " + blurb.getValue());
		MinorTask.chat("USERNAME: " + usernameDiv.getValue() + " EMAIL: " + emailInput.getValue());
		User user = getUser();
		user.setUsername(usernameDiv.getValue());
		user.setEmail(emailInput.getValue());
		user.setBlurb(blurb.getValue());
		try {
			getDatabase().update(user);
			savedNotice();
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void profileImageUploaded(DocumentAddedEvent event) {
		final Document doc = event.getValue();
		User user = getUser();
		user.setProfileImageID(doc.documentID.getValue());
		user.profileImage = doc;
		try {
			getDatabase().update(user);
			savedNotice();
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void setStyles() {
		blurb.setId("user-profile-bio");
		emailInput.setId("user-profile-email");
		greeting.setId("user-profile-greeting");
		imageDiv.setId("profile-image-div");
		imageUpload.setId("user-profile-imageupload");
		usernameDiv.setId("user-profile-username");

	}

	public static interface UserModel extends TemplateModel {

		public String getUsername();

		public void setUsername(String newValue);

		public String getEmail();

		public void setEmail(String newValue);

		public void setGreeting(String newValue);

		public String getBiography();
	}
}
