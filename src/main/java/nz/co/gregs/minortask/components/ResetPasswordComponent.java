/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.dbvolution.exceptions.IncorrectPasswordException;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.PasswordResetRequests;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class ResetPasswordComponent extends VerticalLayout implements MinorTaskComponent {

	public final Label USERNAME_LABEL = new Label("Unknown User");
	public final PasswordField PASSWORD_FIELD = new PasswordField("Password");
	public final PasswordField REPEAT_PASSWORD_FIELD = new PasswordField("Repeat Password");
	private User existingUser = new User();
	private Long userid;
	private String resetCode;

	public ResetPasswordComponent() {
		add(getComponent());
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();

		PASSWORD_FIELD.clear();
		REPEAT_PASSWORD_FIELD.clear();
		PASSWORD_FIELD.setRequiredIndicatorVisible(true);
		REPEAT_PASSWORD_FIELD.setRequiredIndicatorVisible(true);

		Button setPasswordButton = new Button("Set Password");
		setAsDefaultButton(setPasswordButton);

		Button returnToLoginButton = new Button("Return to Login");
		setEscapeButton(returnToLoginButton);

		HorizontalLayout buttonLayout = new HorizontalLayout(returnToLoginButton, setPasswordButton);
		layout.add(USERNAME_LABEL, new HorizontalLayout(PASSWORD_FIELD, REPEAT_PASSWORD_FIELD), buttonLayout);

		return layout;
	}

	public void handleDefaultButton() {
		try {
			final String pass = PASSWORD_FIELD.getValue();
			final String pass2 = REPEAT_PASSWORD_FIELD.getValue();
			final StringBuffer warningBuffer = new StringBuffer();
			if (pass.isEmpty()) {
				warningBuffer.append("Blank names and passwords are not allowed\n");
			}
			if (!pass.equals(pass2)) {
				warningBuffer.append("The passwords do not match, try typing them again\n");
			}
			if (!(pass.length() > 10
					|| !(pass.matches("[a-z]") && pass.matches("[A-Z]") && pass.matches("[0-9]") && pass.matches("[ _+-=!@#$%^&*(),./;'<>?:{}|]")))) {
				warningBuffer.append("Passwords must be greater than 10 characters or contain at least one each of lowercase letters, upper case letters, number, and symbols(_+-=!@#$%^&*,./;'<>?:{}|)\n");
			}
			User example = new User();
			example.queryUserID().permittedValues(userid);
			Long count = getDatabase().getDBTable(example).count();
			if (count == 0) {
				minortask().error("No Such User", "Please check your username \"" + existingUser.getUsername() + "\" we couldn't find you.");
			} else if (count > 1) {
				minortask().error("Bad User Lookup", "There is a serious issue with your account, please contact MinorTask to fix the problem.");
			} else {
				if (warningBuffer.length() > 0) {
					minortask().error("Secure password required", warningBuffer.toString());
				} else {
					try {
						existingUser.setPassword(pass);
						existingUser.setSignupDate(new Date());
						getDatabase().update(existingUser);
						minortask().chat("Welcome to Minor Task @" + userid);
						minortask().loginAs(existingUser, pass, false);
					} catch (MinorTask.UnknownUserException | IncorrectPasswordException ex) {
						minortask().warning("Login Error", "Name and/or password do not match any known combination");
					} catch (MinorTask.TooManyUsersException ex) {
						minortask().warning("Login Error", "There is something odd with this login, please contact MinorTask about this issue");
					}
				}
			}
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public void handleEscapeButton() {
		minortask().showLogin(existingUser.getUsername(), PASSWORD_FIELD.getValue());
	}

	public final void setAsDefaultButton(Button button) {
		button.addClickListener((event) -> {
			handleDefaultButton();
		});
	}

	public final void setEscapeButton(Button button) {
		button.addClickListener((event) -> {
			handleEscapeButton();
		});
	}

	public void setUserid(Long parameter) {
		userid = (parameter == null ? 0 : parameter);
	}

	public void setResetCode(String parameter) throws NoSuchResetRequest {
		try {
			resetCode = parameter;
			System.out.println("RESET CODE: " + resetCode);
			USERNAME_LABEL.setText("Reset Code: " + resetCode);
			if (resetCode != null && !resetCode.isEmpty()) {
				PasswordResetRequests example = new PasswordResetRequests();
				example.resetCode.permittedValues(resetCode);
				example.expiryTime.permittedRange(new Date(),null);
				DBDatabase database = getDatabase();
				List<DBQueryRow> requestsFound = database.get(example, new User());
				if (requestsFound.size() == 1) {
					PasswordResetRequests actualRequest = requestsFound.get(0).get(example);
					User user = requestsFound.get(0).get(new User());
					if (actualRequest != null && user != null) {
						userid = user.getUserID();
						existingUser = user;
						USERNAME_LABEL.setText("Reset password " + existingUser.getUsername() + "?");
					}
				}else{
					throw new NoSuchResetRequest(parameter);
				}
			}
		} catch (SQLException | AccidentalBlankQueryException | AccidentalCartesianJoinException ex) {
			sqlerror(ex);
		}
	}

	public static class NoSuchResetRequest extends Exception {

		public NoSuchResetRequest(String parameter) {
			super("No valid reset request for "+parameter+" was found");
		}
	}
}
