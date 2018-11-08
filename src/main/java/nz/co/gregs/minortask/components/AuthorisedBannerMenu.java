/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@Tag("authorised-banner")
@StyleSheet("styles/authorised-banner.css")
public class AuthorisedBannerMenu extends Div implements RequiresLogin, HasText {

	final Anchor welcomeMessage = new Anchor(Globals.getApplicationURL(), "Welcome");

	public AuthorisedBannerMenu() {
		if (minortask().isLoggedIn()) {
			buildComponent();
		} else {
			add(new AccessDeniedComponent());
		}
		this.addClassName("authorised-banner");
	}

	public final void buildComponent() {
		setSizeUndefined();

		welcomeMessage.addClassName("welcome-message");

		setText("Welcome to " + Globals.getApplicationName());

		User user = minortask().getUser();
		Div profileImageDiv = new Div();
		profileImageDiv.setId("authorised-banner-profile-image");
		if (user.profileImage != null) {
			minortask().setBackgroundToImage(profileImageDiv, user.profileImage);
		}
		final String welcomeUser = "Welcome to " + Globals.getApplicationName() + " @" + user.getUsername();
		setText(welcomeUser);

		Icon userIcon = new Icon(VaadinIcon.USER);
		Icon unlock = new Icon(VaadinIcon.UNLOCK);

		Button profileButton = new Button(userIcon);
		profileButton.setId("authorised-banner-profile-button");
		profileButton.addClickListener((event) -> {
			minortask().showProfile();
		});

		Button logoutButton = new Button(unlock);
		logoutButton.setId("authorised-banner-logout-button");
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

	@Override
	public String getText() {
		return welcomeMessage.getText();
	}

	@Override
	public void setText(String text) {
		welcomeMessage.setText(text);
	}

}
