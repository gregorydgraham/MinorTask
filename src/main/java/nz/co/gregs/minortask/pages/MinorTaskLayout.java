/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.banner.AuthorisedBannerMenu;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.components.task.editor.Sidebar;
import nz.co.gregs.minortask.components.task.editor.MinorTaskView;
import nz.co.gregs.minortask.components.task.editor.ViewBanner;
import nz.co.gregs.minortask.components.generic.FlexBox;
import nz.co.gregs.minortask.MinorTaskViews;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.MinorTaskEventListener;

/**
 *
 * @author gregorygraham
 */
@Route(value = "editor", layout = MinortaskPage.class)
@RouteAlias("ideas")
@RouteAlias("overdue")
@RouteAlias("projects")
@RouteAlias("task")
@RouteAlias("today")
@RouteAlias("search")
@RouteAlias("colleagues")
@RouteAlias("cluster")
@StyleSheet("styles/editor-layout.css")
public class MinorTaskLayout
		extends Div
		implements HasUrlParameter<Long>, MinorTaskComponent, BeforeEnterObserver, HasDynamicTitle, MinorTaskEventListener {

	protected final AuthorisedBannerMenu appBanner = new AuthorisedBannerMenu();
	private final ViewBanner viewBanner = new ViewBanner(this);
	private final MinorTaskView view = new MinorTaskView();
	private final Sidebar sidebar = new Sidebar();
	private final Div bottomLeft = new Div();
	private final Div bottomRight = new Div();
	private final Div bottom = new Div();
	private final Div taskComponents = new Div();
	private final FlexBox editorSection = new FlexBox();
	private Long requestedTaskID = null;

	public MinorTaskLayout() {
		super();
		buildContents();
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
		System.out.println("BEFORE SET PARAMETER EditorLayout with TASKID: " + parameter);
		requestedTaskID = parameter;
		System.out.println("AFTER SET PARAMETER EditorLayout with TASKID: " + parameter);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		System.out.println("BEFORE ENTER EditorLayout");
		if (!minortask().isLoggedIn()) {
			showLogin(event);
		} else {
			Location location = event.getLocation();
			String intendedView = location.getFirstSegment();
			try {
				handleMinorTaskEvent(MinorTaskViews.getEventFor(this, intendedView, getTask(requestedTaskID)));
			} catch (Globals.InaccessibleTaskException ex) {
				handleMinorTaskEvent(MinorTaskViews.getEventFor(this, intendedView));
			}
		}
		System.out.println("AFTER ENTER EditorLayout");
	}

	@Override
	public String getPageTitle() {
		return "Minortask: " + view.getTitle().orElse("Your Life Organised");
	}

	private void buildContents() {
		removeAll();
		add(new MinorTaskTemplate());

		viewBanner.addClassName("minortask-taskbanner");

		viewBanner.addMinorTaskEventListener(this);
		view.addMinorTaskEventListener(this);
		sidebar.addMinorTaskEventListener(this);
		appBanner.addMinorTaskEventListener(this);

		final Div topLeft = new Div(viewBanner);
		topLeft.addClassName("minortask-topleft");

		final Div topRightSpacer = new Div();
		topRightSpacer.addClassName("minortask-topright-spacer");

		final Div topRight = new Div(topRightSpacer);
		topRight.addClassName("minortask-topright");

		final Div top = new Div(topLeft, topRight);
		top.addClassName("minortask-top");

		final Div editorContents = new Div(view);
		editorContents.addClassName("minortask-editorcontents");

		bottomLeft.add(editorContents);
		bottomLeft.addClassName("minortask-taskcomponents");

		bottomRight.add(sidebar);
		bottomRight.addClassName("minortask-bottomright");

		bottom.add(bottomLeft, bottomRight);
		bottom.addClassName("minortask-underthebanner");

		taskComponents.add(top, bottom);
		taskComponents.addClassName("minortask-tasksection");

		editorSection.add(taskComponents);
		editorSection.addClassName("minortask-internal");

		Div verticalLayout = new Div(editorSection);
		verticalLayout.addClassName("minortask-internal-container");

		add(appBanner);
		add(verticalLayout);
		add(new FooterMenu());
	}

	private void changeURLPath(String urlFragment) {
		getUI().ifPresent((ui) -> {
			ui.getPage().executeJavaScript("history.pushState(history.state,'','" + urlFragment + "');");
		});
	}

	@Override
	public void handleMinorTaskEvent(MinorTaskEvent event) {
		switch (event.getView()) {
			case SEARCH:
				showSearchList();
				break;
			case CLUSTER:
				showClusterPage();
				break;
			case TASKDETAILS:
				showTask(event);
				break;
			case FAVOURITES:
				showFavouritesList();
				break;
			case RECENT:
				showRecentList();
				break;
			case COLLEAGUES:
				showColleagues();
				break;
			case PROFILE:
				showProfile();
				break;
			case TODAY:
				showTodayPage();
				break;
			default:;
		}
		refreshSideBar();
	}

	private void changeToTopLevelPage(String bannerTitle, String bannerDescription, String urlSnippet, Runnable viewCall) {
		viewCall.run();
		viewBanner.setTask((Task) null);
		changeToPage(bannerTitle, bannerDescription, urlSnippet);
	}

	private void changeToTaskPage(Task task, String bannerTitle, String bannerDescription, String urlSnippet, Runnable viewCall) {
		view.setTask(task);
		viewCall.run();
		viewBanner.setTask(task);
		changeToPage(bannerTitle, bannerDescription, urlSnippet);
	}

	private void changeToPage(final String title, final String description, final String urlDestination) {
		viewBanner.setTitle(title);
		viewBanner.setDescription(description);
		UI.getCurrent().getPage().setTitle(getPageTitle());
		changeURLPath(urlDestination);
//		sidebar.refresh();
	}

	private void showTask(MinorTaskEvent event) {
		final Task task = event.getTask();
		String title = (task == null ? "Projects" : task.name.getValue());
		String description = (task == null ? "Build Your Plans Here" : task.description.getValue());
		String urlFragment = "task" + (task == null ? "" : "/" + task.taskID.getValue());
		changeToTaskPage(task, title, description, urlFragment, view::showDetails);
	}

	public void showRecentList() {
		changeToTopLevelPage("Recent Tasks", "recently viewed tasks", "recent", view::showRecentList);
	}

	public void showFavouritesList() {
		changeToTopLevelPage("Favourites", "all your favourite tasks and projects", "favourites", view::showFavouritesList);
	}

	public void showSearchList() {
		changeToTopLevelPage("Search", "search for tasks by name and description", "search", view::showSearchList);
	}

	private void showClusterPage() {
		changeToTopLevelPage("Cluster", "make sure the data is safe", "cluster", view::showCluster);
	}

	private void showColleagues() {
		changeToTopLevelPage("Colleagues", "your work colleagues that will help with these tasks", "colleagues", view::showColleagues);
	}

	private void showProfile() {
		changeToTopLevelPage("Profile", "your settings and information", "profile", view::showProfile);
	}

	private void showTodayPage() {
		changeToTopLevelPage("Today", "what to do today within your projects", "today", view::showTodayForAllProjects);
	}

	private void showLogin(BeforeEnterEvent event) {
		viewBanner.setTitle("Login");
		viewBanner.setDescription("Please login first");
		view.setLoginMethod(() -> {
			appBanner.refresh();
			sidebar.refresh();
			beforeEnter(event);
		});
		view.showLogin();
	}

	private LocalTime nextRefresh;

	private void refreshSideBar() {
		if (nextRefresh == null || LocalTime.now().isAfter(nextRefresh)) {
			sidebar.refresh();
			nextRefresh = LocalTime.now().plus(20, ChronoUnit.SECONDS);
		}
	}
}
