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
import nz.co.gregs.minortask.datamodel.TaskDocument;
import nz.co.gregs.minortask.streamresources.DocumentIconStreamResource;
import nz.co.gregs.minortask.streamresources.DocumentStreamResource;

/**
 *
 * @author gregorygraham
 */
public class DocumentGrid extends VerticalLayout implements RequiresLogin {

	private final Long taskID;
	private Grid<TaskDocument> grid = new Grid<TaskDocument>();
	private List<TaskDocument> allRows;
	private UploadDocument uploader;

	public DocumentGrid(Long taskID) {
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
		grid.addComponentColumn((TaskDocument source) -> getFileIconComponent(source)
		);
		grid.addComponentColumn((TaskDocument source) -> getDescriptionComponent(source)
		).setFlexGrow(10);
		grid.addComponentColumn((TaskDocument source) -> getRemoveComponent(source));
		uploader = new UploadDocument(taskID);
		uploader.addDocumentAddedListener((event) -> {
			setItems();
		});
		add(grid);
		add(uploader);
	}

	private Button getRemoveComponent(TaskDocument source) {
		return new Button("remove", (event) -> removeDocument(source));
	}

	private Anchor getFileIconComponent(TaskDocument source) {
		Anchor anchor = new Anchor(new DocumentStreamResource(source), "");
		anchor.setTarget("_blank");
		Component icon;
		if (source.mediaType.getValue().startsWith("image/")) {
			icon = new Image(new DocumentIconStreamResource(source), source.filename.getValue());
		} else {
			icon = new Icon(VaadinIcon.FILE);
		}
		anchor.add(icon);
		return anchor;
	}

	private TextField getDescriptionComponent(TaskDocument source) {
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
			TaskDocument example = new TaskDocument();
			example.taskID.permittedValues(this.taskID);
			example.userID.permittedValues(minortask().getUserID());
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

	private void removeDocument(TaskDocument img) {
		try {
			getDatabase().delete(img);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		setItems();
	}

	private void updateDescription(TaskDocument source, String value) {
		source.description.setValue(value);
		try {
			getDatabase().update(source);		
			minortask().chat("Saved");
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}
}
