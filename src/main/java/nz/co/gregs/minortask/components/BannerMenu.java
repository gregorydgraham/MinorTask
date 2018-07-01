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
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class BannerMenu extends MinorTaskComponent {

//	private final MinorTaskUI ui;
//	private final Long taskID;

	public BannerMenu(MinorTaskUI ui, Long taskID) {
		super(ui, taskID);
		Component banner = getComponent();

		this.setCompositionRoot(banner);
		this.setWidth(100, Unit.PERCENTAGE);
		this.setHeightUndefined();
	}

	protected Component getComponent() {
		HorizontalLayout banner = new HorizontalLayout();
		banner.setWidth(100, Unit.PERCENTAGE);
		banner.setHeightUndefined();
		banner.setDefaultComponentAlignment(Alignment.TOP_RIGHT);

		final Button createTaskButton = new Button("New");
		createTaskButton.addClickListener((event) -> {
			this.handleDefaultButton();
		});

		final Button showProjects = new Button("Projects");
		showProjects.addClickListener((event) -> {
			minortask().showTask(null);
		});
		final HorizontalLayout usefulButtons = new HorizontalLayout(createTaskButton, showProjects);
		banner.addComponents(usefulButtons);
		banner.setComponentAlignment(usefulButtons, Alignment.TOP_LEFT);

		final long userID = minortask().getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			final DBTable<User> userTable = Helper.getDatabase().getDBTable(example);
			User user = userTable.getOnlyRow();
			final Label label = new Label("Welcome to MinorTask @" + user.getUsername());
			label.setWidth(100, Unit.PERCENTAGE);
			banner.addComponent(label);
			banner.setComponentAlignment(label, Alignment.TOP_CENTER);
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
			Helper.sqlerror(ex);
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
//		new TaskCreationComponent(ui, taskID).show();
	}

}
