/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.shared.Registration;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.*;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/tasktabs.css")
public class TaskTabs extends Tabs implements MinorTaskComponent {

	private Registration addSelectedChangeListener;

	private TaskTabs(Tab tab, Long taskID) {
		super(MinorTaskTab.getTabArray());
		setSelectedTab(tab);
		addSelectedChangeListener = addSelectedChangeListener((e) -> {
			tabClicked(e, taskID);
		});
		addClassName("minortask-tabs");
	}

	public TaskTabs(Component page, Long taskID) {
		this(MinorTaskTab.getTabForPage(page), taskID);
	}

	@Override
	public final void setSelectedTab(Tab tab) {
		try {
			super.setSelectedTab(tab);
		} catch (IllegalArgumentException exp) {
		}
	}

	@Override
	public int indexOf(Component component) {
		if (component instanceof Tab) {
			return MinorTaskTab.getIndexOf((Tab) component);
		} else {
			return 0;
		}
	}

	@Override
	public final Registration addSelectedChangeListener(ComponentEventListener<SelectedChangeEvent> e) {
		return super.addSelectedChangeListener(e);
	}

	protected void tabClicked(Tabs.SelectedChangeEvent e, Long taskID) {
		Tabs tabs = e.getSource();
		Tab selectedTab = tabs.getSelectedTab();
		MinorTaskTab.moveTo(selectedTab, taskID);
	}

	public void setTask(Long taskID) {
		if (addSelectedChangeListener != null) {
			addSelectedChangeListener.remove();
		}
		addSelectedChangeListener = addSelectedChangeListener((e) -> {
			tabClicked(e, taskID);
		});
	}

	public void setTask(Task task) {
		setTask(task==null?null:task.taskID.getValue());
	}

	public static class MinorTaskTab extends Tab {

		private static void moveTo(Tab selectedTab, Long taskID) {
			for (MinorTaskTab opt : getTabArray()) {
				if (opt.getTab().getLabel().equals(selectedTab.getLabel())) {
					opt.moveTo(taskID);
					break;
				}
			}
		}

		public static int getIndexOf(Tab component) {
			MinorTaskTab[] tabArray = getTabArray();
			int index = 0;
			for (MinorTaskTab minorTaskTab : tabArray) {
				if (minorTaskTab.getLabel().equals(component.getLabel())) {
					return index;
				}
				index++;
			}
			return 0;
		}

		protected final Class<? extends Component> destinationComponent;

		<C extends Component> MinorTaskTab(String label, Class<C> destination) {
			super(label);
			this.destinationComponent = destination;
		}

		private Tab getTab() {
			return this;
		}

		public static MinorTaskTab[] getTabArray() {
			return new MinorTaskTab[]{
				new MinorTaskTab("Details", MinorTaskLayout.class),
				new MinorTaskTab("Today", TodaysTaskLayout.class),
				new MinorTaskTab("Upcoming", UpcomingTasksPage.class),
				new MinorTaskTab("Overdue", OverdueTasksPage.class),
				new MinorTaskTab("Ideas", IdeasPage.class),
				new MinorTaskTab("Open", AllOpenTasksPage.class),
				new MinorTaskTab("Completed", AllCompletedTasksPage.class)
			};

		}

		public static Tab getTabForPage(Component page) {
			Class<? extends Component> pageClass = page.getClass();
			final MinorTaskTab[] tabArray = getTabArray();
			for (MinorTaskTab value : tabArray) {
				if (value.destinationComponent.equals(pageClass)) {
					return value;
				}
			}
			return null;
		}

		private <C extends Component & HasUrlParameter<Long>> void moveTo(Long taskID) {
			@SuppressWarnings("unchecked")
			Class<C> dest = (Class<C>) destinationComponent;
			try {
				UI.getCurrent().navigate(dest, taskID);
			} catch (IllegalArgumentException ex) {
				// Throw by pages that require the task ID so divert to projects
				UI.getCurrent().navigate(ProjectsLayout.class);
			}
		}
	}

}
