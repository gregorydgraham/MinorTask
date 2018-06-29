/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.ui.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public abstract class AuthorisedComponent extends MinorTaskComponent {

	Long currentTaskID = null;

	public AuthorisedComponent(MinorTaskUI ui, Long currentTask) {
		super(ui);
		this.currentTaskID = currentTask;
	}

	public abstract Component getAuthorisedComponent();

	@Override
	public final Component getComponent() {
		if (!authorised()) {
			return new LoginComponent(ui).getComponent();
		} else {
			Component banner = createBanner();
			Component footer = createFooter();

			GridLayout vlayout = new GridLayout(1, 2);
			vlayout.addComponent(banner);
			vlayout.setComponentAlignment(banner, Alignment.TOP_RIGHT);
			vlayout.addComponent(getAuthorisedComponent());

			vlayout.addComponent(footer);
			return vlayout;
		}
	}

	private boolean authorised() {
		User example = new User();
		example.queryUserID().permittedValues(getUserID());
		try {
			final DBDatabase database = getDatabase();
			User user = database.getDBTable(example).getOnlyRow();
			user.setLastLoginDate(new Date());
			database.update(user);
		} catch (SQLException|UnexpectedNumberOfRowsException ex) {
			Logger.getLogger(AuthorisedComponent.class.getName()).log(Level.SEVERE, null, ex);
			sqlerror(ex);
			return false;
		}
		return !notLoggedIn();
	}

	@Override
	public void handleDefaultButton() {
		new TaskCreationComponent(ui, currentTaskID).show();
	}

	@Override
	public void handleEscapeButton() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private Component createBanner() {
		HorizontalLayout banner = new HorizontalLayout();
		banner.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
		final Button createTaskButton = new Button("New");
		setAsDefaultButton(createTaskButton);
		final Button showTasks = new Button("Top List");
		showTasks.addClickListener((event) -> {
			new TaskListComponent(ui, null, ui.getTaskExampleForTaskID(null)).show();
		});
		banner.addComponents(createTaskButton, showTasks);
		final long userID = getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			final DBTable<User> userTable = getDatabase().getDBTable(example);
			User user = userTable.getOnlyRow();
			banner.addComponent(new Label("Welcome to MinorTask @" + user.getUsername()));
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
			sqlerror(ex);
		}
		banner.addComponent(ui.LOGOUT_BUTTON);
		return banner;
	}

	private Component createFooter() {
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label("MinorTask is a simple system to help you manage projects and tasks."));
		layout.addComponent(new Label("The key concept is that every project is just a collection of minor tasks."));
		layout.addComponent(new Label("MinorTask provides you the tools to break all your tasks and projects into their component minor tasks and complete the successfully."));
		return layout;
	}

}
