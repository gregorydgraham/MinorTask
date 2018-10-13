/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.place;

import nz.co.gregs.minortask.place.PlaceSearchComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.components.RequiresLogin;

/**
 *
 * @author gregorygraham
 */
public class PlaceGrid extends VerticalLayout implements RequiresLogin {

	private final Long taskID;
	private final Grid<Place> grid = new Grid<Place>();
	private List<Place> allRows;
	private PlaceSearchComponent searcher;

	public PlaceGrid(Long taskID) {
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
		grid.setHeightByRows(true);
		grid.addComponentColumn((Place source) -> getPlaceIconComponent(source)
		);
		grid.addComponentColumn((Place source) -> getDescriptionComponent(source)
		).setFlexGrow(20);
		grid.addComponentColumn((Place source) -> getRemoveComponent(source));
		searcher = new PlaceSearchComponent(taskID);
		searcher.addLocationAddedListener((event) -> {
			setItems();
		});
		add(grid);
		add(searcher);
	}

	private Button getRemoveComponent(Place source) {
		return new Button(new Icon(VaadinIcon.TRASH), (event) -> removePlace(source));
	}

	private Component getPlaceIconComponent(Place source) {
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
			HorizontalLayout layout = new HorizontalLayout();
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
			layout.setMargin(false);
			layout.setPadding(false);
			layout.setSpacing(false);
			return layout;
		} else {
			return icon;
		}
	}

	private Component getDescriptionComponent(Place source) {
		VerticalLayout layout = new VerticalLayout();
		Label label = new Label(source.displayName.getValueWithDefaultValue("Location"));
		label.setWidth("100%");
		TextField component = new TextField(
				"",
				source.description.getValueWithDefaultValue("Important Location"),
				(event) -> {
					updateDescription(source, event.getValue());
				});
		component.setWidth("100%");
		layout.add(label, component);
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setSpacing(false);
		return layout;
	}

	private void setItems() {
		try {
			Place example = new Place();
			example.taskID.permittedValues(this.taskID);
			allRows = getDatabase().getDBTable(example).getAllRows();
			grid.setItems(allRows);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void removePlace(Place locn) {
		try {
			getDatabase().delete(locn);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		setItems();
	}

	private void updateDescription(Place source, String value) {
		source.description.setValue(value);
		try {
			getDatabase().update(source);
			minortask().chat("Saved");
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}
}
