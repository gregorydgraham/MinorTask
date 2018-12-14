/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.databases.DBDatabaseCluster;
import nz.co.gregs.dbvolution.databases.DatabaseConnectionSettings;
import nz.co.gregs.dbvolution.internal.database.ClusterDetails;

/**
 *
 * @author gregorygraham
 */
public class DatabaseComponent extends Div {

	public DatabaseComponent(DBDatabase db, ClusterDetails details, DatabaseConnectionSettings authoritativeDatabase) {
		String label = db.getLabel();
		label = (label == null || label.isEmpty()) ? "Unnamed" : label;
		final DatabaseConnectionSettings settings = db.getSettings();
		final Div databaseDescriptionDiv = new Div(
				new Label(settings.toString()
						.replaceAll("DATABASECONNECTIONSETTINGS:* *", "")
						.replaceAll("nz\\.co\\.gregs\\.dbvolution\\.databases\\.", ""))
		);
		databaseDescriptionDiv.addClassName("cluster-monitor-database-description");
		final Div databaseStatusDiv = new Div(
				new Label(details.getStatusOf(db).name()
				)
		);
		if (settings.equals(authoritativeDatabase)) {
			databaseStatusDiv.add(new Label("AUTHORITATIVE"));
		}
		databaseStatusDiv.addClassName("cluster-monitor-database-status");
		add(
				new Label(label+": "+db.getClass().getSimpleName()),
				databaseDescriptionDiv,
				databaseStatusDiv
		);
		addClassName("cluster-monitor-database");
	}

	public DatabaseComponent(DBDatabase db) {
		String label = db.getLabel();
		label = label == null || label.isEmpty() ? "Unnamed" : label;
		final DatabaseConnectionSettings settings = db.getSettings();
		final Div databaseDescriptionDiv = new Div(
				new Label(settings.toString()
						.replaceAll("DATABASECONNECTIONSETTINGS:* *", "")
						.replaceAll("nz\\.co\\.gregs\\.dbvolution\\.databases\\.", ""))
		);
		databaseDescriptionDiv.addClassName("cluster-monitor-database-description");

		add(
				new Label(label+": "+db.getClass().getSimpleName()),
				databaseDescriptionDiv
		);
		Exception except = db.getLastException();
		if (except != null) {
			add(new TextField("Exception", db.getLastException().getLocalizedMessage(), ""));
			StackTraceElement[] stackTrace = except.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				add(new TextField("", stackTraceElement.toString(), ""));
			}
		}

		addClassName("cluster-monitor-database");
	}
}
