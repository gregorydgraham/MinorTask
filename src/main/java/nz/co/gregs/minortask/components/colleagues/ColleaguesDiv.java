/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.colleagues;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H2;
import nz.co.gregs.minortask.components.colleagues.ColleagueList;
import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@Tag("colleagues")
@StyleSheet("styles/colleagues-page.css")
public class ColleaguesDiv extends SecureDiv {

	private final H2 greeting = new H2();
	private final ColleagueList colleagueList = new ColleagueList();

	public ColleaguesDiv() {
		setValues();
		setLabels();
		setStyles();
		addListeners();
		setId("colleagues-contents");
		add(greeting, colleagueList);
		colleagueList.refreshList();
	}

	private void setValues() {
	}

	private void setLabels() {
		User user = minortask().getCurrentUser();
		if (user != null && user.getUsername() != null) {
			greeting.setText("@" + user.getUsername() + "'s Team");
		} else {
			greeting.setText("Nobody's Team");
		}
	}

	private void setStyles() {
		greeting.setId("user-colleagues-greeting");
	}

	private void addListeners() {
	}

	public void refresh() {
		setLabels();
		setValues();
		colleagueList.refreshList();
	}

}
