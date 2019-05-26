/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

import nz.co.gregs.minortask.components.colleagues.ColleaguesDiv;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.components.task.SecureTaskDiv;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tabs;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.minortask.components.*;
import nz.co.gregs.minortask.components.tasklists.*;
import nz.co.gregs.minortask.MinorTaskEventListener;
import nz.co.gregs.minortask.MinorTaskEventNotifier;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@Tag("task-editor")
@StyleSheet("styles/edittask.css")
public class MinorTaskView extends SecureTaskDiv implements MinorTaskEventListener, MinorTaskEventNotifier, TaskEditorTabOptions {


	private final TaskEditor editorComponent = new TaskEditor();

	private final TodaysTasksList todaysTasksList = new TodaysTasksList();
	private final UpcomingTasksList upcomingTasksList = new UpcomingTasksList();
	private final OverdueTasksList overdueTasksList = new OverdueTasksList();
	private final IdeasList ideasList = new IdeasList();
	private final AllOpenTasksList openTaskList = new AllOpenTasksList();
	private final AllCompletedTasksList completedTaskList = new AllCompletedTasksList();
	private final FavouritedTasksList favouritedTasksList = new FavouritedTasksList();
	private final SearchedTasksList searchedTasksList = new SearchedTasksList();
	private final RecentlyViewedTasks recentlyViewedTasks = new RecentlyViewedTasks();
	private final ColleaguesDiv colleaguesDiv = new ColleaguesDiv();
	private final ProfileDiv profileDiv = new ProfileDiv();
	
	private final TaskEditorTabs taskTabs = new TaskEditorTabs(this);
	private final LoginComponent loginComponent = new LoginComponent();
	private final Div adminDiv = new Div();
	

	public MinorTaskView() {
		super();
		setInternalComponents();
	}

	private void setInternalComponents() {

		addAllViews();
		addClassName("edit-task-component");
	}

	private void addAllViews() {
		hideAll();
		
		taskTabs.addSelectedChangeListener((event) -> {
			taskTabs.tabClicked(event);
		});
		taskTabs.setOrientation(Tabs.Orientation.HORIZONTAL);
		
		
		editorComponent.addMinorTaskEventListener(this);
		todaysTasksList.addMinorTaskEventListener(this);
		upcomingTasksList.addMinorTaskEventListener(this);
		overdueTasksList.addMinorTaskEventListener(this);
		ideasList.addMinorTaskEventListener(this);
		openTaskList.addMinorTaskEventListener(this);
		completedTaskList.addMinorTaskEventListener(this);

		favouritedTasksList.addMinorTaskEventListener(this);
		searchedTasksList.addMinorTaskEventListener(this);
		recentlyViewedTasks.addMinorTaskEventListener(this);

		add(taskTabs);
		add(todaysTasksList);
		add(upcomingTasksList);
		add(overdueTasksList);
		add(ideasList);
		add(openTaskList);
		add(completedTaskList);
		add(editorComponent);
		add(favouritedTasksList);
		add(searchedTasksList);
		add(recentlyViewedTasks);
		add(colleaguesDiv);
		add(profileDiv);
		add(loginComponent);
		add(adminDiv);
	}

	private void hideAll() {
		todaysTasksList.setVisible(false);
		upcomingTasksList.setVisible(false);
		overdueTasksList.setVisible(false);
		ideasList.setVisible(false);
		openTaskList.setVisible(false);
		completedTaskList.setVisible(false);
		editorComponent.setVisible(false);
		favouritedTasksList.setVisible(false);
		searchedTasksList.setVisible(false);
		recentlyViewedTasks.setVisible(false);
		colleaguesDiv.setVisible(false);
		profileDiv.setVisible(false);
		loginComponent.setVisible(false);
		adminDiv.setVisible(false);
	}

	public void showTodayForAllProjects() {
		setTitle("Today");
		taskTabs.setVisible(false);
		todaysTasksList.setTask((Task)null);
		showComponent(todaysTasksList);
	}

	@Override
	public void showDetails() {
		Task task = setTitleToTaskName();
		taskTabs.setVisible(true);
		taskTabs.setSelectedTab(taskTabs.getDetailsTab());
		editorComponent.setTask(task);
		editorComponent.refresh();
		showComponent(editorComponent);
	}

	public Task setTitleToTaskName() {
		final Task task = getTask();
		setTitle(task==null?"Projects": task.name.getValue());
		return task;
	}

	@Override
	public void showUpcomingList() {
		Task task = setTitleToTaskName();
		taskTabs.setVisible(true);
		upcomingTasksList.setTask(task);
		showComponent(upcomingTasksList);
	}

	@Override
	public void showOverdueList() {
		Task task = setTitleToTaskName();
		taskTabs.setVisible(true);
		overdueTasksList.setTask(task);
		showComponent(overdueTasksList);
	}

	@Override
	public void showIdeasList() {
		Task task = setTitleToTaskName();
		taskTabs.setVisible(true);
		ideasList.setTask(task);
		showComponent(ideasList);
	}

	@Override
	public void showOpenList() {
		Task task = setTitleToTaskName();
		taskTabs.setVisible(true);
		openTaskList.setTask(task);
		showComponent(openTaskList);
	}

	@Override
	public void showCompletedList() {
		Task task = setTitleToTaskName();
		taskTabs.setVisible(true);
		completedTaskList.setTask(task);
		showComponent(completedTaskList);
	}

	private void showComponent(Component comp) {
		hideAll();
		comp.setVisible(true);
	}

	@Override
	public void showSearchList() {
		setTitle("Search");
		taskTabs.setVisible(false);
		showComponent(searchedTasksList);
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		// pass it up the chain
		fireEvent(event);
	}

	public void showFavouritesList() {
		setTitle("Favourites");
		taskTabs.setVisible(false);
		favouritedTasksList.refresh();
		showComponent(favouritedTasksList);
	}

	public void showRecentList() {
		setTitle("Recent");
		taskTabs.setVisible(false);
		recentlyViewedTasks.refresh();
		showComponent(recentlyViewedTasks);
	}

	public void showCluster() {
		setTitle("Cluster Management");
		taskTabs.setVisible(false);
		adminDiv.removeAll();
		adminDiv.add(new ClusterMonitorComponent());
		DBDatabase database = getDatabase();
		final DatabaseComponent databaseDiv = new DatabaseComponent(database);
		adminDiv.add(databaseDiv);
		showComponent(adminDiv);
	}

	public void showColleagues() {
		setTitle("Colleagues");
		taskTabs.setVisible(false);
		showComponent(colleaguesDiv);
		colleaguesDiv.refresh();
	}

	public void showProfile() {
		setTitle("Profile");
		taskTabs.setVisible(false);
		showComponent(profileDiv);
	}

	@Override
	public void showTodayForThisTask() {
		setTitle("Today");
		taskTabs.setVisible(true);
		todaysTasksList.setTask(getTask());
		showComponent(todaysTasksList);
	}

	public void showLogin() {
		setTitle("Login");
		taskTabs.setVisible(false);
		showComponent(loginComponent);
	}
	
	public void setLoginMethod(Runnable run){
		loginComponent.setLoginMethod(run);
	}
}
