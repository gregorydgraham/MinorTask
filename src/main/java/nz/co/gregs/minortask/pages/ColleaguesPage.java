/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveListener;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.components.ColleagueList;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@Route(value = "colleagues", layout = MinortaskPage.class)
@Tag("colleagues")
@StyleSheet("styles/colleagues-page.css")
public class ColleaguesPage extends AuthorisedPage implements BeforeLeaveListener {

	private final H2 greeting = new H2();
	private final ColleagueList colleagueList = new ColleagueList();

	public ColleaguesPage() {
		super();
	}

	@Override
	protected Component getInternalComponent() {
		setValues();
		setLabels();
		setStyles();
		addListeners();

		Div component = new Div();
		component.setId("colleagues-contents");
		component.add(
				greeting,
				colleagueList
		);

		return component;
	}

	@Override
	public String getPageTitle() {
		if (minortask().isLoggedIn()) {
			return "Colleagues Of @" + getUser().getUsername();
		}
		return "Colleagues Page";
	}

	private void addListeners() {
		
	}

	private void setValues() {
	}

	private void setLabels() {
		User user = minortask().getUser();
		greeting.setText("@" + user.getUsername() + "'s Team");
	}

	private void setStyles() {
		banner.setProfileButtonSelected();
		greeting.setId("user-colleagues-greeting");
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		banner.setAllButtonsUnselected();
	}
}
