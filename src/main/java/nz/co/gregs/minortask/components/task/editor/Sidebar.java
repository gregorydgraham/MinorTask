/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.components.generic.SecureSpan;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.changes.ChangesList;
import nz.co.gregs.minortask.components.tasklists.RecentlyCompletedTasks;
import nz.co.gregs.minortask.components.tasklists.RecentlyViewedTasks;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
@Tag("sidebar")
@StyleSheet("styles/sidebar.css")
public class Sidebar extends SecureSpan implements MinorTaskEventNotifier, MinorTaskEventListener {

	final RecentlyCompletedTasks recentlyCompletedTasks = new RecentlyCompletedTasks();
	final ChangesList changesList = new ChangesList();
	final RecentlyViewedTasks recentlyViewedTasks = new RecentlyViewedTasks();

	public Sidebar() {
		addClassName("sidebar");

		add(
				recentlyCompletedTasks,
				Globals.getSpacer(),
				changesList,
				Globals.getSpacer(),
				recentlyViewedTasks
		);

		addMinorTaskEventListeners();
	}

	private void addMinorTaskEventListeners() {
		recentlyCompletedTasks.addMinorTaskEventListener(this);
		recentlyViewedTasks.addMinorTaskEventListener(this);
		changesList.addMinorTaskEventListener(this);
	}

	public void refresh() {
		recentlyCompletedTasks.refresh();
		changesList.refresh();
		recentlyViewedTasks.refresh();
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		fireEvent(event);
	}

}
