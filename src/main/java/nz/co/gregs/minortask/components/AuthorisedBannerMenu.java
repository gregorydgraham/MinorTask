/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class AuthorisedBannerMenu extends HorizontalLayout implements RequiresLogin {

	public AuthorisedBannerMenu(Long taskID) {
		buildComponent();

		this.getElement().setAttribute("theme", "success primary");
		this.addClassName("authorised-banner");
	}

	public final void buildComponent() {
		setSizeUndefined();
		setWidth("100%");
		setHeight("2px");
		setDefaultVerticalComponentAlignment(Alignment.CENTER);
		
		HorizontalLayout left = new HorizontalLayout();
		left.setDefaultVerticalComponentAlignment(Alignment.START);
		left.setAlignItems(Alignment.START);
		left.setWidth("100%");
		
		HorizontalLayout right = new HorizontalLayout();
		right.setDefaultVerticalComponentAlignment(Alignment.END);
		right.setAlignItems(Alignment.END);
		right.setWidth("100%");
		
		final long userID = minortask().getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			final DBTable<User> userTable = getDatabase().getDBTable(example);
			User user = userTable.getOnlyRow();
			final Label label = new Label("Welcome to MinorTask @" + user.getUsername());
			left.add(label);
			setVerticalComponentAlignment(Alignment.CENTER, label);
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
			Globals.sqlerror(ex);
		}
		
		Button profileButton = new Button("Profile");
		profileButton.addClickListener((event) -> {
			minortask().showProfile(); 
		});
		right.add(profileButton);
		
		Button logoutButton = new Button("Logout");
		logoutButton.addClickListener((event) -> {
			minortask().logout();
		});

		right.add(logoutButton);
		setVerticalComponentAlignment(Alignment.END, logoutButton);
		setAlignItems(FlexComponent.Alignment.END);
		add(left, right);
	}

}
