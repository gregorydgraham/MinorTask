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
import nz.co.gregs.minortask.components.SignupComponent;


/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("signup")
public class SignUpLayout extends VerticalLayout implements HasUrlParameter<String>{

	public SignUpLayout() {
		signupComponent = new SignupComponent("", "");
		add(new MinorTaskTemplate());
		add(new PublicBannerMenu());
		add(signupComponent);
	}
	public final SignupComponent signupComponent;

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		signupComponent.setUsername(parameter);	
	}
	
}
