/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import nz.co.gregs.minortask.Globals;

/**
 *
 * @author gregorygraham
 */
@Tag("quicklinks")
@StyleSheet("styles/quicklinks.css")
public class QuickLinks extends Span {

	public QuickLinks() {
		super();
		this.add(this.getComponents());
	}
	
	private Component[] getComponents() {
		IconWithToolTip search = new IconWithToolTip(VaadinIcon.SEARCH, "Search");
		search.addClickListener((event) -> {
			Globals.showSearchPage();
		});
		search.addClassName("navigator-task-search");

		IconWithToolTip today = new IconWithToolTip(VaadinIcon.TIMER, "Today's Tasks");
		today.addClickListener((event) -> {
			Globals.showTodaysTasks();
		});
		today.addClassName("navigator-task-today");

		IconWithToolTip recent = new IconWithToolTip(VaadinIcon.CLOCK, "Recently Viewed");
		recent.addClickListener((event) -> {
			Globals.showRecentsPage();
		});
		recent.addClassName("navigator-task-recents");

		IconWithToolTip favourites = new IconWithToolTip(VaadinIcon.HEART, "Favourited");
		favourites.addClickListener((event) -> {
			Globals.showFavouritesPage();
		});
		favourites.addClassName("navigator-task-favourites");

		return new Component[]{search, today, recent, favourites};
	}
}
