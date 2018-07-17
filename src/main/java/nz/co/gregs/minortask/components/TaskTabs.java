/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import java.util.ArrayList;

/**
 *
 * @author gregorygraham
 */
public class TaskTabs extends Tabs {

	public TaskTabs(Option opt) {
		super(Option.getTabs());
		setSelectedTab(opt.tab);
	}

	@Override
	public final void setSelectedTab(Tab tab) {
		super.setSelectedTab(tab);
	}

	public static enum Option {
		Projects(new Tab("Projects")),
		Creator(new Tab("Creator")),
		Editor(new Tab("Editor")),
		Today(new Tab("Today")),
		Urgent(new Tab("Urgent")),
		Picker(new Tab(new ProjectPicker(null))),
		Completed(new Tab("Completed"));

		private final Tab tab;

		Option(Tab tab) {
			this.tab = tab;
		}
		
		public static Tab[] getTabs(){
			Option[] values = values();
			ArrayList<Tab> tabsList = new ArrayList<Tab>();
			for (Option value : values) {
				tabsList.add(value.tab);
			}
			return tabsList.toArray(new Tab[]{});
		}
	}

}
