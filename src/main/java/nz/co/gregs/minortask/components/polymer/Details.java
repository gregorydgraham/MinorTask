/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.polymer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;

/**
 *
 * @author gregorygraham
 */
@Tag("details")
public class Details extends Component implements HasComponents, HasStyle{

	private Details() {
		super();
	}

	public Details(String text) {
		this();
		add(new Summary(text));
	}

	public Details(Component... components) {
		this();
		add(components);
	}

	public Details(String summary, Component... components) {
		this(summary);
		add(components);
	}
}
