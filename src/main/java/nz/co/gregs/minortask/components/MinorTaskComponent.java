/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.minortask.MinorTaskUI;

/**
 *
 * @author gregorygraham
 */
public abstract class MinorTaskComponent implements Serializable {

	public MinorTaskUI ui;

	public MinorTaskComponent(MinorTaskUI ui) {
		this.ui = ui;
	}

//	public abstract void show();

	public abstract void handleDefaultButton();

	public abstract void handleEscapeButton();
	
	public abstract Component getComponent();

	public final void chat(String string) {
		new Notification(string, Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
	}

	public final void warning(final String topic, final String warning) {
		Notification note = new Notification(topic, warning, Notification.Type.WARNING_MESSAGE);
		note.show(Page.getCurrent());
	}

	public final void error(final String topic, final String error) {
		Notification note = new Notification(topic, error, Notification.Type.ERROR_MESSAGE);
		note.show(Page.getCurrent());
	}

	public final void sqlerror(Exception exp) {
		Logger.getLogger(MinorTaskUI.class.getName()).log(Level.SEVERE, null, exp);
		Notification note = new Notification("SQL ERROR", exp.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
		note.show(Page.getCurrent());
	}

	public final void setAsDefaultButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener((event) -> {
			handleDefaultButton();
		});
	}

	public final void setEscapeButton(Button button) {
		button.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
		button.addClickListener((event) -> {
			handleEscapeButton();
		});
	}

	public final void show() {
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponent(getComponent());
		ui.setContent(verticalLayout);
		ui.currentPage = this;
	}

	final protected DBDatabase getDatabase() {
		return ui.getDatabase();
	}

	protected long getUserID() {
		return ui.getUserID();
	}

	protected boolean notLoggedIn() {
		return ui.getNotLoggedIn();
	}

	protected boolean loggedIn() {
		return !notLoggedIn();
	}

}
