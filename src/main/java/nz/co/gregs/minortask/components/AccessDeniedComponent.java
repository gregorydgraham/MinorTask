/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.dom.Element;

/**
 *
 * @author gregorygraham
 */
//@Tag("access-denied-component")
public class AccessDeniedComponent extends Label implements MinorTaskComponent {

	public AccessDeniedComponent() {
		setText("Access Denied");
		setTitle("Access to this task or a required field has been denied either due to security concerns or the task not existing.");
		getElement().setAttribute("theme", "error");
	}

	@Override
	public final void setTitle(String title) {
		super.setTitle(title);
	}

	@Override
	public final Element getElement() {
		return super.getElement();
	}

}
