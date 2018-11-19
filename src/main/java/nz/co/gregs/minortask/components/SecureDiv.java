/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 *
 * @author gregorygraham
 */
public class SecureDiv extends Div implements MinorTaskComponent {

	public SecureDiv() {
	}

	@Override
	public void add(Component... components) {
		if (isAccessDenied(this)) {
			for (Component component : components) {
				super.add(new AccessDeniedComponent());
			}
		} else {
			super.add(components);
		}
	}
}
