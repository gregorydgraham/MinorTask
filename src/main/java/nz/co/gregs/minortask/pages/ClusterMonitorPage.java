/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import nz.co.gregs.minortask.components.ClusterMonitorComponent;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import nz.co.gregs.minortask.MinorTaskTemplate;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@Route("cluster")
@Push
public class ClusterMonitorPage extends VerticalLayout {

	private final ClusterMonitorComponent clusterMonitorComponent;
	
	public ClusterMonitorPage() {
		clusterMonitorComponent = new ClusterMonitorComponent();
		try {
			addComponents();
		} catch (Exception ex) {
			System.out.println(this.getClass().getCanonicalName() + ".<init>(): " + ex.getClass().getSimpleName() + " -> " + ex.getMessage());
		}
	}

	public final void addComponents() {
		final MinorTaskTemplate minorTaskTemplate = new MinorTaskTemplate();
		add(minorTaskTemplate);
		add(clusterMonitorComponent);
	}
}
