/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import nz.co.gregs.minortask.MinorTaskUI;

/**
 *
 * @author gregorygraham
 */
public abstract class PublicComponent extends CustomComponent{

	private final MinorTaskUI minorTaskUI;

	public PublicComponent(MinorTaskUI ui) {
		this.minorTaskUI = ui;
	}

	/**
	 * @return the ui
	 */
	public MinorTaskUI minortask() {
		return minorTaskUI;
	}

	@Override
	protected final void setCompositionRoot(Component compositionRoot) {
		super.setCompositionRoot(compositionRoot);
	}
	
	
}
