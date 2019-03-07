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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.components.SecureDiv;
import nz.co.gregs.minortask.components.changes.Changes;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/openstreetmapplace-grid.css")
public class OpenStreetMapPlaceGrid extends SecureDiv {

	private Long taskID;
	private final Div grid = new Div();

	public OpenStreetMapPlaceGrid(Long taskID) {
		this.taskID = taskID;
		makeComponent();
	}

	public OpenStreetMapPlaceGrid(Long taskID, List<OpenStreetMapPlace> places) {
		this(taskID);
		setItems(places);
	}

	public void setTaskID(Long taskID) {
		this.taskID = taskID;
		makeComponent();
	}

	@SuppressWarnings("unchecked")
	private void makeComponent() {
		removeAll();
		addClassName("openstreetmapplace-grid");
		add(grid);
	}

	private Span getSuffixComponent(Place source) {
		final Button button = new Button("Add", new Icon(VaadinIcon.PLUS_CIRCLE), (event) -> {
			addSelectedLocation(source);
		});
		final Span span = new Span(button);
		span.addClassName("openstreetmapplace-grid-entry-suffix");
		return span;
	}

	private Span getPrefixComponent(Place source) {
		Span layout = new Span();
		layout.addClassName("openstreetmapplace-grid-entry-prefix");
		Component icon = new Icon(VaadinIcon.MAP_MARKER);
		String value = source.iconURL.getValue();
		if (value != null && !value.isEmpty()) {
			System.out.println("URL VALUE: \"" + source.iconURL.getValue() + "\"");
			icon = new Image(source.iconURL.getValue(), "");
		}

		if (source.latitude.isNotNull() && source.longitude.isNotNull()) {
			Anchor anchor = new Anchor("https://www.openstreetmap.org"
					+ "/directions?from=&to=" + source.latitude.getValue() + "%2c" + source.longitude.getValue(),
					"");
			anchor.setTarget("_blank");
			anchor.add(icon);
			Anchor anchor2 = new Anchor("https://www.openstreetmap.org"
					+ "/directions?from=&to=" + source.latitude.getValue() + "%2c" + source.longitude.getValue(),
					"View");
			anchor2.setTarget("_blank");
			layout.add(anchor, anchor2);
		} else {
			layout.add(icon);
		}
		return layout;
	}

	private Span getSummaryComponent(Place source) {
		Span layout = new Span();
		layout.addClassName("openstreetmapplace-grid-entry-summary");
		Label label = new Label(source.displayName.getValueWithDefaultValue("Location"));
		label.addClassName("openstreetmapplace-grid-entry-summary-label");
		TextField component = new TextField(
				"",
				source.description.getValueWithDefaultValue("Important Location"));
		component.addClassName("openstreetmapplace-grid-entry-summary-description");
		component.addValueChangeListener((event) -> {
			source.description.setValue(event.getValue());
		});
		layout.add(label, component);
		return layout;
	}

	public void setReadOnly(boolean b) {
	}

	private void addSelectedLocation(Place location) {
		try {
			getDatabase().insert(location);
			getDatabase().insert(new Changes(getCurrentUser(), location));
			grid.removeAll();
		} catch (SQLException ex) {
			sqlerror(ex);
		}

// Ultimately we need to inform our listeners that there is a new location
		fireEvent(new PlaceAddedEvent(this, false));
	}

	void clear() {
		grid.removeAll();
	}

	final void setItems(List<OpenStreetMapPlace> places) {
		places.forEach((source) -> {
			Div gridEntry = new Div();
			gridEntry.addClassName("openstreetmapplace-grid-entry");
			final Place place = new Place(taskID, source);
			System.out.println("" + place);
			gridEntry.add(getPrefixComponent(place));
			gridEntry.add(getSummaryComponent(place)
			);
			gridEntry.add(getSuffixComponent(place));
			grid.add(gridEntry);
		});
	}

	public Registration addPlaceAddedListener(
			ComponentEventListener<PlaceAddedEvent> listener) {
		return addListener(PlaceAddedEvent.class, listener);
	}
}
