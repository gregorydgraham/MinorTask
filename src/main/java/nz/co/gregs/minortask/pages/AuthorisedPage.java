/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Location;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.AuthorisedBannerMenu;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.components.ProjectPathNavigator;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("frontend://styles/shared-styles.html")
@StyleSheet("frontend://styles/authorised-page.css")
public abstract class AuthorisedPage extends Div implements MinorTaskComponent, BeforeEnterObserver, HasDynamicTitle {

	protected Long taskID = null;
	protected final AuthorisedBannerMenu banner = new AuthorisedBannerMenu();

	protected abstract Component getInternalComponent();

	public AuthorisedPage() {
		addClassName("minortask-page");
	}

	@Override
	public final void beforeEnter(BeforeEnterEvent event) {
		System.out.println("BEFORE ENTER MINORTASKPAGE");
		Location location = MinorTask.getCurrentLocation();
		minortask().setLoginDestination(location);
		if (!minortask().isLoggedIn()) {
			setLoginComponents();
		} else {
			setComponents();
		}
	}

	protected void setWelcomeMessage(String message) {
		banner.setText(message);
	}

	public void setComponents() {
		minortask().setLoginDestination(MinorTask.getCurrentLocation());
		removeAll();
		add(new MinorTaskTemplate());
		final Component internalComponent = getInternalComponent();
		
		ProjectPathNavigator projectPath = new ProjectPathNavigator.WithAddProjectButton();
		Div internalComponentHolder
				= new Div(
						banner,
						projectPath,
						internalComponent
				);
		internalComponentHolder.addClassName("minortask-internal");
		Div verticalLayout = new Div(internalComponentHolder);
		add(verticalLayout);
		add(new FooterMenu());
	}

	private void setLoginComponents() {
		removeAll();
		add(new LoginPage(MinorTask.getCurrentLocation()).getLoginPageContents());
	}
}
