/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.UserProfileComponent;

/**
 *
 * @author gregorygraham
 */
@Route("profile")
public class UserProfilePage extends AuthorisedPage {

	public UserProfilePage() {
		super();
	}

	@Override
	public Component getInternalComponent() {
		return new UserProfileComponent();
	}

	@Override
	public String getPageTitle() {
		return MinorTask.getApplicationName() + ": Profile";
	}
}
