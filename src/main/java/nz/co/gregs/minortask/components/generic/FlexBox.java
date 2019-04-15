/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.generic;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;

/**
 *
 * @author gregorygraham
 */
@Tag("flexbox")
public class FlexBox extends Span {

	public FlexBox() {
		getStyle().set("display", "flex");
		getStyle().set("flex-direction", "row");
		getStyle().set("flex-wrap", "wrap");
	}

	public FlexBox(Component... components) {
		this();
		add(components);
	}
}
