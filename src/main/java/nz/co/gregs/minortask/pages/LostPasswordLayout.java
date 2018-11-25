/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.PublicBannerMenu;
import nz.co.gregs.minortask.components.LostPasswordComponent;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route(value="lostpassword", layout = MinortaskPage.class)
public class LostPasswordLayout extends VerticalLayout implements HasUrlParameter<String> {

	public LostPasswordLayout() {
		this("");
	}

	public LostPasswordLayout(String username) {
		component = new LostPasswordComponent(username);
		add(new MinorTaskTemplate());
		add(new PublicBannerMenu());
		add(component);
	}
	public final LostPasswordComponent component;

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		component.setUsername(parameter);
	}

}
