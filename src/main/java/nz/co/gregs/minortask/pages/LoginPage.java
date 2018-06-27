/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.ui.Alignment;
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
public class LoginPage extends MinorTaskPage {

	public LoginPage(MinorTaskUI ui) {
		super(ui);
	}

	@Override
	public void show() {
		VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		Button loginButton = new Button("Login");
		setAsDefaultButton(loginButton);

		Button signupButton = new Button("Sign Up");
		signupButton.addClickListener((Button.ClickEvent e) -> {
			ui.SIGNUP.show();
		});

		HorizontalLayout buttons = new HorizontalLayout(signupButton, loginButton);
		buttons.setComponentAlignment(loginButton, Alignment.TOP_RIGHT);
		
		ui.USERNAME_FIELD.setRequiredIndicatorVisible(true);
		ui.PASSWORD_FIELD.setRequiredIndicatorVisible(true);

		loginPanel.addComponents(ui.USERNAME_FIELD, ui.PASSWORD_FIELD, buttons);
		show(
				new GridLayout(3, 3,
						new VerticalLayout(), new VerticalLayout(), new VerticalLayout(),
						new VerticalLayout(), loginPanel, new VerticalLayout(),
						new VerticalLayout(), new VerticalLayout(), new VerticalLayout()
				)
		);
	}

	@Override
	public void handleDefaultButton() {
		StringBuilder warningBuffer = new StringBuilder();
		if (ui.USERNAME_FIELD.getValue().isEmpty() || ui.PASSWORD_FIELD.getValue().isEmpty()) {
			warningBuffer.append("Name and/or password do not match any known combination\n");
		} else {
			User example = new User();
			example.username.permittedValuesIgnoreCase(ui.USERNAME_FIELD.getValue());
			example.password.permittedValues(ui.PASSWORD_FIELD.getValue());
			try {
				List<User> users = getDatabase().getDBTable(example).getAllRows();
				switch (users.size()) {
					case 1:
						ui.loginAs(users.get(0).userID.getValue());
						ui.TASKS.show();
						break;
					case 0:
						warning("Login Error", "Name and/or password do not match any known combination");
						break;
					default:
						warning("Login Error", "There is something odd with this login, please contact MinorTask about this issue");
						break;
				}
			} catch (SQLException ex) {
				warningBuffer.append("SQL FAILED: ").append(ex.getLocalizedMessage());
			}
		}
		if (warningBuffer.length() > 0) {
			warning("Login error", warningBuffer.toString());
		}
	}

	@Override
	public void handleEscapeButton() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
