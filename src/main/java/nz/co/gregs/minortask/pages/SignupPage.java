/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import java.util.Date;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class SignupPage extends MinorTaskPage {

	public final PasswordField REPEAT_PASSWORD_FIELD = new PasswordField("Repeat Password");
	public final TextField EMAIL_FIELD = new TextField("Rescue Email Address");

	public SignupPage(MinorTaskUI loginUI) {
		super(loginUI);
	}

	@Override
	public void show() {
		REPEAT_PASSWORD_FIELD.clear();
		VerticalLayout layout = new VerticalLayout();
		Button signupButton = new Button("Request Sign Up");
		signupButton.addClickListener((Button.ClickEvent e) -> {
			handle();
		});
		setAsDefaultButton(signupButton);
		Button returnToLoginButton = new Button("Return to Login");
		returnToLoginButton.addClickListener((Button.ClickEvent e) -> {
			ui.LOGIN.show();
		});
		
		HorizontalLayout buttonLayout = new HorizontalLayout(signupButton, returnToLoginButton);
		layout.addComponents(ui.USERNAME_FIELD, EMAIL_FIELD, ui.PASSWORD_FIELD, REPEAT_PASSWORD_FIELD, buttonLayout);
		show(layout);
	}

	@Override
	public void handle() {
		final String name = ui.USERNAME_FIELD.getValue();
		final String email = EMAIL_FIELD.getValue();
		final String pass = ui.PASSWORD_FIELD.getValue();
		final String pass2 = REPEAT_PASSWORD_FIELD.getValue();
		final StringBuffer warningBuffer = new StringBuffer();
		if (name.isEmpty() || pass.isEmpty()) {
			warningBuffer.append("Blank names and passwords are not allowed\n");
		}
		if (!pass.equals(pass2)) {
			warningBuffer.append("The passwords do not match, try typing them again\n");
		}
		if (!(pass.length() > 10 || (pass.matches("[a-z]") && pass.matches("[A-Z]") && pass.matches("[0-9]") && pass.matches("[ _+-=!@#$%^&*(),./;'<>?:{}|]")))) {
			warningBuffer.append("Passwords must be greater than 10 characters or contain at least one each of lowercase letters, upper case letters, number, and symbols(_+-=!@#$%^&*,./;'<>?:{}|)\n");
		}
		User user = new User();
		user.username.permittedValuesIgnoreCase(name);
		try {
			Long count = MinorTaskUI.database.getDBTable(user).count();
			if (count > 0) {
				error("You're unique", "Sorry, that username is already taken, please try another one");
			}
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		if (warningBuffer.length() > 0) {
			error("Secure password required", warningBuffer.toString());
		} else {
			try {
				user = new User();
				user.username.setValue(name);
				user.email.setValue(email);
//				user.defaultProject.setValue(defaultProject);
				user.password.setValue(pass);
				user.signupDate.setValue(new Date());
				MinorTaskUI.database.insert(user);
				chat("Welcome to Minor Task " + name);
				ui.LOGIN.handle();
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
	}

}
