/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class PublicBannerMenu extends HorizontalLayout implements MinorTaskComponent {

	public PublicBannerMenu() {
		buildComponent();
		this.addClassName("public-banner");
	}

	public final void buildComponent() {
		setSizeUndefined();
		setDefaultVerticalComponentAlignment(Alignment.START);

		final long userID = minortask().getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		final Label label = new Label("Welcome to " + minortask().getApplicationName());
		label.setSizeFull();
		add(label);
		setVerticalComponentAlignment(Alignment.CENTER, label);
	}
}
