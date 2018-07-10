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
import java.sql.SQLException;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class BannerMenu extends HorizontalLayout implements HasMinorTask {

	private final Long taskID;

	public BannerMenu(Long taskID) {
		this.taskID = taskID;
		Component banner = getComponent();

		this.add(banner);
		this.setSizeUndefined();
		this.setWidth("100%");
		this.addClassName("banner");
	}

	final protected Component getComponent() {
		HorizontalLayout banner = new HorizontalLayout();
		banner.setSizeUndefined();
		banner.setWidth("100%");
		banner.setDefaultVerticalComponentAlignment(Alignment.START);

		final long userID = minortask().getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			final DBTable<User> userTable = getDatabase().getDBTable(example);
			User user = userTable.getOnlyRow();
			final Label label = new Label("Welcome to MinorTask @" + user.getUsername());
			label.setSizeFull();
			banner.add(label);
			banner.setVerticalComponentAlignment(Alignment.CENTER, label);
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
			minortask().sqlerror(ex);
		}

		Button logoutButton = new Button("Logout");
		logoutButton.setSizeUndefined();
		logoutButton.addClickListener((event) -> {
			minortask().logout();
		});

		banner.add(logoutButton);
		banner.setVerticalComponentAlignment(Alignment.START, logoutButton);

		return banner;
	}

	public final void setAsDefaultButton(Button button) {
//		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
//		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener((event) -> {
			handleDefaultButton();
		});
	}

	public void handleDefaultButton() {
		minortask().showTaskCreation(taskID);
	}

}
