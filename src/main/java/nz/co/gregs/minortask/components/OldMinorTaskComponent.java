/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.Serializable;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.minortask.Helper;
import nz.co.gregs.minortask.MinorTaskUI;

/**
 *
 * @author gregorygraham
 */
public abstract class OldMinorTaskComponent implements Serializable {

	public MinorTaskUI ui;

	public OldMinorTaskComponent(MinorTaskUI ui) {
		this.ui = ui;
	}

//	public abstract void show();

	public abstract void handleDefaultButton();

	public abstract void handleEscapeButton();
	
	public abstract Component getComponent();

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
	}

	final protected DBDatabase getDatabase() {
		return Helper.getDatabase();
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
