/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.banner;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import nz.co.gregs.minortask.components.generic.SecureSpan;
import com.vaadin.flow.component.icon.VaadinIcon;
import nz.co.gregs.minortask.MinorTaskViews;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
@Tag("profile-button")
@StyleSheet("styles/profile-button.css")
public class ProfileButton extends SecureSpan implements MinorTaskEventNotifier {

	public ProfileButton() {
		super();
		init_();
	}

	private void init_() {
		addClassName("profile-button");

		IconWithToolTip profile = new IconWithToolTip(VaadinIcon.USER_CARD, "Profile & Settings", Position.BOTTOM_LEFT);
		profile.addClickListener((event) -> {
			fireEvent(new MinorTaskEvent(this, MinorTaskViews.PROFILE, true));
//			minortask().showProfile();
		});

		add(profile);
	}

}
