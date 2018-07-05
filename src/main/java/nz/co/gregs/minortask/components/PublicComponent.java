/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.minortask.MinorTask;

/**
 *
 * @author gregorygraham
 */
public abstract class PublicComponent extends CustomComponent{

	protected final MinorTask minortask;

	public PublicComponent(MinorTask minortask) {
		this.minortask = minortask;
	}

	/**
	 * @return the ui
	 */
	public MinorTask minortask() {
		return minortask;
	}

	/**
	 * @return the ui
	 */
	public DBDatabase getDatabase() {
		return minortask.getDatabase();
	}

	@Override
	protected final void setCompositionRoot(Component compositionRoot) {
		super.setCompositionRoot(compositionRoot);
	}
	
	
}
