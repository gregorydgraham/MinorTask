/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.datamodel.Location;

/**
 *
 * @author gregorygraham
 */
public class LocationGrid extends VerticalLayout implements RequiresLogin {

	private final Long taskID;
	private final Grid<Location> grid = new Grid<Location>();
	private List<Location> allRows;
	private LocationSearchComponent searcher;

	public LocationGrid(Long taskID) {
		this.taskID = taskID;
		makeComponent();
	}

	@SuppressWarnings("unchecked")
	private void makeComponent() {
		removeAll();
		setMargin(false);
		setPadding(false);
		setSpacing(false);
		setItems();
		getDatabase().print(allRows);
		grid.addComponentColumn(
				(Location source) -> getLocationIconComponent(source)
		);
		grid.addComponentColumn(
				(Location source) -> getDescriptionComponent(source)
		).setFlexGrow(20);
		grid.addComponentColumn((Location source) -> getRemoveComponent(source));
		searcher = new LocationSearchComponent(taskID);
		searcher.addLocationAddedListener((event) -> {
			setItems();
		});
		add(grid);
		add(searcher);
	}

	private Button getRemoveComponent(Location source) {
		return new Button(new Icon(VaadinIcon.RECYCLE), (event) -> removeLocation(source));
	}

	private Component getLocationIconComponent(Location source) {
		Component icon = new Icon(VaadinIcon.MAP_MARKER);
		String value = source.iconURL.getValue();
		if (value != null && !value.isEmpty()) {
			System.out.println("URL VALUE: \"" + source.iconURL.getValue() + "\"");
			icon = new Image(source.iconURL.getValue(), "");
		}
		/*
		https://www.openstreetmap.org/search?query=-41.28654%2C174.77598#map=19/-41.28654/174.77598
		 */
		if (source.latitude.isNotNull() && source.longitude.isNotNull()) {
			Anchor anchor = new Anchor("https://www.openstreetmap.org"
					+"/directions?from=&to=" + source.latitude.getValue() + "%2c" + source.longitude.getValue(),
					"View");
			anchor.setTarget("_blank");
			anchor.add(icon);
			return anchor;
		} else {
			return icon;
		}
	}

	private TextField getDescriptionComponent(Location source) {
		TextField component = new TextField(
				"",
				source.description.getValueWithDefaultValue("Location"),
				(event) -> {
					updateDescription(source, event.getValue());
				});
		component.setWidth("100%");
		return component;
	}

	private void setItems() {
		try {
			Location example = new Location();
			example.taskID.permittedValues(this.taskID);
//			example.userID.permittedValues(minortask().getUserID());
			allRows = getDatabase().getDBTable(example).getAllRows();
			grid.setItems(allRows);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		if (allRows.isEmpty()) {
			this.setSizeUndefined();
			grid.setHeight("2px");
		} else {
			this.setHeight("" + ((allRows.size() * 50 + 100)) + "px");
		}
		this.setWidth("100%");
	}

	private void removeLocation(Location locn) {
		try {
			getDatabase().delete(locn);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		setItems();
	}

	private void updateDescription(Location source, String value) {
		source.description.setValue(value);
		try {
			getDatabase().update(source);
			minortask().chat("Saved");
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}
}
