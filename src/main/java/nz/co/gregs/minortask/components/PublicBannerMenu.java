/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class PublicBannerMenu extends HorizontalLayout implements MinorTaskComponent {

	public PublicBannerMenu() {
		Component banner = buildComponent();

		this.add(banner);
		this.setSizeUndefined();
		this.setWidth("100%");
		this.addClassName("banner");
	}

	public final Component buildComponent() {
		HorizontalLayout banner = new HorizontalLayout();
		banner.setSizeUndefined();
		banner.setWidth("100%");
		banner.setDefaultVerticalComponentAlignment(Alignment.START);

		final long userID = minortask().getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		final Label label = new Label("Welcome to " + minortask().getApplicationName());
		label.setSizeFull();
		banner.add(label);
		banner.setVerticalComponentAlignment(Alignment.CENTER, label);

		return banner;
	}
}
