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
public class ColleaguesButton extends Button implements MinorTaskComponent {

	public ColleaguesButton() {
		super();
		init_();
	}

	public ColleaguesButton(String text) {
		super(text);
		init_();
	}

	public ColleaguesButton(Component icon) {
		super(icon);
		init_();
	}

	public ColleaguesButton(String text, Component icon) {
		super(text, icon);
		init_();
	}

	public ColleaguesButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, clickListener);
		init_();
	}

	public ColleaguesButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(icon, clickListener);
		init_();
	}

	public ColleaguesButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, icon, clickListener);
		init_();
	}

	private void init_() {
		Icon userIcon = new Icon(VaadinIcon.USERS);
		setIcon(userIcon);
		addClassName("colleagues-button");
		addClickListener((event) -> {
			minortask().showColleagues();
		});
	}

}
