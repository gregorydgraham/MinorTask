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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

	public ImageGrid(Long taskID) {
		this.taskID = taskID;
		makeComponent();
	}

	@SuppressWarnings("unchecked")
	private void makeComponent() {
		removeAll();
		setItems();
		getDatabase().print(allRows);
		grid.addComponentColumn((Images source) -> {
			Anchor anchor = new Anchor(new ImageStreamResource(source), "");
			anchor.setTarget("_blank");
			anchor.add(new Image(new ThumbnailStreamResource(source), source.filename.getValue()));
			return anchor;
		});
		grid.addColumn((Images source) -> source.filename.getValueWithDefaultValue(""));
		grid.addColumn((Images source) -> source.description.getValueWithDefaultValue(""));
		grid.addComponentColumn((Images source) -> new Button("remove", (event) -> {
			removeImage(source);
		}));
		final UploadImage uploader = new UploadImage(taskID);
		uploader.addImageAddedListener((event) -> {
			setItems();
		});
		add(grid);
		add(uploader);
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
			this.setHeight("300px");
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
}
