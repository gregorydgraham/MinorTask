/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.AuthorisedBannerMenu;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.components.TaskTabs;

/**
 *
 * @author gregorygraham
 */
public abstract class MinorTaskPage extends VerticalLayout implements MinorTaskComponent, BeforeEnterObserver, HasUrlParameter<Long>, HasDynamicTitle {

	protected Long taskID = null;
	TaskTabs taskTabs;

	protected abstract Component getInternalComponent(Long parameter);

	public MinorTaskPage() {
	}

	@Override
	public final void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
		removeAll();
		taskID = parameter;
		add(new MinorTaskTemplate());
		if (minortask().getNotLoggedIn()) {
			minortask().showLogin();
		} else {
			add(new AuthorisedBannerMenu(parameter));
			taskTabs = new TaskTabs(this, taskID);
			add(taskTabs);
			add(getInternalComponent(parameter));
			add(new FooterMenu());
		}
	}

	@Override
	public final void beforeEnter(BeforeEnterEvent event) {
		if (minortask().getNotLoggedIn()) {
			Location location = event.getLocation();
			minortask().setLoginDestination(location);
			event.rerouteTo(LoginPage.class);
		}
	}
}
