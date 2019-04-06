/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.banner;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import nz.co.gregs.minortask.MinorTaskViews;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
@Tag("quicklinks")
@StyleSheet("styles/quicklinks.css")
public class QuickLinks extends Span implements MinorTaskEventNotifier{

	public QuickLinks() {
		super();
		this.add(this.getComponents());
	}
	
	private Component[] getComponents() {
		IconWithToolTip search = new IconWithToolTip(VaadinIcon.SEARCH, "Search");
		search.addClickListener((event) -> {
			fireEvent(new MinorTaskEvent(search, MinorTaskViews.SEARCH, true));
		});
		search.addClassName("navigator-task-search");

		IconWithToolTip today = new IconWithToolTip(VaadinIcon.TIMER, "Today's Tasks");
		today.addClickListener((event) -> {
			fireEvent(new MinorTaskEvent(search, MinorTaskViews.TODAY, new Task(), true));
		});
		today.addClassName("navigator-task-today");

		IconWithToolTip recent = new IconWithToolTip(VaadinIcon.CLOCK, "Recently Viewed");
		recent.addClickListener((event) -> {
			fireEvent(new MinorTaskEvent(search, MinorTaskViews.RECENT, new Task(), true));
		});
		recent.addClassName("navigator-task-recents");

		IconWithToolTip favourites = new IconWithToolTip(VaadinIcon.HEART, "Favourited");
		favourites.addClickListener((event) -> {
			fireEvent(new MinorTaskEvent(search, MinorTaskViews.FAVOURITES, new Task(), true));
		});
		favourites.addClassName("navigator-task-favourites");

		return new Component[]{search, today, recent, favourites};
	}
}
