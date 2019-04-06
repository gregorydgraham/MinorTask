/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import nz.co.gregs.minortask.components.colleagues.ColleaguesDiv;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveListener;
import com.vaadin.flow.router.Route;

/**
 *
 * @author gregorygraham
 */
//@Route(value = "colleagues", layout = MinortaskPage.class)
//@Tag("colleagues")
@StyleSheet("styles/colleagues-page.css")
public class ColleaguesPage extends AuthorisedPage implements BeforeLeaveListener {

	public ColleaguesPage() {
		super();
		banner.setColleaguesButtonSelected();
	}

	@Override
	protected Component getInternalComponent() {
		return new ColleaguesDiv();
//		setValues();
//		setLabels();
//		setStyles();
//		addListeners();
//
//		ColleaguesDiv component = new ColleaguesDiv();
//		component.setId("colleagues-contents");
//		component.add(
//				greeting,
//				colleagueList
//		);
//
//		return component;
	}

	@Override
	public String getPageTitle() {
		if (minortask().isLoggedIn()) {
			return getCurrentUser().getUsername() + "'s Team";
		}
		return "Team Page";
	}

//	private void addListeners() {
//
//	}

//	private void setValues() {
//	}

//	private void setLabels() {
//		User user = minortask().getCurrentUser();
//		greeting.setText("@" + user.getUsername() + "'s Team");
//	}

//	private void setStyles() {
//		banner.setProfileButtonSelected();
//		greeting.setId("user-colleagues-greeting");
//	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		banner.setAllButtonsUnselected();
	}

}
