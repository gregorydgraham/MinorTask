/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

import nz.co.gregs.minortask.MinorTaskEvent;
import com.vaadin.flow.component.ComponentEventListener;
import nz.co.gregs.minortask.components.generic.SecureSpan;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
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
//	private final List<TaskMoveListener> taskMoveHandlers = new ArrayList<>();

	public Sidebar() {
		addClassName("sidebar");

		add(
				recentlyCompletedTasks,
				Globals.getSpacer(),
				changesList,
				Globals.getSpacer(),
				recentlyViewedTasks
		);

//		recentlyViewedTasks.addMinorTaskEventListener((event) -> fireEvent(event));
//		recentlyCompletedTasks.addMinorTaskEventListener((event) -> fireEvent(event));
//		changesList.addMinorTaskEventListener((event) -> fireEvent(event));
		recentlyCompletedTasks.addMinorTaskEventListener(this);
		recentlyViewedTasks.addMinorTaskEventListener(this);
		changesList.addMinorTaskEventListener(this);
	}

//	public Registration addMinorTaskEventListener(
//			ComponentEventListener<TaskMoveEvent> listener) {
//		return addListener(MinorTaskEvent.class, listener);
//	}
	public void refresh() {
		ScheduledFuture asyncCompleted = MinorTask.runAsync(() -> {
			recentlyCompletedTasks.refresh();
		});

		ScheduledFuture asyncChanges = MinorTask.runAsync(() -> {
			changesList.refresh();
		});

		ScheduledFuture asyncViewed = MinorTask.runAsync(() -> {
			recentlyViewedTasks.refresh();
		});
	}

//	@Override
//	public List<TaskMoveListener> getTaskMoveHandlers() {
//		return taskMoveHandlers;
//	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		fireEvent(event);
	}

}
