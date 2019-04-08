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

/**
 *
 * @author gregorygraham
 */
@Tag("logout-button")
@StyleSheet("styles/logout-button.css")
public class LogoutButton extends SecureSpan {

	public LogoutButton() {
		super();
		init_();
	}

	private void init_() {
		addClassName("logout-button");

		IconWithToolTip unlock = new IconWithToolTip(VaadinIcon.LOCK, "Logout", Position.BOTTOM_LEFT);
		unlock.addClickListener((event) -> {
			minortask().logout();
		});
		
		add(unlock);
	}

}
