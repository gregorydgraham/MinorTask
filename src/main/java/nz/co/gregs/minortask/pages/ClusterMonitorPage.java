/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.pages;

import nz.co.gregs.minortask.components.DatabaseComponent;
import com.vaadin.flow.component.Component;
import nz.co.gregs.minortask.components.ClusterMonitorComponent;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.databases.DBDatabaseCluster;
import nz.co.gregs.dbvolution.databases.DatabaseConnectionSettings;
import nz.co.gregs.dbvolution.internal.database.ClusterDetails;
import nz.co.gregs.minortask.Globals;

/**
 *
 * @author gregorygraham
 */
@HtmlImport("styles/shared-styles.html")
@StyleSheet("styles/cluster-monitor.css")
@Route(value = "cluster", layout = MinortaskPushPage.class)
public class ClusterMonitorPage extends AuthorisedPage {

	private final ClusterMonitorComponent clusterMonitorComponent;

	public ClusterMonitorPage() {
		clusterMonitorComponent = new ClusterMonitorComponent();
	}

	@Override
	protected Component getInternalComponent() {
		Div div = new Div(clusterMonitorComponent);
		DBDatabase database = getDatabase();
		final DatabaseComponent databaseDiv = new DatabaseComponent(database);
		div.add(databaseDiv);
		if (database instanceof DBDatabaseCluster) { // never assume its a cluster ;-)
			databaseDiv.addClassName("cluster-monitor");
			DBDatabaseCluster cluster = (DBDatabaseCluster) database;
			Div clusterDiv = new Div(new Label(cluster.getDatabaseName()));
			clusterDiv.addClassName("cluster-monitor");
			div.add(clusterDiv);
			ClusterDetails details = cluster.getClusterDetails();
			DatabaseConnectionSettings authoritativeDatabase = details.getAuthoritativeDatabase();
			DBDatabase[] allDBs = details.getAllDatabases();
			for (DBDatabase db : allDBs) {
				Div dbDiv = new DatabaseComponent(db, details, authoritativeDatabase);
				databaseDiv.add(dbDiv);
			}
		}
		return div;
	}

	@Override
	public String getPageTitle() {
		return Globals.getApplicationName() + ": Cluster Monitor";
	}
}
