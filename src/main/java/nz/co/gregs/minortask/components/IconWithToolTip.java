/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 *
 * @author gregorygraham
 */
public class IconWithToolTip extends SecureDiv {

	public IconWithToolTip(VaadinIcon vaadinIcon) {
		_init(vaadinIcon, null);
	}

	public IconWithToolTip(VaadinIcon vaadinIcon, String tooltip) {
		_init(vaadinIcon, tooltip);
	}

	private void _init(VaadinIcon vaadinIcon, String tooltip) {
		this.add(new Icon(vaadinIcon));
		if (tooltip != null) {
			setTooltipText(tooltip);
		}
	}
	
	@Override
	public void setTooltipText(String text) {
		this.addClassName("tooltip");
		Div span = new Div(new Paragraph(text));
		getElement().insertChild(0, span.getElement());
		span.addClassName("icontooltiptext");
	}
}
