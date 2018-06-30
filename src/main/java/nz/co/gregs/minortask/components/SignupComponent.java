/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.SQLException;
import java.util.Date;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class SignupComponent extends PublicComponent {

	public final PasswordField REPEAT_PASSWORD_FIELD = new PasswordField("Repeat Password");
	public final TextField EMAIL_FIELD = new TextField("Rescue Email Address");
	private final User newUser = new User();

	public SignupComponent(MinorTaskUI loginUI) {
		super(loginUI);
		setCompositionRoot(getComponent());
	}
	
	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();
		
		REPEAT_PASSWORD_FIELD.clear();
		Helper.USERNAME_FIELD.setRequiredIndicatorVisible(true);
		Helper.PASSWORD_FIELD.setRequiredIndicatorVisible(true);
		REPEAT_PASSWORD_FIELD.setRequiredIndicatorVisible(true);
		
		Button signupButton = new Button("Request Sign Up");
		setAsDefaultButton(signupButton);

		Button returnToLoginButton = new Button("Return to Login");
		setEscapeButton(returnToLoginButton);

		HorizontalLayout buttonLayout = new HorizontalLayout(returnToLoginButton, signupButton);
		layout.addComponents(Helper.USERNAME_FIELD, EMAIL_FIELD, new HorizontalLayout(Helper.PASSWORD_FIELD, REPEAT_PASSWORD_FIELD), buttonLayout);
		
		return layout;
	}

	public void handleDefaultButton() {
		final String username = Helper.USERNAME_FIELD.getValue();
		final String email = EMAIL_FIELD.getValue();
		final String pass = Helper.PASSWORD_FIELD.getValue();
		final String pass2 = REPEAT_PASSWORD_FIELD.getValue();
		final StringBuffer warningBuffer = new StringBuffer();
		Helper.chat(username);
		if (username.isEmpty() || pass.isEmpty()) {
			warningBuffer.append("Blank names and passwords are not allowed\n");
		}if (username.contains(" ")) {
			warningBuffer.append("Usernames may not contain spaces\n");
		}
		if (!pass.equals(pass2)) {
			warningBuffer.append("The passwords do not match, try typing them again\n");
		}
		if (!(pass.length() > 10 || (pass.matches("[a-z]") && pass.matches("[A-Z]") && pass.matches("[0-9]") && pass.matches("[ _+-=!@#$%^&*(),./;'<>?:{}|]")))) {
			warningBuffer.append("Passwords must be greater than 10 characters or contain at least one each of lowercase letters, upper case letters, number, and symbols(_+-=!@#$%^&*,./;'<>?:{}|)\n");
		}
		User example = new User();
		example.queryUsername().permittedValuesIgnoreCase(username);
		try {
			Long count = Helper.getDatabase().getDBTable(example).count();
			if (count > 0) {
				Helper.error("You're unique", "Sorry, that username is already taken, please try another one");
			}
		} catch (SQLException ex) {
			Helper.sqlerror(ex);
		}
		if (warningBuffer.length() > 0) {
			Helper.error("Secure password required", warningBuffer.toString());
		} else {
			try {
				newUser.setUsername(username);
				newUser.setPassword(pass);
				newUser.setEmail(email);
				newUser.setSignupDate(new Date());
				Helper.getDatabase().insert(newUser);
				Helper.chat("Welcome to Minor Task @" + username);
				new LoginPage(minortask()).handleDefaultButton();
			} catch (SQLException ex) {
				Helper.sqlerror(ex);
			}
		}
	}

	public void handleEscapeButton() {
		new LoginPage(minortask()).show();
	}

	public final void setAsDefaultButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener((event) -> {
			handleDefaultButton();
		});
	}

	public final void setEscapeButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
		button.addClickListener((event) -> {
			handleEscapeButton();
		});
	}
}
