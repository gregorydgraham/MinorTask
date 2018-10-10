/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.sql.SQLException;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class UserProfileComponent extends VerticalLayout implements RequiresLogin {


	public UserProfileComponent() {
		User user = minortask().getUser();
		add(new Label("Still working on this bit..."));
//		final DBRowForm<User> form = new DBRowForm<>(user); 
//		add(form);
//
//		form.addValueChangeListener((event) -> {
//			saveRow(event.getValue());
//		});
	}

	private void saveRow(User user) {
		try {
			getDatabase().update(user);
			chat("Saved");
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

}
