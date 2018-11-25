/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import com.vaadin.flow.component.Component;
import nz.co.gregs.minortask.components.ClusterMonitorComponent;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.Globals;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route(value="cluster", layout = MinortaskPushPage.class)
public class ClusterMonitorPage extends AuthorisedPage {

	private final ClusterMonitorComponent clusterMonitorComponent;
	
	public ClusterMonitorPage() {
		clusterMonitorComponent = new ClusterMonitorComponent();
	}

	@Override
	protected Component getInternalComponent() {
		return clusterMonitorComponent;
	}

	@Override
	public String getPageTitle() {
		return Globals.getApplicationName()+": Cluster Monitor";
	}
}
