/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.AuthorisedBannerMenu;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.components.TaskTabs;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("frontend://styles/shared-styles.html")
public abstract class AuthorisedPage extends VerticalLayout implements MinorTaskComponent, BeforeEnterObserver, HasDynamicTitle {

	protected Long taskID = null;
	TaskTabs taskTabs;
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
			event.rerouteTo(LoginPage.class); 
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
		taskTabs = new TaskTabs(this, taskID);
		taskTabs.setOrientation(Tabs.Orientation.HORIZONTAL);
		final Component internalComponent = getInternalComponent();
		VerticalLayout internalComponentHolder
				= new VerticalLayout(
						banner,
						taskTabs,
						internalComponent
				);
		internalComponentHolder.addClassName("minortask-internal");
		VerticalLayout verticalLayout = new VerticalLayout(internalComponentHolder);
		add(verticalLayout);
		add(new FooterMenu());
	}
}
