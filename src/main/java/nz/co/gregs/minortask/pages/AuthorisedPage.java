/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import java.sql.SQLException;
import nz.co.gregs.minortask.MinorTaskUI;
import static nz.co.gregs.minortask.MinorTaskUI.database;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public abstract class AuthorisedPage extends MinorTaskPage {
	
	public AuthorisedPage(MinorTaskUI ui) {
		super(ui);
	}
	
	@Override
	void show(AbstractLayout sublayout) {
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(sublayout);
		
		layout.addComponent(ui.LOGOUT_BUTTON);
		final long userID = ui.getUserID();
		User example = new User();
		example.userID.permittedValues(userID);
		try {
			User user = database.get(example).get(0);
			layout.addComponent(new Link("Go to your tasks " + user.username.getValue(), new ExternalResource("tasks")));
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		super.show(layout);
	}
	
}
