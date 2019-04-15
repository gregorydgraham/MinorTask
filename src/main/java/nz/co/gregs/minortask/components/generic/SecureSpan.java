/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.generic;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import nz.co.gregs.minortask.components.AccessDeniedComponent;
import nz.co.gregs.minortask.components.RequiresPermission;

/**
 *
 * @author gregorygraham
 */
public class SecureSpan extends Span implements RequiresPermission {

	public SecureSpan() {
	}

	public SecureSpan(Component... components) {
		super();
		add(components);
	}
}
