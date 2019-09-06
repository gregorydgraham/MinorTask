/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.database;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.databases.DBDatabaseCluster;
import nz.co.gregs.dbvolution.databases.DatabaseConnectionSettings;
import nz.co.gregs.dbvolution.internal.database.ClusterDetails;
import nz.co.gregs.dbvolution.utility.RegularProcess;
import nz.co.gregs.minortask.components.polymer.Details;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/cluster-monitor.css")
public class DatabaseComponent extends Div {

	private final Div databaseDescriptionDiv = new Div();
	private final Div labelDiv = new Div();
	private final Div databaseStatusDiv = new Div();
	private final Details exceptionDiv = new Details("No Exception");
	private final Div containedDatabaseDiv = new Div();
	private final Div regularProcessorsDiv = new Div();
	private final Div clusterRebuildDiv = new Div();
	private final Div clusterReconnectDiv = new Div();

	protected DatabaseComponent(DBDatabase db, ClusterDetails details, DatabaseConnectionSettings authoritativeDatabase) {
		this(db);
		if (db.getSettings().equals(authoritativeDatabase)) {
			databaseStatusDiv.add(new Div(new Label("AUTHORITATIVE")));
		}
		final DBDatabaseCluster.Status status = details.getStatusOf(db);
		databaseStatusDiv.add(new Div(new Label(status.name())));
		databaseStatusDiv.addClassName("cluster-monitor-database-status");
		addClassName(
				status.equals(DBDatabaseCluster.Status.READY)
				? "cluster-monitor-ready-database"
				: "cluster-monitor-nonready-database");
	}

	public DatabaseComponent(DBDatabase database) {
		addClassName("cluster-monitor-database");
		add(labelDiv);
		add(databaseDescriptionDiv);
		add(clusterRebuildDiv);
		add(clusterReconnectDiv);
		add(databaseStatusDiv);
		add(exceptionDiv);
		add(containedDatabaseDiv);
		add(regularProcessorsDiv);
		labelDiv.addClassName("cluster-monitor-database-label");
		databaseDescriptionDiv.addClassName("cluster-monitor-database-description");
		databaseStatusDiv.addClassName("cluster-monitor-database-status");
		exceptionDiv.addClassName("cluster-monitor-exception");
		containedDatabaseDiv.addClassName("cluster-monitor-contained-databases");
		regularProcessorsDiv.addClassName("cluster-monitor-regularProcessors");

		if (database != null) {
			String label = database.getLabel();
			label = label == null || label.isEmpty() ? "Unnamed" : label;
			labelDiv.add(new Label(label + ": " + database.getClass().getSimpleName()));
			DatabaseConnectionSettings settings = database.getSettings();
			databaseDescriptionDiv.add(
					new Label(settings.toString()
							.replaceAll("DATABASECONNECTIONSETTINGS:* *", "")
							.replaceAll("nz\\.co\\.gregs\\.dbvolution\\.databases\\.", ""))
			);

			Exception except = database.getLastException();
			if (except != null) {
				exceptionDiv.setSummary(database.getLastException().getLocalizedMessage());
				StackTraceElement[] stackTrace = except.getStackTrace();
				for (StackTraceElement stackTraceElement : stackTrace) {
					exceptionDiv.add(new Div(new Label(stackTraceElement.toString())));
				}
			} else {
				exceptionDiv.add(new Div(new Label("No Exception")));
			}

			regularProcessorsDiv.add(new Label("Processors"));
			for (RegularProcess regProc : database.getRegularProcessors()) {
				regularProcessorsDiv.add(new RegularProcessorOverview(regProc));
			}
			if (database instanceof DBDatabaseCluster) {
				addClassName("cluster-monitor-database-cluster");
				DBDatabaseCluster cluster = (DBDatabaseCluster) database;
				boolean autoReconnect = cluster.getAutoReconnect();
				boolean autoRebuild = cluster.getAutoRebuild();
				clusterRebuildDiv.add(new Label("Rebuild: " + autoRebuild));
				clusterReconnectDiv.add(new Label("Reconnect: " + autoReconnect));

				ClusterDetails details = cluster.getClusterDetails();
				DatabaseConnectionSettings authoritativeDatabase = details.getAuthoritativeDatabaseConnectionSettings();
				DBDatabase[] allDBs = details.getAllDatabases();
				containedDatabaseDiv.add(new Label("Databases"));
				for (DBDatabase db : allDBs) {
					Div dbDiv = new DatabaseComponent(db, details, authoritativeDatabase);
					dbDiv.addClassName("cluster-monitor-contained-database");
					containedDatabaseDiv.add(dbDiv);
				}
			} else {
				remove(containedDatabaseDiv);
			}
		}
	}
}
