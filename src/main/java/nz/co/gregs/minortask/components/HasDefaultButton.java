/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;

/**
 *
 * @author gregorygraham
 */
public interface HasDefaultButton extends KeyNotifier {

	default void setAsDefaultButton(
			Button button,
			ComponentEventListener<KeyPressEvent> keyPressHandler,
			ComponentEventListener<ClickEvent<Button>> clickHandler) {
		final Key enter = Key.ENTER;
		this.addKeyPressListener(enter, keyPressHandler);
		button.addClassName("defaultbutton");
		button.addClickListener(clickHandler);
	}

	default void setEscapeButton(Button button,
			ComponentEventListener<KeyPressEvent> keyPressHandler,
			ComponentEventListener<ClickEvent<Button>> clickHandler) {
		final Key enter = Key.ESCAPE;
		this.addKeyPressListener(enter, keyPressHandler);
		button.addClassName("escapebutton");
		button.addClickListener(clickHandler);
	}

}
