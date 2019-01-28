/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.place;

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
import nz.co.gregs.minortask.components.SecureDiv;

/**
 *
 * @author gregorygraham
 */
public class PlaceGrid extends SecureDiv {

	private Long taskID;
	private final Grid<Place> grid = new Grid<Place>();
	private List<Place> allRows;
//	private PlaceSearchComponent searcher;

	public PlaceGrid() { 
	}
	
	public void setTaskID(Long taskID){
		this.taskID = taskID;
		makeComponent();
	}

	@SuppressWarnings("unchecked")
	private void makeComponent() {
		removeAll();
		addClassName("place-grid");
		setItems();
		getDatabase().print(allRows);
		grid.setHeightByRows(true);
		grid.addComponentColumn((Place source) -> getPlaceIconComponent(source)
		);
		grid.addComponentColumn((Place source) -> getDescriptionComponent(source)
		).setFlexGrow(20);
		grid.addComponentColumn((Place source) -> getRemoveComponent(source));
//		searcher = new PlaceSearchComponent(taskID);
//		searcher.addLocationAddedListener((event) -> {
//			setItems();
//		});
		add(grid);
//		add(searcher);
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
		TextField component = new TextField(
				"",
				source.description.getValueWithDefaultValue("Important Location"),
				(event) -> {
					updateDescription(source, event.getValue());
				});
		layout.add(label, component);
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setSpacing(false);
		return layout;
	}

	public void refresh() {
		setItems();
	}

	private void setItems() {
		try {
			Place example = new Place();
			example.taskID.permittedValues(this.taskID);
			allRows = getDatabase().getDBTable(example).getAllRows();
			this.setVisible(!allRows.isEmpty()); 
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

	public void setReadOnly(boolean b) {
//		searcher.setReadOnly(b); 
	}
}
