/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class LoginComponent extends PublicComponent {

	private final TextField USERNAME_FIELD = new TextField("Your Name");
	private final PasswordField PASSWORD_FIELD = new PasswordField("Password");

	public LoginComponent(MinorTask minortask) {
		this(minortask, "", "");
	}
	public LoginComponent(MinorTask minortask, String username, String password) {
		super(minortask);
		setCompositionRoot(getComponent());
		USERNAME_FIELD.setValue(username);
		PASSWORD_FIELD.setValue(password);
	}

	private Component getComponent() {
		VerticalLayout loginPanel = new VerticalLayout();
		final Label welcomeLabel = new Label("Welcome To MinorTask");
		welcomeLabel.setWidthUndefined();
		welcomeLabel.addStyleName("huge");
		loginPanel.addComponent(welcomeLabel);
		loginPanel.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		Button loginButton = new Button("Login");
		setAsDefaultButton(loginButton);

		Button signupButton = new Button("Sign Up");
		signupButton.addClickListener((Button.ClickEvent e) -> {
			minortask().showSignUp(USERNAME_FIELD.getValue(), PASSWORD_FIELD.getValue());
		});

		HorizontalLayout buttons = new HorizontalLayout(signupButton, loginButton);
		buttons.setComponentAlignment(loginButton, Alignment.TOP_RIGHT);

		USERNAME_FIELD.setRequiredIndicatorVisible(true);
		USERNAME_FIELD.setCursorPosition(0);
		PASSWORD_FIELD.setRequiredIndicatorVisible(true);

		loginPanel.addComponents(USERNAME_FIELD, PASSWORD_FIELD, buttons);
		return new GridLayout(
				3, 3,
				new VerticalLayout(), new VerticalLayout(), new VerticalLayout(),
				new VerticalLayout(), loginPanel, new VerticalLayout(),
				new VerticalLayout(), new VerticalLayout(), new VerticalLayout()
		);
	}

	public void handleDefaultButton() {
		StringBuilder warningBuffer = new StringBuilder();
		final String username = USERNAME_FIELD.getValue();
		final String password = PASSWORD_FIELD.getValue();
		if (username.isEmpty() || password.isEmpty()) {
			warningBuffer.append("Name and/or password needs to be entered\n");
		} else {
			User example = new User();
			example.queryUsername().permittedValuesIgnoreCase(username);
			example.queryPassword().permittedValues(password);
			try {
				final DBDatabase database = getDatabase();
//				database.setPrintSQLBeforeExecuting(true);
				final DBTable<User> query = database.getDBTable(example);
				List<User> users = query.getAllRows();
				switch (users.size()) {
					case 1:
						minortask().loginAs(users.get(0).getUserID());
						break;
					case 0:
						minortask.warning("Login Error", "Name and/or password do not match any known combination");
						break;
					default:
						minortask.warning("Login Error", "There is something odd with this login, please contact MinorTask about this issue");
						break;
				}
			} catch (SQLException ex) {
				warningBuffer.append("SQL FAILED: ").append(ex.getLocalizedMessage());
			}
		}
		if (warningBuffer.length() > 0) {
			minortask.warning("Login error", warningBuffer.toString());
		}
	}

	public void handleEscapeButton() {
		throw new UnsupportedOperationException("Not supported yet.");
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
