/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.LoginComponent;
import nz.co.gregs.minortask.components.PublicBannerMenu;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("")
public class LoginPage extends Div implements HasUrlParameter<String>, BeforeEnterObserver {

	LoginComponent loginComponent = new LoginComponent();
	private MinorTask minortask = new MinorTask();

	public LoginPage() {
		try {
			add(getLoginPageContents());
			MinorTask.chatAboutUsers();
		} catch (Exception ex) {
			System.out.println("nz.co.gregs.minortask.pages.LoginPage.<init>(): " + ex.getClass().getSimpleName() + " -> " + ex.getMessage());
			ex.printStackTrace();
		}
		addClassName("login-page");
	}

	@Override
	public final void beforeEnter(BeforeEnterEvent event) {
		System.out.println("BEFORE ENTER MINORTASKPAGE");
		String url = UI.getCurrent().getRouter().getUrl(TodaysTaskLayout.class);
		Location location = new Location(url);
		minortask.setLoginDestination(location);
		if (minortask.isLoggedIn()) {
		}
	}

	public final Component[] getLoginPageContents() {
		List<Component> components = new ArrayList<>();
		final MinorTaskTemplate minorTaskTemplate = new MinorTaskTemplate();
		components.add(minorTaskTemplate);
		components.add(new PublicBannerMenu());
		components.add(loginComponent);
		components.add(new FooterMenu());
		return components.toArray(new Component[]{});
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		loginComponent.setUsername(parameter);
	}

}
