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
import com.vaadin.flow.router.HasUrlParameter;
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
public abstract class MinorTaskPage extends VerticalLayout implements MinorTaskComponent, BeforeEnterObserver, HasUrlParameter<Long>, HasDynamicTitle {

	protected Long taskID = null;
	TaskTabs taskTabs;
	private final AuthorisedBannerMenu banner= new AuthorisedBannerMenu();
	
	protected abstract Component getInternalComponent(Long parameter);

	public MinorTaskPage() {
		addClassName("minortask-page");
	}

	@Override
	public final void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
		taskID = parameter;
		minortask().setLoginDestination(MinorTask.getCurrentLocation());
		if (!minortask().isLoggedIn()) {
			showLoginPanel();
		} else {
			removeAll();
			add(new MinorTaskTemplate());
			taskTabs = new TaskTabs(this, taskID);
			taskTabs.setOrientation(Tabs.Orientation.HORIZONTAL);
			final Component internalComponent = getInternalComponent(parameter);
			final VerticalLayout layout = new VerticalLayout(taskTabs);
			layout.setSizeUndefined();
			VerticalLayout internalComponentHolder
					= new VerticalLayout(
							banner,
							layout,
							internalComponent
					);
			internalComponentHolder.addClassName("minortask-internal");
			VerticalLayout verticalLayout = new VerticalLayout(internalComponentHolder);
			add(verticalLayout);
			add(new FooterMenu());
		}
	}
	
	protected void setWelcomeMessage(String message){
		banner.setText(message);
	}

	private void showLoginPanel() {
		removeAll();
		Location location = MinorTask.getCurrentLocation();
		minortask().setLoginDestination(location);
		add(new LoginPage().getLoginPageContents());
	}

	@Override
	public final void beforeEnter(BeforeEnterEvent event) {
		System.out.println("BEFORE ENTER MINORTASKPAGE");
		if (minortask().getNotLoggedIn()) {
			Location location = MinorTask.getCurrentLocation();
			minortask().setLoginDestination(location);
			MinorTask.showLogin();
		}
	}
}
