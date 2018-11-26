/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.images.ImageIconFromDocument;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@Tag("authorised-banner")
@StyleSheet("styles/authorised-banner.css")
public class AuthorisedBannerMenu extends SecureDiv implements HasText {

	final Anchor welcomeMessage = new Anchor(Globals.getApplicationURL(), "Welcome");
	Button profileButton = new Button();
	Button logoutButton = new Button();

	public AuthorisedBannerMenu() {
		super();
		buildComponent();
		this.addClassName("authorised-banner");
		this.setId(getStaticID());
	}

	public final void buildComponent() {
		setSizeUndefined();

		welcomeMessage.addClassName("welcome-message");

		setText("Welcome to " + Globals.getApplicationName());

		User user = minortask().getUser();
		Component profileImageDiv = new Div();
		if (user.profileImage != null) {
			profileImageDiv = new ImageIconFromDocument(user.profileImage);
		}
		profileImageDiv.setId("authorised-banner-profile-image");
		final String welcomeUser = "Welcome to " + Globals.getApplicationName() + " @" + user.getUsername();
		setText(welcomeUser);

		Icon userIcon = new Icon(VaadinIcon.USER);
		Icon unlock = new Icon(VaadinIcon.UNLOCK);

		profileButton.setIcon(userIcon);
		profileButton.setId("authorised-banner-profile-button");
		profileButton.addClassName("authorised-banner-button");
		profileButton.addClickListener((event) -> {
			minortask().showProfile();
		});

		logoutButton.setIcon(unlock);
		logoutButton.setId("authorised-banner-logout-button");
		profileButton.addClassName("authorised-banner-button");
		logoutButton.addClickListener((event) -> {
			minortask().logout();
		});

		Div left = new Div();
		left.addClassName("authorised-banner-left");

		Div right = new Div();
		right.addClassName("authorised-banner-right");

		left.add(profileImageDiv, welcomeMessage);
		right.add(logoutButton, profileButton);
		add(left, right);
	}

	static String getStaticID() {
		return "authorised_banner_id";
	}

	@Override
	public String getText() {
		return welcomeMessage.getText();
	}

	@Override
	public void setText(String text) {
		welcomeMessage.setText(text);
	}

	public void setAllButtonsUnselected() {
		profileButton.removeClassName("authorised-banner-selected-button");
		logoutButton.removeClassName("authorised-banner-selected-button");
	}

	public void setProfileButtonSelected() {
		profileButton.addClassName("authorised-banner-selected-button");
	}

	public void setLogoutButtonSelected() {
		logoutButton.addClassName("authorised-banner-selected-button");
	}

}
