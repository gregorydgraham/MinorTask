/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.place;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.SecureDiv;

/**
 *
 * @author gregorygraham
 */
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
		addClassName("place-grid");
		add(grid);
	}

	private Span getSuffixComponent(Place source) {
		return new Span(new Button("Add", new Icon(VaadinIcon.PLUS_CIRCLE), (event) -> addSelectedLocation(source)));
	}

	private Span getPrefixComponent(Place source) {
		Component icon = new Icon(VaadinIcon.MAP_MARKER);
		String value = source.iconURL.getValue();
		if (value != null && !value.isEmpty()) {
			System.out.println("URL VALUE: \"" + source.iconURL.getValue() + "\"");
			icon = new Image(source.iconURL.getValue(), "");
		}

		if (source.latitude.isNotNull() && source.longitude.isNotNull()) {
			Span layout = new Span();
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
			return layout;
		} else {
			return new Span(icon);
		}
	}

	private Span getSummaryComponent(Place source) {
		Span layout = new Span();
		Label label = new Label(source.displayName.getValueWithDefaultValue("Location"));
		TextField component = new TextField(
				"",
				source.description.getValueWithDefaultValue("Important Location"));
		layout.add(label, component);
		return layout;
	}

//	private void updateDescription(Place source, String value) {
//		source.description.setValue(value);
//		try {
//			getDatabase().update(source);
//			MinorTask.chat("Saved");
//		} catch (SQLException ex) {
//			sqlerror(ex);
//		}
//	}

	public void setReadOnly(boolean b) {
	}

	private void addSelectedLocation(Place location) {
		try {
			getDatabase().insert(location);
		} catch (SQLException ex) {
			sqlerror(ex);
		}

// Ultimately we need to inform our listeners that there is a new location
		fireEvent(new PlaceAddedEvent(this, true));
	}

	void clear() {
		grid.removeAll();
	}

	final void setItems(List<OpenStreetMapPlace> places) {
		places.forEach((source) -> {
			Div gridEntry = new Div();
			final Place place = new Place(taskID, source);
			System.out.println(""+place);
			gridEntry.add(getPrefixComponent(place));
			gridEntry.add(getSummaryComponent(place)
			);
			gridEntry.add(getSuffixComponent(place));
			grid.add(gridEntry);
		});
	}
}
