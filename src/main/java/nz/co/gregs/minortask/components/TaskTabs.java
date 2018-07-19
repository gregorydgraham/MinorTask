/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.*;

/**
 *
 * @author gregorygraham
 */
public class TaskTabs extends Tabs implements MinorTaskComponent {

	private TaskTabs(Tab tab, Long taskID) {
		super(Option.getTabs());
		setSelectedTab(tab);
		addSelectedChangeListener((e) -> {
			tabClicked(e, taskID);
		});
	}

	public TaskTabs(Option opt, Long taskID) {
		this(opt.tab, taskID);
	}

	public TaskTabs(MinorTaskPage page, Long taskID) {
		this(Option.getTabForPage(page), taskID);
	}

	@Override
	public final void setSelectedTab(Tab tab) {
		super.setSelectedTab(tab);
	}

	@Override
	public final Registration addSelectedChangeListener(ComponentEventListener<SelectedChangeEvent> e) {
		return super.addSelectedChangeListener(e);
	}

	protected void tabClicked(Tabs.SelectedChangeEvent e, Long taskID) {
		Tabs tabs = e.getSource();
		Tab selectedTab = tabs.getSelectedTab();
		Option[] opts = Option.values();
		for (Option opt : opts) {
			if (opt.getTab() == selectedTab) {
//				if (opt.equals(Option.Picker)) {
//					ProjectSelectorTab tab = (ProjectSelectorTab) opt.tab;
//					ProjectSelector selector = tab.getSelector();
//					if (selector.isOpened()) {
//						// wait for it to close
//					} else {
//						Task value = selector.getValue();
//						opt.moveTo(value==null||value.taskID==null?null:value.taskID.getValue());
//					}
//				} else {
					opt.moveTo(taskID);
//				}
			}
		}
	}

	public static enum Option implements MinorTaskComponent {
		Projects(new Tab("Projects"), ProjectsLayout.class),
		Creator(new Tab("Creator"), TaskCreatorLayout.class),
		Editor(new Tab("Editor"), TaskEditorLayout.class),
		Today(new Tab("Today"), TodaysTaskLayout.class),
		Upcoming(new Tab("Upcoming"), UpcomingTasksPage.class),
		Overdue(new Tab("Overdue"), OverdueTasksPage.class),
//		Picker(new ProjectSelectorTab(), ProjectTaskListPage.class),
		AllCompleted(new Tab("All Completed"), AllCompletedTasksPage.class);
		private static Tab[] staticTabs = new Tab[]{};

		private final Tab tab;

		<C extends MinorTaskPage> Option(Tab tab, Class<C> destination) {
			this.tab = tab;
			destinationComponent = destination;
		}
		protected final Class<?> destinationComponent;

		public static Tab[] getTabs() {
			if (staticTabs.length == 0) {
				Option[] values = values();
				ArrayList<Tab> tabsList = new ArrayList<Tab>();
				for (Option value : values) {
					tabsList.add(value.tab);
				}
				staticTabs = tabsList.toArray(new Tab[]{});
			}
			return staticTabs;
		}

		private Tab getTab() {
			return tab;
		}

		public static Tab getTabForPage(MinorTaskPage page) {
			Class<? extends MinorTaskPage> pageClass = page.getClass();
			for (Option value : values()) {
				if (value.destinationComponent.equals(pageClass)) {
					return value.tab;
				}
			}
			return Projects.tab;
		}

		private <C extends Component & HasUrlParameter<Long>> void moveTo(Long taskID) {
			@SuppressWarnings("unchecked")
			Class<C> dest = (Class<C>) destinationComponent;
			UI.getCurrent().navigate(dest, taskID);
		}
	}

}
