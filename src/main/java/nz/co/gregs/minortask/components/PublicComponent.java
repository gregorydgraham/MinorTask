/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.minortask.MinorTask;

/**
 *
 * @author gregorygraham
 */
public abstract class PublicComponent extends VerticalLayout{

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
	
	
}
