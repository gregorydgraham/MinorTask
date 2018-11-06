/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.EventHandler;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.minortask.Globals;
import static nz.co.gregs.minortask.Globals.*;
import static nz.co.gregs.minortask.Globals.getDatabase;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.polymer.PaperInput;
import nz.co.gregs.minortask.components.polymer.PaperTextArea;
import nz.co.gregs.minortask.components.upload.Document;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.components.upload.DocumentAddedEvent;
import nz.co.gregs.minortask.components.upload.ImageFromDocument;
import nz.co.gregs.minortask.components.upload.ImageUpload;

/**
 *
 * @author gregorygraham
 */
@Route("profile")
@Tag("user-profile")
@HtmlImport("frontend://src/user-profile.html")
public class UserProfilePage extends PolymerTemplate<UserProfilePage.UserModel> implements BeforeEnterObserver {

	private User user = null;
	@Id("image-uploader")
	private ImageUpload imageUpload;
	@Id("profile-image")
	private ImageFromDocument profileImage;
	@Id("username")
	private PaperInput usernameDiv;
	@Id("email")
	private PaperInput emailInput;
	@Id("biography")
	private PaperTextArea blurb;
//	@Id("user")
//	private Div userDiv;

	MinorTask minortask = MinorTask.getMinorTask();

	public UserProfilePage() {
		super();
		if (minortask.isLoggedIn()) {
			System.out.println("IS LOGGED IN ID: " + minortask.getUserID());
			user = minortask.getUser();
			System.out.println("IS LOGGED IN USER: " + user);
			System.out.println("USERNAME: " + user.getUsername());
			getModel().setUsername(user.getUsername());
			getModel().setEmail(user.getEmail());
			getElement().addSynchronizedProperty("name");
			getElement().addSynchronizedProperty("email");
			getElement().addSynchronizedProperty("biography");
			if (user.profileImage != null) {
				profileImage.setSrc(user.profileImage);
//				getModel().setProfileImage(user.profileImage);
			} else {
				if (user.getProfileImageID() != null) {
					try {
						List<Document> docs = getDatabase().getDBQuery(user, new Document()).getAllInstancesOf(new Document());
						if (docs.size() == 1) {
							user.profileImage = docs.get(0);
							profileImage.setSrc(user.profileImage);
						}else{chat("Couldn't find the picture");}
					} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
						sqlerror(ex);
					}
				} else {
					chat("image not provided");
				}
			}
			imageUpload.addDocumentAddedListener((event) -> {
				profileImageUploaded(event);
			});
		} else {
			chat("Not Logged In!");
		}
		getModel().setGreeting("Welcome to the User Profile page.");
	}

	@Override
	public final void beforeEnter(BeforeEnterEvent event) {
		System.out.println("BEFORE ENTER MINORTASKPAGE");
		if (!minortask.isLoggedIn()) {
			showLogin();
		}
	}

	@EventHandler
	public void returnToProjects() {
		MinorTask.showProjects();
	}

	@EventHandler
	public void saveUser() {
		System.out.println("VALUE CHANGED!");
		System.out.println("USERNAME: " + getModel().getUsername());
		System.out.println("EMAIL: " + getModel().getEmail());
		System.out.println("BLURB: " + getModel().getBiography());
		MinorTask.chat("USERNAME: " + getModel().getUsername() + " EMAIL: " + getModel().getEmail());
		user.setUsername(getModel().getUsername());
		user.setEmail(getModel().getEmail());
		user.setBlurb(getModel().getBiography());
		try {
			getDatabase().update(user);
			savedNotice();
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void profileImageUploaded(DocumentAddedEvent event) {
		final Document doc = event.getValue();
		profileImage.setSrc(doc);
		user.setProfileImageID(doc.documentID.getValue());
		user.profileImage = doc;
		try {
			getDatabase().update(user);
			savedNotice();
			profileImage = new ImageFromDocument(doc);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public static interface UserModel extends TemplateModel {

		public String getUsername();

		public void setUsername(String newValue);

		public String getEmail();

		public void setEmail(String newValue);

		public void setGreeting(String newValue);

		public String getBiography();

//		public void setProfileImageID(Long id);
//		public Long getProfileImage();
	}
}
