/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.place;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.SecureDiv;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/place-grid.css")
public class PlaceGrid extends SecureDiv {

	private Long taskID;
	private final Div grid = new Div();
	private List<Place> allRows;

	public PlaceGrid() {
		addClassName("place-grid");
	}

	public void setTaskID(Long taskID) {
		this.taskID = taskID;
		makeComponent();
	}

	@SuppressWarnings("unchecked")
	private void makeComponent() {
		removeAll();
		grid.removeAll();
		add(grid);
		setItems();
	}

	private Span getSuffixComponent(Place source) {
		final Span span = new Span(new Button(new Icon(VaadinIcon.TRASH), (event) -> removePlace(source)));
		span.addClassName("place-grid-entry-suffix");
		return span;
	}

	private Span getPrefixComponent(Place source) {
		Span layout = new Span();
		layout.addClassName("place-grid-entry-prefix");
		Component icon = new Icon(VaadinIcon.MAP_MARKER);
		String value = source.iconURL.getValue();
		if (value != null && !value.isEmpty()) {
			System.out.println("URL VALUE: \"" + source.iconURL.getValue() + "\"");
			icon = new Image(source.iconURL.getValue(), "");
		}

		if (source.latitude.isNotNull() && source.longitude.isNotNull()) {
			final String url = "https://www.openstreetmap.org"
					+ "/search?query=" + source.displayName.getValue("Unknown location").replaceAll(",", "%2c").replaceAll(" ", "%20")
					+ "#map=" + source.osmID.stringValue() + "/" + source.latitude.getValue() + "%2c" + source.longitude.getValue();
			Anchor anchor = new Anchor(url, "");
			anchor.setTarget("_blank");
			anchor.add(icon);
			Anchor anchor2 = new Anchor(url, "View");
			anchor2.setTarget("_blank");
			layout.add(anchor, anchor2);
		} else {
			layout.add(icon);
		}
		return layout;
	}

	private Span getSummaryComponent(Place source) {
		Span layout = new Span();
		layout.addClassName("place-grid-entry-summary");
		Div label = new Div(new Span(source.displayName.getValueWithDefaultValue("Location")));
		label.addClassName("place-grid-entry-prefix-label");
		TextField component = new TextField(
				"",
				source.description.getValueWithDefaultValue("Important Location"),
				(event) -> {
					updateDescription(source, event.getValue());
				});
		component.addClassName("place-grid-entry-prefix-description");
		layout.add(label, component);
		return layout;
	}

	public void refresh() {
		setItems();
	}

	private void setItems() {
		try {
			grid.removeAll();
			Place example = new Place();
			example.taskID.permittedValues(this.taskID);
			allRows = getDatabase().getDBTable(example).getAllRows();
			this.setVisible(!allRows.isEmpty());
			allRows.forEach((source) -> {
				Div gridEntry = new Div();
				gridEntry.addClassName("place-grid-entry");
				gridEntry.add(getPrefixComponent(source));
				gridEntry.add(getSummaryComponent(source));
				gridEntry.add(getSuffixComponent(source));
				grid.add(gridEntry);
			});
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void removePlace(Place locn) {
		try {
			getDatabase().delete(locn);
			fireEvent(new PlaceRemovedEvent(this, true));
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		setItems();
	}

	private void updateDescription(Place source, String value) {
		source.description.setValue(value);
		try {
			getDatabase().update(source);
			MinorTask.chat("Saved");
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public void setReadOnly(boolean b) {
	}

	public Registration addPlaceRemovedListener(
			ComponentEventListener<PlaceRemovedEvent> listener) {
		return addListener(PlaceRemovedEvent.class, listener);
	}
}
