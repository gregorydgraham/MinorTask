/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.databases.DBDatabaseCluster;
import nz.co.gregs.minortask.Globals;

/**
 *
 * @author gregorygraham
 */
public class ClusterMonitorComponent extends VerticalLayout {

	private FeederThread thread;
	final AppendingTextArea clusterStates = new AppendingTextArea("Current State", 1000);
	private final Checkbox firstCheckBox = new Checkbox("Monitor");

	public ClusterMonitorComponent() {
		add(new HorizontalLayout(firstCheckBox), clusterStates);
		firstCheckBox.addValueChangeListener((event) -> {
			createMonitorThread();
			if (thread != null) {
				if (event.getValue()) {
					// Start the data feed thread
					thread.startup();
				} else {
					// Stop it
					thread.shutdown();
				}
			}
		});
		clusterStates.setHeight("20em");
		clusterStates.setWidth("50em");
		clusterStates.addClassName("cluster-states");
	}

	private void createMonitorThread() {
		if (thread == null) {
			final DBDatabase database = Globals.getDatabase();
			if (database instanceof DBDatabaseCluster) {
				thread = new FeederThread(UI.getCurrent(), this, (DBDatabaseCluster) database);
			}
		}
	}

	private static class AppendingTextArea extends TextArea {

		private int maxChars = 10000;

		public AppendingTextArea(String label, int maxChars) {
			super(label);
			this.maxChars = maxChars;
		}

		@Override
		public void setValue(String value) {
			String newValue = value + "\n" + this.getValue();
			if (newValue.length() > maxChars) {
				newValue = newValue.substring(0, maxChars);
			}
			super.setValue(newValue);
		}
	}

	private static class FeederThread extends Thread {

		private final UI ui;
		private final ClusterMonitorComponent view;
		private final DBDatabaseCluster cluster;
		private boolean keepGoing = true;

		public FeederThread(UI ui, ClusterMonitorComponent view, DBDatabaseCluster cluster) {
			this.ui = ui;
			this.view = view;
			this.cluster = cluster;

		}

		@Override
		public void run() {
			try {
				// Update the data for a while
				while (keepGoing) {
					// Sleep to emulate background work
					Thread.sleep(800);
					String message = cluster.getClusterStatus() + "\n" + cluster.getDatabaseStatuses();

					ui.access(() -> view.clusterStates.setValue(message));
				}
			} catch (InterruptedException e) {
//				e.printStackTrace();
			}
			keepGoing = true;
		}

		private void shutdown() {
			this.keepGoing = false;
		}

		private void startup() {
			this.start();
		}

		@Override
		public void start() {
			this.keepGoing = true;
			super.start();
		}
	}

}
