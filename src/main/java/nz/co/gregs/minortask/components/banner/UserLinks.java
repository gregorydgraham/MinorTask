/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.banner;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import nz.co.gregs.minortask.components.generic.SecureSpan;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
@Tag("userlinks")
@StyleSheet("styles/userlinks.css")
public class UserLinks extends SecureSpan implements MinorTaskEventNotifier, MinorTaskEventListener {

	ColleaguesButton colleaguesButton = new ColleaguesButton();
	ProfileButton profileButton = new ProfileButton();
	AdminButton adminButton = new AdminButton();
	LogoutButton logoutButton = new LogoutButton();

	public UserLinks() {
		super();
		this.add(this.getComponents());
	}

	private Component[] getComponents() {

		colleaguesButton.setId("authorised-banner-colleagues-button");
		colleaguesButton.addClassName("authorised-banner-button");

		profileButton.setId("authorised-banner-profile-button");
		profileButton.addClassName("authorised-banner-button");

		adminButton.setId("authorised-banner-admin-button");
		adminButton.addClassName("authorised-banner-button");

		logoutButton.setId("authorised-banner-logout-button");
		logoutButton.addClassName("authorised-banner-button");

		colleaguesButton.addMinorTaskEventListener(this);
		adminButton.addMinorTaskEventListener(this);
		profileButton.addMinorTaskEventListener(this);

		return new Component[]{colleaguesButton, profileButton, adminButton, logoutButton};
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		fireEvent(event);
	}

	public void setAllButtonsUnselected() {
		profileButton.removeClassName("authorised-banner-selected-button");
		logoutButton.removeClassName("authorised-banner-selected-button");
		colleaguesButton.removeClassName("authorised-banner-selected-button");
	}

	public void setProfileButtonSelected() {
		setAllButtonsUnselected();
		profileButton.addClassName("authorised-banner-selected-button");
	}

	public void setColleaguesButtonSelected() {
		setAllButtonsUnselected();
		colleaguesButton.addClassName("authorised-banner-selected-button");
	}

	public void setLogoutButtonSelected() {
		setAllButtonsUnselected();
		logoutButton.addClassName("authorised-banner-selected-button");
	}
}
