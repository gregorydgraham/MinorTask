/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.router.BeforeEvent;

public abstract class AuthorisedTaskPage extends AuthorisedOptionalTaskPage {

	@Override
	public final void setParameter(BeforeEvent event, Long parameter) {
		super.setParameter(event, parameter);
	}
}
