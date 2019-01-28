/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;

/**
 *
 * @author gregorygraham
 */
public class SecureButton extends Button implements RequiresLogin, HasToolTip {

	/**
	 * Default constructor. Creates an empty button.
	 */
	public SecureButton() {
		super();
	}

	/**
	 * Creates a button with a text inside.
	 *
	 * @param text the text inside the button
	 * @see #setText(String)
	 */
	public SecureButton(String text) {
		super(text);
	}

	/**
	 * Creates a button with an icon inside.
	 *
	 * @param icon the icon inside the button
	 * @see #setIcon(Component)
	 */
	public SecureButton(Component icon) {
		super(icon);
	}

	/**
	 * Creates a button with a text and an icon inside.
	 * <p>
	 * Use {@link #setIconAfterText(boolean)} to change the order of the text and
	 * the icon.
	 *
	 * @param text the text inside the button
	 * @param icon the icon inside the button
	 * @see #setText(String)
	 * @see #setIcon(Component)
	 */
	public SecureButton(String text, Component icon) {
		super(text, icon);
	}

	public SecureButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, clickListener);
	}

	public SecureButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(icon, clickListener);
	}

	public SecureButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, icon, clickListener);
	}
}
