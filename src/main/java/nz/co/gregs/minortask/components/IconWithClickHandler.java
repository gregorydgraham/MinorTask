/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 *
 * @author gregorygraham
 */
public class IconWithClickHandler extends Icon implements ClickNotifier<IconWithClickHandler>, HasToolTip{

	public IconWithClickHandler(VaadinIcon vaadinIcon) {
		super(vaadinIcon);
	}
	
}
