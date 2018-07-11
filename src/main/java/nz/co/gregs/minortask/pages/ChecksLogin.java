/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import nz.co.gregs.minortask.components.HasMinorTask;

/**
 *
 * @author gregorygraham
 */
public interface ChecksLogin extends HasMinorTask, BeforeEnterObserver, HasUrlParameter<Long>  {

	@Override
	default public void beforeEnter(BeforeEnterEvent event) {
		if (minortask().getNotLoggedIn()) {
			
			Location location = event.getLocation();
			minortask().setLoginDestination(location);
			event.rerouteTo(LoginPage.class);
		}
	}
}
