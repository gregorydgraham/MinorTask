/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/tooltip.css")
public interface HasToolTip extends HasStyle {

	default public void setTooltipText(String text) {
		this.addClassName("tooltip");
		Div span = new Div(new Paragraph(text));
		getElement().insertChild(0, span.getElement());
		span.addClassName("tooltiptext");
	}
}
