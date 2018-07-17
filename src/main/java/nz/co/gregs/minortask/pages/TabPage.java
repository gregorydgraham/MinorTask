/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.AuthorisedBannerMenu;
import nz.co.gregs.minortask.components.CreateTask;
import nz.co.gregs.minortask.components.EditTask;
import nz.co.gregs.minortask.components.FooterMenu;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("tabs")
public class TabPage extends VerticalLayout implements HasUrlParameter<Integer> {

	private Integer taskID;
	final EditTask editTask = new EditTask(null);
	final CreateTask createTask = new CreateTask(null);
	final VerticalLayout contentPanel = new VerticalLayout();

	public TabPage() throws MinorTask.InaccessibleTaskException {
		this(new Tab[]{});
	}

	public TabPage(Tab... tabs) throws MinorTask.InaccessibleTaskException {
		super(tabs);
		add(new AuthorisedBannerMenu(null));

		Tab editorTab = new Tab("edit");
		Tab createTab = new Tab("create");

		Tabs tabber = new Tabs();

		tabber.add(editorTab);
		tabber.add(createTab);

		tabber.addSelectedChangeListener((event) -> {
			Tab selectedTab = tabber.getSelectedTab();
			if (selectedTab == editorTab) {
				contentPanel.removeAll();
				contentPanel.add(editTask);
			} else if (selectedTab == createTab) {
				contentPanel.removeAll();
				contentPanel.add(createTask);				
			}
		});

		add(tabber);
		
		contentPanel.add(editTask);
		add(contentPanel);

		add(new FooterMenu());
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter Integer parameter) {
		this.taskID = parameter;
	}

}
