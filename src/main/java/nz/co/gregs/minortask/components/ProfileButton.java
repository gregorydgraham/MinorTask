/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.icon.VaadinIcon;

/**
 *
 * @author gregorygraham
 */
public class ProfileButton extends SecureSpan {//Button implements MinorTaskComponent {

	public ProfileButton() {
		super();
		init_();
	}
	
	private void init_() {
		addClassName("profile-button");

		IconWithToolTip profile = new IconWithToolTip(VaadinIcon.USER_CARD, "Profile & Settings", Position.BOTTOM_LEFT);
		profile.addClickListener((event) -> {
			minortask().showProfile();
		});

		add(profile);
	}

}
