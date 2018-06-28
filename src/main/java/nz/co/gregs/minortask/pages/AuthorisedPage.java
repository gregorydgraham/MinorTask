/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public abstract class AuthorisedPage extends MinorTaskPage {

	Long currentTask = null;

	public AuthorisedPage(MinorTaskUI ui, Long currentTask) {
		super(ui);
		this.currentTask = currentTask;
	}

	@Override
	void show(AbstractLayout sublayout) {
		if (!authorised()) {
			new LoginPage(ui).show();
		} else {
			Component banner = createBanner();
			Component footer = createFooter();

			GridLayout vlayout = new GridLayout(1, 2);
			vlayout.addComponent(banner);
			vlayout.setComponentAlignment(banner, Alignment.TOP_RIGHT);
			vlayout.addComponent(sublayout);

			vlayout.addComponent(footer);

			super.show(vlayout);
		}
	}

	public Component createBanner() {
		HorizontalLayout banner = new HorizontalLayout();
		banner.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
		final Button createTaskButton = new Button("New");
		setAsDefaultButton(createTaskButton);
		final Button showTasks = new Button("Top List");
		showTasks.addClickListener((event) -> {
			new TaskListPage(ui, null).show();
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

	private boolean authorised() {
		return !notLoggedIn();
	}

	@Override
	public void handleDefaultButton() {
		new TaskCreationPage(ui, currentTask).show();
	}

	@Override
	public void handleEscapeButton() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private Component createFooter() {
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label("MinorTask is a simple system to help you manage projects and tasks."));
		layout.addComponent(new Label("The key concept is that every project is just a collection of minor tasks."));
		layout.addComponent(new Label("MinorTask provides you the tools to break all your tasks and projects into their component minor tasks and complete the successfully."));
		return layout;
	}

}
