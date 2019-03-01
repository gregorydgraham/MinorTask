/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBReport;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBNumber;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.dbvolution.exceptions.NoAvailableDatabaseException;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.images.SizedImageFromDocument;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.pages.UserProfilePage;

/**
 *
 * @author gregorygraham
 */
@Tag("authorised-banner")
@StyleSheet("styles/authorised-banner.css")
public class AuthorisedBannerMenu extends SecureDiv implements HasText {

	final Anchor welcomeMessage = new Anchor(Globals.getApplicationURL(), "Welcome");
	ColleaguesButton colleaguesButton = new ColleaguesButton();
	ProfileButton profileButton = new ProfileButton();
	LogoutButton logoutButton = new LogoutButton();

	public AuthorisedBannerMenu() {
		super();
		buildComponent();
		this.addClassName("authorised-banner");
		this.setId(getStaticID());
	}

	@Override
	public final void setId(String id) {
		super.setId(id);
	}

	public final void buildComponent() {
		setSizeUndefined();

		welcomeMessage.addClassName("welcome-message");

		setText("" + Globals.getApplicationName());

		final User user = getCurrentUser();
		if (user != null) {

			Label counts = new Label("Tasks: ??/##");
			TaskCounts taskCounts = new TaskCounts();
			try {
				final Task.Owner owner = new Task.Owner();
				owner.queryUserID().permittedValues(user.getUserID());
				List<TaskCounts> got = getDatabase().get(taskCounts, owner);
				final TaskCounts gotFirst = got.get(0);
				counts = new Label("Velocity: "+gotFirst.velocity.stringValue()+" Tasks: " + gotFirst.completed.stringValue() + "/" + gotFirst.created.stringValue());
			} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException | NoAvailableDatabaseException ex) {
				sqlerror(ex);
			}

			SecureDiv defaultImageDiv = new SecureDiv();
			defaultImageDiv.addClickListener((event) -> {
				minortask().showProfile();
			});
			Component profileImageDiv = defaultImageDiv;

			if (user.profileImage != null) {
				SizedImageFromDocument image = new SizedImageFromDocument(user.profileImage, 100);
				image.addClickListener((event) -> {
					minortask().showProfile();
				});
				profileImageDiv = image;
			}

			profileImageDiv.setId("authorised-banner-profile-image");

			final String welcomeUser = "@" + user.getUsername();
			Anchor profileAnchor = new Anchor(UserProfilePage.getURL(), welcomeUser);

			colleaguesButton.setId("authorised-banner-colleagues-button");
			colleaguesButton.addClassName("authorised-banner-button");

			profileButton.setId("authorised-banner-profile-button");
			profileButton.addClassName("authorised-banner-button");

			logoutButton.setId("authorised-banner-logout-button");
			logoutButton.addClassName("authorised-banner-button");

			Div left = new Div();
			left.addClassName("authorised-banner-left");

			Div right = new Div();
			right.addClassName("authorised-banner-right");

			left.add(profileImageDiv, welcomeMessage);
			right.add(new Div(new Div(profileAnchor, colleaguesButton, profileButton, logoutButton), counts));
			add(left, right);
		}
	}

	public static String getStaticID() {
		return "authorised_banner_id";
	}

	@Override
	public String getText() {
		return welcomeMessage.getText();
	}

	@Override
	public void setText(String text) {
		welcomeMessage.setText(text);
	}

	public void setAllButtonsUnselected() {
		profileButton.removeClassName("authorised-banner-selected-button");
		logoutButton.removeClassName("authorised-banner-selected-button");
	}

	public void setProfileButtonSelected() {
		profileButton.addClassName("authorised-banner-selected-button");
	}

	public void setLogoutButtonSelected() {
		logoutButton.addClassName("authorised-banner-selected-button");
	}

	public static class TaskCounts extends DBReport {

		Task.Owner user = new Task.Owner();
		Task task = new Task();

		@DBColumn
		public DBInteger created = new DBInteger(task.column(task.taskID).count());

		@DBColumn
		public DBInteger completed = new DBInteger(
				task.column(task.completionDate)
						.isNotNull()
						.ifThenElse(1, 0)
						.sum()
		);

		@DBColumn
		public DBNumber velocity = new DBNumber(
				task.column(task.completionDate)
						.isNotNull()
						.and(
								task.column(task.completionDate)
										.isGreaterThanOrEqual(DateExpression.currentDate().addDays(-30))
						)
						.ifThenElse(1, 0)
						.sum()
						.dividedBy(30)
		);
	}

}
