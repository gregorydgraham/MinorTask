/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.PasswordResetRequests;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class LostPasswordComponent extends VerticalLayout implements MinorTaskComponent {

	public final TextField USERNAME_FIELD = new TextField("Your Name");

	public LostPasswordComponent() {
		this("");
	}

	public LostPasswordComponent(String username) {
		USERNAME_FIELD.setValue(username);
		add(getComponent());
	}

	private Component getComponent() {
		VerticalLayout layout = new VerticalLayout();

		USERNAME_FIELD.setRequiredIndicatorVisible(true);
		USERNAME_FIELD.focus();

		Button resetPassword = new Button("Reset My Password Please");
		setAsDefaultButton(resetPassword);

		Button returnToLoginButton = new Button("Return to Login");
		setEscapeButton(returnToLoginButton);

		HorizontalLayout buttonLayout = new HorizontalLayout(returnToLoginButton, resetPassword);
		layout.add(USERNAME_FIELD, buttonLayout);

		return layout;
	}

	public void handleDefaultButton() {
		try {
			final String username = USERNAME_FIELD.getValue().trim();
			final StringBuffer warningBuffer = new StringBuffer();
			minortask().chat(username);
			if (username.isEmpty()) {
				warningBuffer.append("Blank names are not allowed\n");
			}
			if (username.contains(" ")) {
				warningBuffer.append("Usernames may not contain spaces\n");
			}
			if (warningBuffer.length() > 0) {
				minortask().error("Please Check This", warningBuffer.toString());
			} else {
				User example = new User();
				example.queryUsername().permittedValuesIgnoreCase(username);
				List<User> users = getDatabase().get(example);
				int count = users.size();
				if (count == 0) {
					minortask().error("Are You Sure?", "Sorry, that username hasn't been found");
				} else {
					User user = users.get(0);
					String email = user.getEmail();
					if (email == null || email.isEmpty()) {
						minortask().error("Can't Reach You", "No email has been set for this user, so we can't send you any messages");
					} else {
						PasswordResetRequests request = makeResetRequest(user);
						getDatabase().insert(request);
						sendResetRequestMessage(user, request);
						minortask().showLogin(user.getUsername(), "");
					}
				}
			}
		} catch (Exception ex) {
			error("Email Server", ex.getLocalizedMessage());
		}
	}

	private PasswordResetRequests makeResetRequest(User user) throws SQLException {
		String resetCode = MinorTask.getRandomID();

		Calendar cal = GregorianCalendar.getInstance();
		cal.add(GregorianCalendar.HOUR_OF_DAY, 24);
		Date expiryTime = cal.getTime();

		PasswordResetRequests request = new PasswordResetRequests();
		request.userId.setValue(user.getUserID());
		request.expiryTime.setValue(expiryTime);
		request.resetCode.setValue(resetCode);

		return request;
	}

	private void sendResetRequestMessage(User user, PasswordResetRequests request) throws MessagingException {
		try {
			Message message = minortask().getEmailMessageToSend();

			message.setRecipients(
					Message.RecipientType.TO,
					InternetAddress.parse(user.getEmail())
			);
			message.setSubject("Resert Password To " + minortask().getApplicationName());

			String msg = "Hi " + user.getUsername() + "\n\n"
					+ "A password reset has been requested, follow the link below to change your password.\n\n"
					+ minortask().getApplicationURL() + "/resetpassword/" + request.resetCode.getValue() + "\n\n"
					+ "Your password has not been changed, you can continue to use it.  Follow the instructions at the website to change your password.";

			message.setText(msg);

			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg.replaceAll("\n\n", "<p>"), "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);

			message.setContent(multipart);

			chat("Sending \"" + message.getSubject() + "\" to " + user.getEmail());

			Transport.send(message);
		} catch (Exception ex) {
			error("Email Issue", ex.getLocalizedMessage());
		}
	}

	public void handleEscapeButton() {
		minortask().showLogin(USERNAME_FIELD.getValue(), "");
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

	public void setUsername(String parameter) {
		this.USERNAME_FIELD.setValue(parameter == null ? "" : parameter);
	}
}
