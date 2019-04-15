/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.shared.Registration;
import java.util.Arrays;
import nz.co.gregs.minortask.components.MinorTaskComponent;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/tasktabs.css")
public class TaskEditorTabs extends Tabs implements MinorTaskComponent {

	private Registration addSelectedChangeListener;
	private final TaskEditorTabOptions page;

	private final MinorTaskTab[] tabArray = new MinorTaskTab[]{
		new MinorTaskTab("Details", this) {
			@Override
			public void moveTo() {
				page.showDetails();
			}
		},
		new MinorTaskTab("Today", this) {
			@Override
			public void moveTo() {
				page.showTodayForThisTask();
			}
		},
		new MinorTaskTab("Upcoming", this) {
			@Override
			public void moveTo() {
				getTabs().getPage().showUpcomingList();
			}
		},
		new MinorTaskTab("Overdue", this) {
			@Override
			public void moveTo() {
				getTabs().getPage().showOverdueList();
			}
		},
		new MinorTaskTab("Ideas", this) {
			@Override
			public void moveTo() {
				getTabs().getPage().showIdeasList();
			}
		},
		new MinorTaskTab("Open", this) {
			@Override
			public void moveTo() {
				getTabs().getPage().showOpenList();
			}
		},
		new MinorTaskTab("Completed", this) {
			@Override
			public void moveTo() {
				getTabs().getPage().showCompletedList();
			}
		}
	};

	public TaskEditorTabs(TaskEditorTabOptions page) {
		super();
		this.page = page;
		addClassName("minortask-tabs");
		add(tabArray);
	}

	@Override
	public final void setSelectedTab(Tab tab) {
		try {
			super.setSelectedTab(tab);
		} catch (IllegalArgumentException exp) {
		}
	}

	private TaskEditorTabOptions getPage() {
		return page;
	}

	@Override
	public int indexOf(Component component) {
		if (component instanceof Tab) {
			final int index = Arrays.asList(tabArray).indexOf(component);
			return index == -1 ? 0 : index;
		} else {
			return 0;
		}
	}

	@Override
	public final Registration addSelectedChangeListener(ComponentEventListener<SelectedChangeEvent> e) {
		return super.addSelectedChangeListener(e);
	}

	public void tabClicked(Tabs.SelectedChangeEvent e) {
		Tabs tabs = e.getSource();
		MinorTaskTab selectedTab = (MinorTaskTab) tabs.getSelectedTab();
		selectedTab.moveTo();
	}

	public void setTask(Long taskID) {
		if (addSelectedChangeListener != null) {
			addSelectedChangeListener.remove();
		}
		addSelectedChangeListener = addSelectedChangeListener((e) -> {
			tabClicked(e);
		});
	}

	public void setTask(Task task) {
		setTask(task == null ? null : task.taskID.getValue());
	}

	public MinorTaskTab[] getTabArray() {
		return tabArray;
	}

	public Tab getDetailsTab() {
		return getTabArray()[0];
	}

	public void setSelectedTabByName(String tabName) {
		for (MinorTaskTab minorTaskTab : getTabArray()) {
			if (minorTaskTab.getLabel().toLowerCase().equals(tabName.toLowerCase())) {
				setSelectedTab(minorTaskTab);
				return;
			}
		}
	}

	public static abstract class MinorTaskTab extends Tab {

		protected final TaskEditorTabs tabs;

		MinorTaskTab(String label, TaskEditorTabs tabs) {
			super(label);
			this.tabs = tabs;
		}

		public TaskEditorTabs getTabs() {
			return tabs;
		}

		public abstract void moveTo();
	}

}
