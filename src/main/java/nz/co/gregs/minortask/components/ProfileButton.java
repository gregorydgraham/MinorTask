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
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/minortask-buttons.css")
public class ProfileButton extends Button implements MinorTaskComponent {

	public ProfileButton() {
		super();
		init_();
	}

	public ProfileButton(String text) {
		super(text);
		init_();
	}

	public ProfileButton(Component icon) {
		super(icon);
		init_();
	}

	public ProfileButton(String text, Component icon) {
		super(text, icon);
		init_();
	}

	public ProfileButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, clickListener);
		init_();
	}

	public ProfileButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(icon, clickListener);
		init_();
	}

	public ProfileButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, icon, clickListener);
		init_();
	}

	private void init_() {
		Icon userIcon = new Icon(VaadinIcon.USER_CARD);
		setIcon(userIcon);
		addClassName("profile-button");
		addClickListener((event) -> {
			minortask().showProfile();
		});
	}

}
