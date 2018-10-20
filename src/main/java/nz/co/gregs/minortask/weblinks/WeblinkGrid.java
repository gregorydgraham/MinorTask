/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.weblinks;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
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
public class WeblinkGrid extends VerticalLayout implements RequiresLogin {

	private Long taskID;
	private final Grid<Weblink> grid = new Grid<Weblink>();
	private List<Weblink> allRows;
	private WeblinkEditorComponent searcher;

	public WeblinkGrid() {
	}

	public final void setTaskID(Long taskID) {
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
		grid.addComponentColumn((source) -> getAnchorComponent(source)
		).setFlexGrow(20);
		grid.addComponentColumn((source) -> getDescriptionComponent(source)
		).setFlexGrow(20);
		grid.addComponentColumn((source) -> getRemoveComponent(source));
		searcher = new WeblinkEditorComponent(taskID);
		searcher.addWeblinkAddedListener((event) -> {
			setItems();
		});
		add(grid);
		add(searcher);
	}

	private Button getRemoveComponent(Weblink source) {
		final Button button = new Button(new Icon(VaadinIcon.TRASH), (event) -> removePlace(source));
		button.setEnabled(this.isEnabled()&&!this.isReadOnly());
		return button;
	}

	private Component getAnchorComponent(Weblink source) {
		Component icon = new Icon(VaadinIcon.MAP_MARKER);
		String value = source.iconURL.getValue();
		if (value != null && !value.isEmpty()) {
			System.out.println("URL VALUE: \"" + source.iconURL.getValue() + "\"");
			icon = new Image(source.iconURL.getValue(), "");
		}

		HorizontalLayout layout = new HorizontalLayout();
		Anchor iconAnchor = new Anchor(source.webURL.getValue(), "");
		iconAnchor.setTarget("_blank");
		iconAnchor.add(icon);
		Anchor urlAnchor = new Anchor(source.webURL.getValue(), source.webURL.getValue().replaceAll("http[s]*://", ""));
		urlAnchor.setTarget("_blank");
		layout.add(iconAnchor, urlAnchor);
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setSpacing(false);
		return layout;
	}

	private Component getDescriptionComponent(Weblink source) {
		VerticalLayout layout = new VerticalLayout();
		TextField component = new TextField(
				"",
				source.description.getValueWithDefaultValue("Important Location"),
				(event) -> {
					updateDescription(source, event.getValue());
				});
		component.setWidth("100%");
		layout.add(component);
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setSpacing(false);
		return layout;
	}

	private void setItems() {
		try {
			Weblink example = new Weblink();
			example.taskID.permittedValues(this.taskID);
			allRows = getDatabase().getDBTable(example).getAllRows();
			grid.setItems(allRows);
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
			minortask().chat("Saved");
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public boolean isReadOnly() {
		return searcher.isReadOnly(); 
	}

	public void setReadOnly(boolean readonly) {
		searcher.setEnabled(!readonly);
		grid.setEnabled(!readonly);
	}

	@Override
	public void setEnabled(boolean enabled) {
		getElement().setEnabled(enabled);
		searcher.setEnabled(enabled);
	}
}
