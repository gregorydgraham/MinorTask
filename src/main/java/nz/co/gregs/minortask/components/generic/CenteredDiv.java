/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.generic;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;


@Tag("centered-div")
public class CenteredDiv extends Div {
	
	private final SecureDiv innerDiv = new SecureDiv();

	public CenteredDiv() {
		addClassName("centered-div");
		super.add(innerDiv);
	}

	public CenteredDiv(Component... components) {
		this();
		innerDiv.add(components);
	}

	@Override
	public void addComponentAsFirst(Component component) {
		innerDiv.addComponentAsFirst(component);
	}

	@Override
	public void addComponentAtIndex(int index, Component component) {
		innerDiv.addComponentAtIndex(index, component);
	}

	@Override
	public void removeAll() {
		innerDiv.removeAll();
	}

	@Override
	public void remove(Component... components) {
		innerDiv.remove(components);
	}

	@Override
	public void add(Component... components) {
		innerDiv.add(components);
	}
	
	
	
}
