/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.PaperInput;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@Route("profile")
@Tag("user-profile")
@HtmlImport("frontend://src/user-profile.html")
public class UserProfilePage extends PolymerTemplate<UserProfilePage.UserModel> implements BeforeEnterObserver {

	@Id("username")
	private PaperInput usernameDiv;

	MinorTask minortask = new MinorTask();

	public UserProfilePage() {
		super();
		if (minortask.isLoggedIn()) {
			System.out.println("IS LOGGED IN ID: " + minortask.getUserID());
			final User user = minortask.getUser();
			System.out.println("IS LOGGED IN USER: " + user);
			System.out.println("USERNAME: " + user.getUsername());
			getModel().setUser(user);
			getModel().setUsername(user.getUsername());
		}
		getModel().setGreeting("Welcome to the User Profile page.");
	}

	@Override
	public final void beforeEnter(BeforeEnterEvent event) {
		System.out.println("BEFORE ENTER MINORTASKPAGE");
		if (!minortask.isLoggedIn()) {
			Globals.showLogin();
		}
	}

	public static interface UserModel extends TemplateModel {

		public String getUsername();

		public void setUsername(String newValue);

		public void setGreeting(String newValue);

		public void setUser(User user);
	}
}
