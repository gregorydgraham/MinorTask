/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.pages.ProjectTaskListPage;

/**
 *
 * @author gregorygraham
 */
public class ProjectSelectorTab extends Tab {

	final ProjectSelector selector;

	/**
	 * Constructs a new object in its default state.
	 */

	public ProjectSelectorTab() {
		this("Project: ");

	}

	/**
	 * Constructs a new object with the given label.
	 *
	 * @param label the label to display
	 */
	public ProjectSelectorTab(String label) {
		super(label);
		selector = new ProjectSelector(null);
		add(selector);
		selector.addValueChangeListener((event) -> {
			Task value = selector.getValue();
			UI.getCurrent().navigate(ProjectTaskListPage.class, value.taskID.getValue());
		});
	}

	/**
	 * Constructs a new object with child components.
	 *
	 * @param components the child components
	 */
	public ProjectSelectorTab(Component[] components) {
		this();
		add(components);
	}

	public ProjectSelector getSelector() {
		return this.selector;
	}

}
