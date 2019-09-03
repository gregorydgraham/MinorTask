/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.banner;

import nz.co.gregs.minortask.components.generic.SecureSpan;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 *
 * @author gregorygraham
 */
public class IconWithToolTip extends SecureSpan {

	public IconWithToolTip(VaadinIcon vaadinIcon) {
		_init(vaadinIcon, null, Position.BOTTOM_RIGHT);
	}

	public IconWithToolTip(VaadinIcon vaadinIcon, String tooltip) {
		_init(vaadinIcon, tooltip, Position.BOTTOM_RIGHT);
	}

	public IconWithToolTip(VaadinIcon vaadinIcon, String tooltip, Position posn) {
		_init(vaadinIcon, tooltip, posn);
	}

	private void _init(VaadinIcon vaadinIcon, String tooltip, Position posn) {
		this.add(new Icon(vaadinIcon));
		if (tooltip != null) {
			this.setTitle(tooltip);
			setTooltipText(tooltip, posn);
		}
	}
	
	@Override
	public void setTooltipText(String text) {
		setTooltipText(text, Position.BOTTOM_RIGHT);
	}
}
