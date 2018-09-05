/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
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
public class LoginPage extends VerticalLayout implements HasUrlParameter<String> {

	LoginComponent loginComponent = new LoginComponent();

	public LoginPage() {
		try {
			MinorTask minorTask = new MinorTask();
			add(getLoginPageContents());
			minorTask.chatAboutUsers();
		} catch (Exception ex) {
			System.out.println("nz.co.gregs.minortask.pages.LoginPage.<init>(): " + ex.getClass().getSimpleName() + " -> " + ex.getMessage());
			ex.printStackTrace();
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
