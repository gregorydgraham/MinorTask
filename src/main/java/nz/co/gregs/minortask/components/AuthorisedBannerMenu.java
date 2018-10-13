/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class AuthorisedBannerMenu extends Div implements RequiresLogin, HasText {

	final Anchor welcomeMessage = new Anchor("http://minortask.com", "Welcome");

	public AuthorisedBannerMenu() {
		buildComponent();

//		this.getElement().setAttribute("theme", "success primary");
		this.addClassName("authorised-banner");
	}

	public final void buildComponent() {
		setSizeUndefined();
		setWidth("100%");

		Div left = new Div();
		left.addClassName("authorised-banner-left");

		Div right = new Div();
		right.addClassName("authorised-banner-right");
		
		welcomeMessage.addClassName("welcome-message");

		left.add(welcomeMessage);
		setText("Welcome to " + Globals.getApplicationName());

		final long userID = minortask().getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			final DBTable<User> userTable = getDatabase().getDBTable(example);
			User user = userTable.getOnlyRow();
			final String welcomeUser = "Welcome to MinorTask @" + user.getUsername();
			setText(welcomeUser);
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
		}

		Icon userIcon = new Icon(VaadinIcon.USER);
		Icon unlock = new Icon(VaadinIcon.UNLOCK);
		Icon lock = new Icon(VaadinIcon.LOCK);
		
		Button profileButton = new Button(userIcon);
		profileButton.addClickListener((event) -> {
			minortask().showProfile();
		});
		right.add();

		Button logoutButton = new Button(unlock);
		logoutButton.addClickListener((event) -> {
			minortask().logout();
		});

		right.add(logoutButton, profileButton);
		final Div clearBreak = new Div();
		clearBreak.getStyle().set("clear", "both");
		add(left, right);
	}

	@Override
	public String getText() {
		return welcomeMessage.getText();
	}

	@Override
	public void setText(String text) {
		welcomeMessage.setText(text);
	}

}
