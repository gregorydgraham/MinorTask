/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
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
public class AuthorisedBannerMenu extends HorizontalLayout implements RequiresLogin {

	private final Long taskID;

	public AuthorisedBannerMenu(Long taskID) {
		this.taskID = taskID;
		buildComponent();

		this.getElement().setAttribute("theme", "success primary");
		this.addClassName("authorised-banner");
	}

	public final void buildComponent() {
		setSizeUndefined();
		setWidth("100%");
		setDefaultVerticalComponentAlignment(Alignment.START);
		
		final Div spacer1 = new Div();
		spacer1.setWidth("40%");
		add(spacer1);
		
		final long userID = minortask().getUserID();
		User example = new User();
		example.queryUserID().permittedValues(userID);
		try {
			final DBTable<User> userTable = getDatabase().getDBTable(example);
			User user = userTable.getOnlyRow();
			final Label label = new Label("Welcome to MinorTask @" + user.getUsername());
			label.setWidth("90%");
			add(label);
			setVerticalComponentAlignment(Alignment.CENTER, label);
		} catch (UnexpectedNumberOfRowsException | SQLException ex) {
			minortask().sqlerror(ex);
		}

		final Div spacer2 = new Div();
		spacer2.setWidth("40%");
		add(spacer2);
		
		Button logoutButton = new Button("Logout");
//		logoutButton.setSizeUndefined();
		logoutButton.addClickListener((event) -> {
			minortask().logout();
		});

		add(logoutButton);
		setVerticalComponentAlignment(Alignment.END, logoutButton);
	}

}
