/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.generic;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;
import nz.co.gregs.minortask.components.AccessDeniedComponent;
import nz.co.gregs.minortask.components.RequiresPermission;

/**
 *
 * @author gregorygraham
 */
@Tag("flexcolumn")
public class FlexColumn extends Span implements RequiresPermission {

	public FlexColumn() {
		getStyle().set("display", "flex");
		getStyle().set("flex-direction", "column");
	}

	public FlexColumn(Component... components) {
		this();
		add(components);
	}
}
