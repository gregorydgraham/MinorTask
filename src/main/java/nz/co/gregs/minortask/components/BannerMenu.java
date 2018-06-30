/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.dbvolution.exceptions.UnexpectedNumberOfRowsException;
import nz.co.gregs.minortask.MinorTaskUI;
import nz.co.gregs.minortask.datamodel.TaskWithSortColumns;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class BannerMenu extends CustomComponent {

	private final MinorTaskUI ui;
	private final Long taskID;

	public BannerMenu(MinorTaskUI ui, Long taskID) {
		this.ui = ui;
		this.taskID = taskID;
		this.setWidth(100, Unit.PERCENTAGE);
		this.setHeightUndefined();
		HorizontalLayout banner = new HorizontalLayout();
		banner.setWidth(100, Unit.PERCENTAGE);
		banner.setHeightUndefined();
		banner.setDefaultComponentAlignment(Alignment.TOP_RIGHT);
		final Button createTaskButton = new Button("New");
		setAsDefaultButton(createTaskButton);
		final Button showTasks = new Button("Top List");
		showTasks.addClickListener((event) -> {
			ui.showTask(null);
		});
		banner.addComponents(createTaskButton, showTasks);
		final long userID = ui.getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			final DBTable<User> userTable = ui.getDatabase().getDBTable(example);
			User user = userTable.getOnlyRow();
			banner.addComponent(new Label("Welcome to MinorTask @" + user.getUsername()));
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
			ui.sqlerror(ex);
		}
		banner.addComponent(ui.LOGOUT_BUTTON);
		
		this.setCompositionRoot(banner);
	}
	public final void setAsDefaultButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener((event) -> {
			handleDefaultButton();
		});
	}
	public void handleDefaultButton() {
		ui.showTaskCreation(taskID);
//		new TaskCreationComponent(ui, taskID).show();
	}


}
