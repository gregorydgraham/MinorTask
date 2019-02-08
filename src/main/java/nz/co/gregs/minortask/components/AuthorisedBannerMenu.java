/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.images.SizedImageFromDocument;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.pages.UserProfilePage;

/**
 *
 * @author gregorygraham
 */
@Tag("authorised-banner")
@StyleSheet("styles/authorised-banner.css")
public class AuthorisedBannerMenu extends SecureDiv implements HasText {

	final Anchor welcomeMessage = new Anchor(Globals.getApplicationURL(), "Welcome");
	ColleaguesButton colleaguesButton = new ColleaguesButton();
	ProfileButton profileButton = new ProfileButton();
	LogoutButton logoutButton = new LogoutButton();

	public AuthorisedBannerMenu() {
		super();
		buildComponent();
		this.addClassName("authorised-banner");
		this.setId(getStaticID());
	}

	@Override
	public final void setId(String id) {
		super.setId(id);
	}

	public final void buildComponent() {
		setSizeUndefined();

		welcomeMessage.addClassName("welcome-message");

		setText("" + Globals.getApplicationName());

		final User user = getCurrentUser();
		if (user != null) {
			SecureDiv defaultImageDiv = new SecureDiv();
			defaultImageDiv.addClickListener((event) -> {
				minortask().showProfile();
			});
			Component profileImageDiv = defaultImageDiv;

			if (user.profileImage != null) {
				SizedImageFromDocument image = new SizedImageFromDocument(user.profileImage, 100);
				image.addClickListener((event) -> {
					minortask().showProfile();
				});
				profileImageDiv = image;
			}

			profileImageDiv.setId("authorised-banner-profile-image");

			final String welcomeUser = "@" + user.getUsername();
			Anchor profileAnchor = new Anchor(UserProfilePage.getURL(), welcomeUser);

			colleaguesButton.setId("authorised-banner-colleagues-button");
			colleaguesButton.addClassName("authorised-banner-button");

			profileButton.setId("authorised-banner-profile-button");
			profileButton.addClassName("authorised-banner-button");

			logoutButton.setId("authorised-banner-logout-button");
			logoutButton.addClassName("authorised-banner-button");

			Div left = new Div();
			left.addClassName("authorised-banner-left");

			Div right = new Div();
			right.addClassName("authorised-banner-right");

			left.add(profileImageDiv, welcomeMessage);
			right.add(profileAnchor, colleaguesButton, profileButton, logoutButton);
			add(left, right);
		}
	}

	public static String getStaticID() {
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
