/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.BannerMenu;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.TaskEditor;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("task")
@RouteAlias("edit")
public class TaskEditorLayout extends VerticalLayout implements ChecksLogin{

	public TaskEditorLayout() {
	}

	@Override
	public void setParameter(BeforeEvent event,  @OptionalParameter Long parameter) {
		removeAll();
		add(new MinorTaskTemplate());
		add(new BannerMenu(parameter));
		add(new TaskEditor(parameter));
		add(new FooterMenu(parameter));
	}
	
}
