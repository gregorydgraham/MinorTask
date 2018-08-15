/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.minortask.datamodel.Images;
import nz.co.gregs.minortask.imageprocessing.ImageStreamResource;
import nz.co.gregs.minortask.imageprocessing.ThumbnailStreamResource;

/**
 *
 * @author gregorygraham
 */
public class ImageGrid extends VerticalLayout implements RequiresLogin {

	private final Long taskID;
	private Grid<Images> grid = new Grid<Images>();
	private List<Images> allRows;
	private UploadImage uploader;

	public ImageGrid(Long taskID) {
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
				(Images source) -> getImageComponent(source)
		);
		grid.addComponentColumn(
				(Images source) -> getDescriptionComponent(source)
		).setFlexGrow(10);
		grid.addComponentColumn((Images source) -> getRemoveComponent(source));
		uploader = new UploadImage(taskID);
		uploader.addImageAddedListener((event) -> {
			setItems();
		});
		add(grid);
		add(uploader);
	}

	private Button getRemoveComponent(Images source) {
		return new Button("remove", (event) -> removeImage(source));
	}

	private Anchor getImageComponent(Images source) {
		Anchor anchor = new Anchor(new ImageStreamResource(source), "");
		anchor.setTarget("_blank");
		anchor.add(new Image(new ThumbnailStreamResource(source), source.filename.getValue()));
		return anchor;
	}

	private TextField getDescriptionComponent(Images source) {
		TextField component = new TextField(
				"",
				source.description.getValueWithDefaultValue(source.filename.getValueWithDefaultValue("...")),
				(event) -> {
					updateDescription(source, event.getValue());
				});
		component.setWidth("100%");
		return component;
	}

	private void setItems() {
		try {
			Images example = new Images();
			example.taskID.permittedValues(this.taskID);
			example.userID.permittedValues(minortask().getUserID());
			allRows = getDatabase().getDBTable(example).getAllRows();
			grid.setItems(allRows);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		if (allRows.isEmpty()) {
			this.setSizeUndefined();
			grid.setSizeUndefined();
		} else {
			this.setHeight(""+((allRows.size()*50+100))+"px");
		}
		this.setWidth("100%");
	}

	private void removeImage(Images img) {
		try {
			getDatabase().delete(img);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		setItems();
	}

	private void updateDescription(Images source, String value) {
		source.description.setValue(value);
		try {
			getDatabase().update(source);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}
}
