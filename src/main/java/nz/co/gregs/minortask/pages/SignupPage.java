/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.data.Binder;
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
	private final User newUser = new User();
	private final Binder<User> binder = new Binder<>();

	public SignupPage(MinorTaskUI loginUI) {
		super(loginUI);
	}

	@Override
	public void show() {
		VerticalLayout layout = new VerticalLayout();
		
		REPEAT_PASSWORD_FIELD.clear();
		ui.USERNAME_FIELD.setRequiredIndicatorVisible(true);
		ui.PASSWORD_FIELD.setRequiredIndicatorVisible(true);
		REPEAT_PASSWORD_FIELD.setRequiredIndicatorVisible(true);
		
//		binder.bind(ui.USERNAME_FIELD, User::getUsername, User::setUsername);
//		binder.bind(ui.PASSWORD_FIELD, User::getPassword, User::setPassword);
//		binder.bind(EMAIL_FIELD, User::getEmail, User::setEmail);
		
		newUser.setUsername(ui.USERNAME_FIELD.getValue());
		newUser.setPassword(ui.PASSWORD_FIELD.getValue());
		binder.setBean(newUser);
		
		Button signupButton = new Button("Request Sign Up");
		setAsDefaultButton(signupButton);

		Button returnToLoginButton = new Button("Return to Login");
		setEscapeButton(returnToLoginButton);

		HorizontalLayout buttonLayout = new HorizontalLayout(returnToLoginButton, signupButton);
		layout.addComponents(ui.USERNAME_FIELD, EMAIL_FIELD, new HorizontalLayout(ui.PASSWORD_FIELD, REPEAT_PASSWORD_FIELD), buttonLayout);
		show(layout);
	}

	@Override
	public void handleDefaultButton() {
		final String name = newUser.getUsername();//ui.USERNAME_FIELD.getValue();
		final String email = newUser.getEmail();//EMAIL_FIELD.getValue();
		final String pass = newUser.getPassword();//ui.PASSWORD_FIELD.getValue();
		final String pass2 = REPEAT_PASSWORD_FIELD.getValue();
		final StringBuffer warningBuffer = new StringBuffer();
		chat(name);
		if (name.isEmpty() || pass.isEmpty()) {
			warningBuffer.append("Blank names and passwords are not allowed\n");
		}if (name.contains(" ")) {
			warningBuffer.append("Usernames may not contain spaces\n");
		}
		if (!pass.equals(pass2)) {
			warningBuffer.append("The passwords do not match, try typing them again\n");
		}
		if (!(pass.length() > 10 || (pass.matches("[a-z]") && pass.matches("[A-Z]") && pass.matches("[0-9]") && pass.matches("[ _+-=!@#$%^&*(),./;'<>?:{}|]")))) {
			warningBuffer.append("Passwords must be greater than 10 characters or contain at least one each of lowercase letters, upper case letters, number, and symbols(_+-=!@#$%^&*,./;'<>?:{}|)\n");
		}
		User example = new User();
		example.queryUsername().permittedValuesIgnoreCase(name);
		try {
			Long count = getDatabase().getDBTable(example).count();
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
				chat("User name: "+newUser.getUsername());
//				user = new User();
//				user.setUsername(name);
//				user.setEmail(email);
//				user.setPassword(pass);
				newUser.setSignupDate(new Date());
				getDatabase().insert(newUser);
				chat("Welcome to Minor Task @" + name);
				new LoginPage(ui).handleDefaultButton();
			} catch (SQLException ex) {
				sqlerror(ex);
			}
		}
	}

	@Override
	public void handleEscapeButton() {
		new LoginPage(ui).show();
	}

}
