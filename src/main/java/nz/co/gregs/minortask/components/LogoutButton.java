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
public class LogoutButton extends SecureSpan {

	public LogoutButton() {
		super();
		init_();
	}

	private void init_() {
		addClassName("logout-button");

		IconWithToolTip unlock = new IconWithToolTip(VaadinIcon.UNLOCK, "Logout", Position.BOTTOM_LEFT);
		unlock.addClickListener((event) -> {
			minortask().logout();
		});
		
		add(unlock);
	}

}
