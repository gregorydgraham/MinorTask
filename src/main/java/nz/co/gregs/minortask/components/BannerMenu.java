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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class BannerMenu extends MinorTaskComponent {

	public BannerMenu(MinorTask minortask, Long taskID) {
		super(minortask, taskID);
		Component banner = getComponent();

		this.setCompositionRoot(banner);
		this.setWidth(100, Unit.PERCENTAGE);
		this.setHeightUndefined();
		this.addStyleName("banner");
	}

	@Override
	public final void setWidth(float width, Unit unit) {
		super.setWidth(width, unit);
	}

	@Override
	public final void setHeightUndefined() {
		super.setHeightUndefined();
	}

	@Override
	public final void addStyleName(String style) {
		super.addStyleName(style);
	}

	final protected Component getComponent() {
		HorizontalLayout banner = new HorizontalLayout();
		banner.setWidth(100, Unit.PERCENTAGE);
		banner.setHeightUndefined();
		banner.setDefaultComponentAlignment(Alignment.TOP_RIGHT);

		final long userID = minortask().getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			final DBTable<User> userTable = getDatabase().getDBTable(example);
			User user = userTable.getOnlyRow();
			final Label label = new Label("Welcome to MinorTask @" + user.getUsername());
			label.setWidth(100, Unit.PERCENTAGE);
			banner.addComponent(label);
			banner.setComponentAlignment(label, Alignment.TOP_CENTER);
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
			minortask.sqlerror(ex);
		}
		
		Button logoutButton = new Button("Logout");
		logoutButton.addClickListener((event) -> {
			minortask().logout();
		});
		
		banner.addComponent(logoutButton);
		banner.setComponentAlignment(logoutButton, Alignment.TOP_RIGHT);

		return banner;
	}

	public final void setAsDefaultButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener((event) -> {
			handleDefaultButton();
		});
	}

	public void handleDefaultButton() {
		minortask().showTaskCreation(getTaskID());
	}

}
