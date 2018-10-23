/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ComponentEventListener;
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
			Button button) {
		final Key enter = Key.ENTER;

		button.addClassName(DEFAULTBUTTON_CLASSNAME);
		return this.addKeyPressListener(enter, (event) -> {
			button.click();
		});
	}

	default Registration setAsDefaultButton(
			Button button,
			ComponentEventListener<KeyPressEvent> keyPressHandler) {
		final Key enter = Key.ENTER;

		button.addClassName(DEFAULTBUTTON_CLASSNAME);
		return this.addKeyPressListener(enter, keyPressHandler);
	}

	default void removeAsDefaultButton(
			Button button, Registration defaultButtonRegistration) {
		try {
			if (defaultButtonRegistration != null) {
				defaultButtonRegistration.remove();
			}
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		button.removeClassName(DEFAULTBUTTON_CLASSNAME);
	}

	default Registration setEscapeButton(Button button,
			ComponentEventListener<KeyPressEvent> keyPressHandler) {
		final Key enter = Key.ESCAPE;
		button.addClassName(ESCAPEBUTTON_CLASSNAME);
		return this.addKeyPressListener(enter, keyPressHandler);
	}

	default void removeAsEscapeButton(
			Button button, Registration escapeButtonRegistration) {
		escapeButtonRegistration.remove();
		button.removeClassName(ESCAPEBUTTON_CLASSNAME);
	}

}
