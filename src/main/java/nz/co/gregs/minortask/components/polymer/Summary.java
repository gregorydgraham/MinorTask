/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.polymer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;

/**
 *
 * @author gregorygraham
 */
@Tag("summary")
public class Summary extends Component implements HasComponents, HasStyle, HasText{

	private Summary() {
		super();
	}

	public Summary(String text) {
		this();
		setText(text);
	}

	public Summary(Component... components) {
		this();
		add(components);
	}

	public Summary(String text, Component... components) {
		this();
		setText(text);
		add(components);
	}
}
