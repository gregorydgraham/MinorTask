/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.generic;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import nz.co.gregs.minortask.components.RequiresPermission;

/**
 *
 * @author gregorygraham
 */
public class SecureDiv extends Div implements RequiresPermission {

	public SecureDiv() {
	}

	public SecureDiv(Component... components) {
		super();
		add(components);
	}
}
