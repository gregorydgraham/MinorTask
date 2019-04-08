/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.banner;

import nz.co.gregs.minortask.components.generic.SecureDiv;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import java.sql.SQLException;
import java.util.List;
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
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;
import nz.co.gregs.minortask.MinorTaskViews;

/**
 *
 * @author gregorygraham
 */
@Tag("authorised-banner")
@StyleSheet("styles/authorised-banner.css")
public class AuthorisedBannerMenu extends SecureDiv implements HasText, MinorTaskEventListener, MinorTaskEventNotifier {

	final Anchor welcomeMessage = new Anchor(Globals.getApplicationURL(), "Welcome");
	final QuickLinks quickLinks = new QuickLinks();
	final UserLinks userLinks = new UserLinks();

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

		quickLinks.addMinorTaskEventListener(this);
		userLinks.addMinorTaskEventListener(this);

		setText("" + Globals.getApplicationName());

		final User user = getCurrentUser();
		if (user != null) {

			Label counts = new Label("Tasks: ??/##");
			OwnerStatistics taskCounts = new OwnerStatistics();
			try {
				final Task.Owner owner = new Task.Owner();
				owner.queryUserID().permittedValues(user.getUserID());
				List<OwnerStatistics> got = getDatabase().get(taskCounts, owner);
				final OwnerStatistics gotFirst = got.get(0);
				counts = new Label("Velocity: " + gotFirst.velocity.stringValue() + " Tasks: " + gotFirst.completed.stringValue() + "/" + gotFirst.created.stringValue());
			} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException | NoAvailableDatabaseException ex) {
				sqlerror(ex);
			}

			SecureDiv defaultImageDiv = new SecureDiv();
			defaultImageDiv.addClickListener((event) -> {
				fireEvent(new MinorTaskEvent(event.getSource(), MinorTaskViews.PROFILE, true));
			});
			Component profileImageDiv = defaultImageDiv;

			if (user.profileImage != null) {
				SizedImageFromDocument image = new SizedImageFromDocument(user.profileImage, 100);
				image.addClickListener((event) -> {
					fireEvent(new MinorTaskEvent(event.getSource(), MinorTaskViews.PROFILE, true));
				});
				profileImageDiv = image;
			}

			profileImageDiv.setId("authorised-banner-profile-image");

			Div left = new Div();
			left.addClassName("authorised-banner-left");

			Div right = new Div();
			right.addClassName("authorised-banner-right");

			left.add(profileImageDiv, quickLinks);
			Div centre = new Div(welcomeMessage);
			right.add(userLinks, counts);
			add(left, centre, right);
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
		userLinks.setAllButtonsUnselected();
	}

	public void setProfileButtonSelected() {
		userLinks.setProfileButtonSelected();
	}

	public void setColleaguesButtonSelected() {
		userLinks.setColleaguesButtonSelected();
	}

	public void setLogoutButtonSelected() {
		userLinks.setLogoutButtonSelected();
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		fireEvent(event);
	}

	public static class OwnerStatistics extends DBReport {

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
						.round(2)
		);
	}

}
