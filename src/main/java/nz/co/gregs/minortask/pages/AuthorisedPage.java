/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
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

	public AuthorisedPage(MinorTaskUI ui) {
		super(ui);
	}

	@Override
	void show(AbstractLayout sublayout) {
		if (authorised()) {
			VerticalLayout layout = new VerticalLayout();
			layout.addComponent(sublayout);

			layout.addComponent(ui.LOGOUT_BUTTON);
			final long userID = getUserID();
			User example = new User();
			example.userID.permittedValues(userID);
			try {
				final DBTable<User> userTable = getDatabase().getDBTable(example);
				User user = userTable.getOnlyRow();
				layout.addComponent(new Label("Welcome to MinorTask " + user.username.getValue()));
			} catch (UnexpectedNumberOfRowsException | SQLException ex) {
				sqlerror(ex);
			}
			super.show(layout);
		}
	}

	private boolean authorised() {
		if (notLoggedIn()) {
			ui.LOGIN.show();
			return false;
		}
		return true;
	}

}
