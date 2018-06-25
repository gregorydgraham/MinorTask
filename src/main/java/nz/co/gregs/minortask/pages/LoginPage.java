/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class LoginPage extends MinorTaskPage{

	public LoginPage(MinorTaskUI ui) {
		super(ui);
	}

	@Override
	public void show() {
		VerticalLayout loginPanel = new VerticalLayout();
		//		layout.removeAllComponents();
		Button loginButton = new Button("Login");
		loginButton.addClickListener((Button.ClickEvent e) -> {
			handle();
		});
		setAsDefaultButton(loginButton);
		Button signupButton = new Button("Sign Up");
		signupButton.addClickListener((Button.ClickEvent e) -> {
			ui.SIGNUP.show();
		});
		HorizontalLayout buttons = new HorizontalLayout(signupButton, loginButton);
		loginPanel.addComponents(ui.USERNAME_FIELD, ui.PASSWORD_FIELD, buttons);
		show(new GridLayout(3, 3, new VerticalLayout(), new VerticalLayout(), new VerticalLayout(), new VerticalLayout(), loginPanel, new VerticalLayout(), new VerticalLayout(), new VerticalLayout(), new VerticalLayout()));
	}

	@Override
	public void handle() {
		StringBuilder warningBuffer = new StringBuilder();
		if (ui.USERNAME_FIELD.getValue().isEmpty() || ui.PASSWORD_FIELD.getValue().isEmpty()) {
			warningBuffer.append("Name and/or password do not match any known combination\n");
		} else {
			User example = new User();
			example.username.permittedValuesIgnoreCase(ui.USERNAME_FIELD.getValue());
			example.password.permittedValues(ui.PASSWORD_FIELD.getValue());
			try {
				List<User> usersFound = MinorTaskUI.database.getDBTable(example).getAllRows();
				if (usersFound.size() == 1) {
					ui.notLoggedIn = false;
					ui.setUserID(usersFound.get(0).userID.getValue());
					ui.TASKS.show();
				} else {
					warningBuffer.append("Name and password do not match any known combination\n");
				}
			} catch (SQLException ex) {
				warningBuffer.append("SQL FAILED: ").append(ex.getLocalizedMessage());
			}
		}
		if (warningBuffer.length() > 0) {
			warning("Login error", warningBuffer.toString());
		}
	}
	
}
