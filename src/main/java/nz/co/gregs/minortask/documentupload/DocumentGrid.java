/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.documentupload;

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
import nz.co.gregs.minortask.components.RequiresLogin;

/**
 *
 * @author gregorygraham
 */
public class DocumentGrid extends VerticalLayout implements RequiresLogin {

	private Long taskID = null;
	private final Grid<TaskDocument> grid = new Grid<TaskDocument>();
	private List<TaskDocument> allRows;
//	private DocumentUpload uploader;

	public DocumentGrid() {
	}
	
	public final void setTaskID(Long taskID){
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
		grid.addComponentColumn((TaskDocument source) -> getFileIconComponent(source));
		grid.addComponentColumn((TaskDocument source) -> getDescriptionComponent(source))
				.setFlexGrow(20);
		grid.addComponentColumn((TaskDocument source) -> getRemoveComponent(source));
//		uploader = new DocumentUpload(taskID);
//		uploader.addDocumentAddedListener((event) -> {
//			setItems();
//		});
		add(grid);
//		add(uploader);
	}

	private Button getRemoveComponent(TaskDocument source) {
		return new Button(new Icon(VaadinIcon.TRASH), (event) -> removeDocument(source));
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

	public void setReadOnly(boolean b) {
//		uploader.setEnabled(!b);
	}

	public void refresh() {
		setItems();
	}
}
