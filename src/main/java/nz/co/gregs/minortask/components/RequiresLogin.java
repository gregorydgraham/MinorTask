/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import nz.co.gregs.minortask.pages.LoginPage;

/**
 *
 * @author gregorygraham
 */
public interface RequiresLogin extends HasMinorTask, BeforeEnterObserver {

	@Override
	default public void beforeEnter(BeforeEnterEvent event) {
		if (minortask().getNotLoggedIn()) {
			event.rerouteTo(LoginPage.class);
		}
	}
}
