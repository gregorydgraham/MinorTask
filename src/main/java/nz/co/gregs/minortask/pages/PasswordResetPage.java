/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.PublicBannerMenu;
import nz.co.gregs.minortask.components.ResetPasswordComponent;


/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("resetpassword")
public class PasswordResetPage extends VerticalLayout implements HasUrlParameter<String>{

	public PasswordResetPage() {
		resetComponent = new ResetPasswordComponent();
		add(new MinorTaskTemplate());
		add(new PublicBannerMenu());
		add(resetComponent);
	}
	public final ResetPasswordComponent resetComponent;

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		try {	
			resetComponent.setResetCode(parameter);
		} catch (ResetPasswordComponent.NoSuchResetRequest ex) {
			remove(resetComponent);
			VerticalLayout layout = new VerticalLayout();
			layout.add(new Label("Unable to use this reset request, please request another"));
			Button button = new Button("Request Reset");
			button.addClickListener((event2) -> {
				MinorTask.showLostPassword("");
			});
			layout.add(button);
			add(layout);
		}
	}
	
}
