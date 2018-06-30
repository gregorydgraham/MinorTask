/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class LoginPage extends OldMinorTaskComponent {

	public LoginPage(MinorTaskUI ui) {
		super(ui);
	}

	
	@Override
	public Component getComponent() {
		VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		Button loginButton = new Button("Login");
		setAsDefaultButton(loginButton);

		Button signupButton = new Button("Sign Up");
		signupButton.addClickListener((Button.ClickEvent e) -> {
			ui.showSignUp();
		});

		HorizontalLayout buttons = new HorizontalLayout(signupButton, loginButton);
		buttons.setComponentAlignment(loginButton, Alignment.TOP_RIGHT);

		Helper.USERNAME_FIELD.setRequiredIndicatorVisible(true);
		Helper.USERNAME_FIELD.setCursorPosition(0);
		Helper.PASSWORD_FIELD.setRequiredIndicatorVisible(true);

		loginPanel.addComponents(Helper.USERNAME_FIELD, Helper.PASSWORD_FIELD, buttons);
		return new GridLayout(
				3, 3,
				new VerticalLayout(), new VerticalLayout(), new VerticalLayout(),
				new VerticalLayout(), loginPanel, new VerticalLayout(),
				new VerticalLayout(), new VerticalLayout(), new VerticalLayout()
		);
	}

	@Override
	public void handleDefaultButton() {
		StringBuilder warningBuffer = new StringBuilder();
		if (Helper.USERNAME_FIELD.getValue().isEmpty() || Helper.PASSWORD_FIELD.getValue().isEmpty()) {
			warningBuffer.append("Name and/or password needs to be entered\n");
		} else {
			User example = new User();
			example.queryUsername().permittedValuesIgnoreCase(Helper.USERNAME_FIELD.getValue());
			example.queryPassword().permittedValues(Helper.PASSWORD_FIELD.getValue());
			try {
				List<User> users = getDatabase().getDBTable(example).getAllRows();
				switch (users.size()) {
					case 1:
						ui.loginAs(users.get(0).getUserID());
						ui.showTask(null);
						break;
					case 0:
						Helper.warning("Login Error", "Name and/or password do not match any known combination");
						break;
					default:
						Helper.warning("Login Error", "There is something odd with this login, please contact MinorTask about this issue");
						break;
				}
			} catch (SQLException ex) {
				warningBuffer.append("SQL FAILED: ").append(ex.getLocalizedMessage());
			}
		}
		if (warningBuffer.length() > 0) {
			Helper.warning("Login error", warningBuffer.toString());
		}
	}

	@Override
	public void handleEscapeButton() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
