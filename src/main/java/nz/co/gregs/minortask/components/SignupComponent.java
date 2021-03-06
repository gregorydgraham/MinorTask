/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Location;
import java.sql.SQLException;
import java.util.Date;
import nz.co.gregs.dbvolution.exceptions.IncorrectPasswordException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class SignupComponent extends VerticalLayout implements MinorTaskComponent {

	public final TextField USERNAME_FIELD = new TextField("Your Name");
	public final PasswordField PASSWORD_FIELD = new PasswordField("Password");
	public final PasswordField REPEAT_PASSWORD_FIELD = new PasswordField("Repeat Password");
	public final TextField EMAIL_FIELD = new TextField("Rescue Email Address");
	private final User newUser = new User();
	private final Checkbox REMEMBER_ME_FIELD = new Checkbox("Remember Me", false);
	private Location destination;

	public SignupComponent() {
		this("", "");
	}

	public SignupComponent(String username, String password) {
		USERNAME_FIELD.setValue(username);
		PASSWORD_FIELD.setValue(password);
		add(getComponent());
		USERNAME_FIELD.focus();
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();

		REPEAT_PASSWORD_FIELD.clear();
		USERNAME_FIELD.setRequiredIndicatorVisible(true);
		EMAIL_FIELD.setRequiredIndicatorVisible(true);
		PASSWORD_FIELD.setRequiredIndicatorVisible(true);
		REPEAT_PASSWORD_FIELD.setRequiredIndicatorVisible(true);

		Button signupButton = new Button("Request Sign Up");
		setAsDefaultButton(signupButton);

		Button returnToLoginButton = new Button("Return to Login");
		setEscapeButton(returnToLoginButton);

		HorizontalLayout buttonLayout = new HorizontalLayout(returnToLoginButton, signupButton);
		layout.add(USERNAME_FIELD, EMAIL_FIELD, new HorizontalLayout(PASSWORD_FIELD, REPEAT_PASSWORD_FIELD), buttonLayout);

		return layout;
	}

	public void handleDefaultButton() {
		try {
			final String username = USERNAME_FIELD.getValue().trim();
			final String email = EMAIL_FIELD.getValue().trim();
			final String pass = PASSWORD_FIELD.getValue();
			final String pass2 = REPEAT_PASSWORD_FIELD.getValue();
			final StringBuffer warningBuffer = new StringBuffer();
			MinorTask.chat(username);
			if (username.isEmpty() || pass.isEmpty()) {
				warningBuffer.append("Blank names and passwords are not allowed\n");
			}
			if (username.contains(" ")) {
				warningBuffer.append("Usernames may not contain spaces\n");
			}
			if (email.equals("")) {
				warningBuffer.append("An e-mail address helps you when you need a password reset\n");
			} else if (!email.matches(".*@.*\\..*")) {
				warningBuffer.append("That email address doesn't look right, could you check it?\n");
			}
			if (!pass.equals(pass2)) {
				warningBuffer.append("The passwords do not match, try typing them again\n");
			}
			if (!(pass.length() > 10
					|| !(pass.matches("[a-z]") && pass.matches("[A-Z]") && pass.matches("[0-9]") && pass.matches("[ _+-=!@#$%^&*(),./;'<>?:{}|]")))) {
				warningBuffer.append("Passwords must be greater than 10 characters or contain at least one each of lowercase letters, upper case letters, number, and symbols(_+-=!@#$%^&*,./;'<>?:{}|)\n");
			}
			User example = new User();
			example.queryUsername().permittedValuesIgnoreCase(username);
			Long count = getDatabase().getDBTable(example).count();
			if (warningBuffer.length() > 0) {
				MinorTask.error("Secure password required", warningBuffer.toString());
			} else {
				if (count > 0) {
					MinorTask.error("You're unique", "Sorry, that username is already taken, please try another one");
				} else {
					try {
						newUser.setUsername(username);
						newUser.setPassword(pass);
						newUser.setEmail(email);
						newUser.setSignupDate(new Date());
						getDatabase().insert(newUser);
						MinorTask.chat("Welcome to Minor Task @" + username);
						minortask().loginAs(newUser, pass, REMEMBER_ME_FIELD.getValue());
						if (minortask().isLoggedIn()) {
							showDestination();
						}
					} catch (MinorTask.UnknownUserException | IncorrectPasswordException ex) {
						MinorTask.warning("Login Error", "Name and/or password do not match any known combination");
					} catch (MinorTask.TooManyUsersException ex) {
						MinorTask.warning("Login Error", "There is something odd with this login, please contact MinorTask about this issue");
					}
				}
			}
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void showDestination() {
		if (destination != null) {
			Globals.showLocation(destination);
		}else{
			Globals.showOpeningPage();
		}
	}

	public void handleEscapeButton() {
		MinorTask.showLogin(USERNAME_FIELD.getValue(), PASSWORD_FIELD.getValue());
	}

	public final void setAsDefaultButton(Button button) {
//		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
//		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener((event) -> {
			handleDefaultButton();
		});
	}

	public final void setEscapeButton(Button button) {
//		button.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
		button.addClickListener((event) -> {
			handleEscapeButton();
		});
	}

	public void setUsername(String parameter) {
		this.USERNAME_FIELD.setValue(parameter == null ? "" : parameter);
	}
}
