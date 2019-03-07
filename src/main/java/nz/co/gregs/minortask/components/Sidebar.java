/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.changes.ChangesList;
import nz.co.gregs.minortask.components.tasklists.RecentlyCompletedTasks;
import nz.co.gregs.minortask.components.tasklists.RecentlyViewedTasks;

/**
 *
 * @author gregorygraham
 */
@Tag("sidebar")
@StyleSheet("styles/sidebar.css")
public class Sidebar extends SecureSpan {

	public Sidebar() {
		addClassName("sidebar");
		add(
//				Globals.getSpacer(),
				new RecentlyCompletedTasks(),
				Globals.getSpacer(),
				new ChangesList(),
				Globals.getSpacer(),
				new RecentlyViewedTasks()
		);
	}

}
