/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.weblinks;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.components.SecureDiv;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/weblink-grid.css")
public class WeblinkGrid extends SecureDiv {

	private Long taskID;
	private final Div gridDiv = new Div();
	private List<Weblink> allRows;

	public WeblinkGrid() {
	}

	public final void setTaskID(Long taskID) {
		this.taskID = taskID;
		makeComponent();
		addClassName("weblink-component");
	}

	@SuppressWarnings("unchecked")
	private void makeComponent() {
		removeAll();
		setSizeUndefined();
		gridDiv.addClassName("weblink-grid");

		setItems();

		add(gridDiv);
	}

	private Span getSuffixComponent(Weblink source) {
		final Span layout = new Span();
		layout.addClassName("weblink-grid-entry-suffix");

		final Button button = new Button(new Icon(VaadinIcon.TRASH), (event) -> removePlace(source));
		button.setEnabled(this.isEnabled() && !this.isReadOnly());
		layout.add(button);

		return layout;
	}

	private Span getPrefixComponent(Weblink source) {
		Span layout = new Span();
		layout.addClassName("weblink-grid-entry-prefix");

		Span iconSpan = new Span();
		String iconURL = source.iconURL.getValue();
		Component icon = new Icon(VaadinIcon.BOOKMARK_O);
		if (iconURL != null && !iconURL.isEmpty()) {
			icon = new Image(iconURL, "");
		}
		((HasStyle)icon).addClassName("weblink-grid-entry-prefix-icon");
		iconSpan.add(icon);

		Anchor iconAnchor = new Anchor(source.webURL.getValue(), "");
		iconAnchor.setTarget("_blank");
		iconAnchor.add(iconSpan);
		Anchor urlAnchor = new Anchor(source.webURL.getValue(), source.webURL.getValue().replaceAll("http[s]*://", ""));
		urlAnchor.setTarget("_blank");
		layout.add(iconAnchor, urlAnchor);
		return layout;
	}

	private Span getSummaryComponent(Weblink source) {
		Span layout = new Span();
		layout.addClassName("weblink-grid-entry-summary");
		TextField component = new TextField(
				"",
				source.description.getValueWithDefaultValue("Important Location"),
				(event) -> {
					updateDescription(source, event.getValue());
				});
		layout.add(component);
		return layout;
	}

	private void setItems() {
		try {
			Weblink example = new Weblink();
			example.taskID.permittedValues(this.taskID);
			allRows = getDatabase().getDBTable(example).getAllRows();
			allRows.forEach((source) -> {
				Div gridEntry = new Div();
				gridEntry.addClassName("weblink-grid-entry");
				gridEntry.add(getPrefixComponent(source));
				gridEntry.add(getSummaryComponent(source));
				gridEntry.add(getSuffixComponent(source));
				gridDiv.add(gridEntry);
			});
			this.setVisible(!allRows.isEmpty());
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void removePlace(Weblink locn) {
		try {
			getDatabase().delete(locn);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		setItems();
	}

	private void updateDescription(Weblink source, String value) {
		source.description.setValue(value);
		try {
			getDatabase().update(source);
			chat("Saved");
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public boolean isReadOnly() {
		return !isEnabled();
	}

	public void setReadOnly(boolean readonly) {
		gridDiv.setEnabled(!readonly);
	}

	@Override
	public void setEnabled(boolean enabled) {
		getElement().setEnabled(enabled);
	}

	public void refresh() {
		setItems();
	}
}
