/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.Location;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.Cookie;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.exceptions.IncorrectPasswordException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.upload.Document;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class LoginComponent extends VerticalLayout implements MinorTaskComponent, HasComponents, KeyNotifier, HasDefaultButton {

	private TextField USERNAME_FIELD;
	private PasswordField PASSWORD_FIELD;
	private final Checkbox REMEMBER_ME_FIELD = new Checkbox("Remember Me", true);
	private Location destination;
	private Runnable run;

	public LoginComponent() {
		this("", "");
	}

	public LoginComponent(String username, String password) {
		super();
		addClassName("minortask-login");
		USERNAME_FIELD = new TextField("Your Name");
		PASSWORD_FIELD = new PasswordField("Password");
		USERNAME_FIELD.setValue(username);
		PASSWORD_FIELD.setValue(password);
		setSizeUndefined();

		add(getComponent());

		Optional<Cookie> cookie = Globals.getLastUsernameCookieValue();
		if (cookie.isPresent()) {
			System.out.println("nz.co.gregs.minortask.components.LoginComponent.getComponent(): " + cookie.get().getValue());
			USERNAME_FIELD.setValue(cookie.get().getValue());
		} else {
			System.out.println("NO LAST USER FOUND");
		}
	}

	private Component getComponent() {
		VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.addClassName("login-panel");

		Button loginButton = new Button("Login");
		loginButton.addClassName("login-button");
		loginButton.addClickListener((event) -> {
			handleDefaultButton();
		});
		setAsDefaultButton(loginButton, (event) -> {
			handleDefaultButton();
		});

		Button signupButton = new Button("Sign Up");
		signupButton.addClickListener((event) -> {
			MinorTask.showSignUp(USERNAME_FIELD.getValue(), PASSWORD_FIELD.getValue());
		});

		Button lostPasswordButton = new Button("Lost Password?");
		lostPasswordButton.addClickListener((event) -> {
			MinorTask.showLostPassword(USERNAME_FIELD.getValue());
		});

		HorizontalLayout LoginButtons = new HorizontalLayout(REMEMBER_ME_FIELD, loginButton);

		HorizontalLayout buttons = new HorizontalLayout(lostPasswordButton, signupButton);

		USERNAME_FIELD.setRequiredIndicatorVisible(true);
		PASSWORD_FIELD.setRequiredIndicatorVisible(true);

		loginPanel.add(
				USERNAME_FIELD,
				PASSWORD_FIELD,
				LoginButtons,
				buttons);

		return loginPanel;
	}

	public void handleDefaultButton() {
		System.out.println("nz.co.gregs.minortask.components.LoginComponent.handleDefaultButton()");
		StringBuilder warningBuffer = new StringBuilder();
		final String username = USERNAME_FIELD.getValue().trim();
		final String password = PASSWORD_FIELD.getValue();
		final Boolean rememberMeValue = REMEMBER_ME_FIELD.getValue();
		if (username.isEmpty() || password.isEmpty()) {
			warningBuffer.append("Name and/or password needs to be entered\n");
		} else {
			User example = new User();
			example.queryUsername().permittedValuesIgnoreCase(username);
			try {
				final DBDatabase database = MinorTask.getDatabase();
				final DBQuery query = database.getDBQuery(example).addOptional(new Document());
				System.out.println("" + query.getSQLForQuery());
				List<User> users = query.getAllInstancesOf(example);
				switch (users.size()) {
					case 1:
						User user = users.get(0);
						System.out.println("User: " + user);
						minortask().loginAs(user, password, rememberMeValue);
						if (minortask().isLoggedIn()) {
							showDestination();
						}
						break;
					case 0:
						throw new MinorTask.UnknownUserException();
					default:
						MinorTask.warning("Login Error", "There is something odd with this login, please contact MinorTask about this issue");
						break;
				}
			} catch (SQLException ex) {
				warningBuffer.append("SQL FAILED: ").append(ex.getLocalizedMessage());
			} catch (MinorTask.UnknownUserException | IncorrectPasswordException ex) {
				MinorTask.warning("Login Error", "Name and/or password do not match any known combination");
			} catch (MinorTask.TooManyUsersException ex) {
				MinorTask.warning("Login Error", "There is something odd with this login, please contact MinorTask about this issue");
			}
		}
		if (warningBuffer.length() > 0) {
			MinorTask.warning("Login error", warningBuffer.toString());
		}
	}

	public void setUsername(String parameter) {
		this.USERNAME_FIELD.setValue(parameter == null ? "" : parameter);
		PASSWORD_FIELD.focus();
	}

	public void setDestination(Location location) {
		destination = location;
	}

	private void showDestination() {
		if (run != null) {
			run.run();
		}
		if (destination != null) {
			Globals.showLocation(destination);
		}
	}

	public void setLoginMethod(Runnable run) {
		this.run = run;
	}
}
