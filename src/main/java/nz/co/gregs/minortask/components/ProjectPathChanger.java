/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;

/**
 *
 * @author gregorygraham
 */
public interface ProjectPathChanger {
	public Registration addProjectPathAlteredListener(
			ComponentEventListener<ProjectPathAltered> listener
	) ;
}
