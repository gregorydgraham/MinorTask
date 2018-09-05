/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;

/**
 *
 * @author gregorygraham
 */
public interface HasDefaultButton extends KeyNotifier {

	static final String DEFAULTBUTTON_CLASSNAME = "defaultbutton";
	static final String ESCAPEBUTTON_CLASSNAME = "escapebutton";

	default Registration setAsDefaultButton(
			Button button,
			ComponentEventListener<KeyPressEvent> keyPressHandler,
			ComponentEventListener<ClickEvent<Button>> clickHandler) {
		final Key enter = Key.ENTER;

		button.addClassName(DEFAULTBUTTON_CLASSNAME);
		button.addClickListener(clickHandler);
		return this.addKeyPressListener(enter, keyPressHandler);
	}

	default void removeAsDefaultButton(
			Button button, Registration defaultButtonRegistration) {
		try {
			defaultButtonRegistration.remove();
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		button.removeClassName(DEFAULTBUTTON_CLASSNAME);
	}

	default void setEscapeButton(Button button,
			ComponentEventListener<KeyPressEvent> keyPressHandler,
			ComponentEventListener<ClickEvent<Button>> clickHandler) {
		final Key enter = Key.ESCAPE;
		this.addKeyPressListener(enter, keyPressHandler);
		button.addClassName(ESCAPEBUTTON_CLASSNAME);
		button.addClickListener(clickHandler);
	}

	default void removeAsEscapeButton(
			Button button, Registration escapeButtonRegistration) {
		escapeButtonRegistration.remove();
		button.removeClassName(ESCAPEBUTTON_CLASSNAME);
	}

}
