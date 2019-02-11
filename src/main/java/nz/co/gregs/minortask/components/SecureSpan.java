/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

/**
 *
 * @author gregorygraham
 */
public class SecureSpan extends Span implements RequiresLogin {

	public SecureSpan() {
	}

	public SecureSpan(Component... components) {
		super();
		add(components);
	}

	@Override
	public final void add(Component... components) {
		if (!checkForPermission()) {
			for (Component component : components) {
				super.add(new AccessDeniedComponent());
			}
		} else {
			super.add(components);
		}
	}

	protected boolean checkForPermission() {
		return isLoggedIn();
	}
}
