/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import nz.co.gregs.minortask.components.task.editor.Sidebar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTaskTemplate;
import nz.co.gregs.minortask.components.AccessDeniedComponent;
import nz.co.gregs.minortask.components.generic.FlexBox;
import nz.co.gregs.minortask.components.FooterMenu;
import nz.co.gregs.minortask.components.ProjectPathChanger;
import nz.co.gregs.minortask.components.ProjectPathNavigator;
import nz.co.gregs.minortask.components.TaskBanner;
import nz.co.gregs.minortask.components.TaskTabs;
import nz.co.gregs.minortask.datamodel.Task;

public abstract class AuthorisedOptionalTaskPage extends AuthorisedPage implements HasUrlParameter<Long> {

	TaskTabs taskTabs;

	@Override
	protected final Component getInternalComponent() {
		return new AccessDeniedComponent();
	}

	protected abstract Component getInternalComponent(Task parameter);

	@Override
	public final void setComponents() {
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
		taskID = parameter;
		removeAll();
		add(new MinorTaskTemplate());

		final TaskBanner taskBanner = new TaskBanner();

		try {
			Task task = getTask(parameter);
			taskBanner.setTask(task);
			taskBanner.addClassName("minortask-taskbanner");
		} catch (Globals.InaccessibleTaskException ex) {
			Logger.getLogger(AuthorisedOptionalTaskPage.class.getName()).log(Level.SEVERE, null, ex);
		}

		taskTabs = new TaskTabs(this, taskID);
		taskTabs.setOrientation(Tabs.Orientation.HORIZONTAL);

		Component internalComponent;
		if (minortask().isLoggedIn()) {
			try {
				internalComponent = getInternalComponent(getTask(parameter));
			} catch (Globals.InaccessibleTaskException ex) {
				internalComponent = new AccessDeniedComponent();
			}
		} else {
			internalComponent = new AccessDeniedComponent();
		}

		ProjectPathNavigator projectPath = new ProjectPathNavigator(this.getClass(), taskID);
		if (internalComponent instanceof ProjectPathChanger) {
			ProjectPathChanger ppc = (ProjectPathChanger) internalComponent;
			ppc.addProjectPathAlteredListener((evt) -> {
				projectPath.refresh();
			});
		}
		final Div topLeft = new Div(taskBanner);
		topLeft.addClassName("minortask-topleft");

		final Div topRightSpacer = new Div();
		topRightSpacer.addClassName("minortask-topright-spacer");

		final Div topRight = new Div(topRightSpacer);
		topRight.addClassName("minortask-topright");

		final Div top = new Div(topLeft, topRight);
		top.addClassName("minortask-top");

		final Div bottomLeft = new Div(taskTabs, internalComponent);
		bottomLeft.addClassName("minortask-taskcomponents");

		final Div bottomRight = new Div(new Sidebar());
		bottomRight.addClassName("minortask-bottomright");

		final Div bottom = new Div(bottomLeft, bottomRight);
		bottom.addClassName("minortask-underthebanner");

		final Div taskComponents = new Div(top, bottom);
		taskComponents.addClassName("minortask-tasksection");

		FlexBox internalComponentHolder = new FlexBox(taskComponents);
		internalComponentHolder.addClassName("minortask-internal");

		Div verticalLayout = new Div(internalComponentHolder);
		verticalLayout.addClassName("minortask-internal-container");

		add(banner);
		add(verticalLayout);
		add(new FooterMenu());
	}
}
