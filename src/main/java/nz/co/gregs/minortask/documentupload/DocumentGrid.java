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
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.components.RequiresLogin;

/**
 *
 * @author gregorygraham
 */
public class DocumentGrid extends VerticalLayout implements RequiresLogin {

	private Long taskID = null;
	private final Grid<Document> grid = new Grid<>();
	private List<Document> allRows;

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
		grid.addComponentColumn((Document source) -> getFileIconComponent(source));
		grid.addComponentColumn((Document source) -> getDescriptionComponent(source))
				.setFlexGrow(20);
		grid.addComponentColumn((Document source) -> getRemoveComponent(source));
		add(grid);
	}

	private Button getRemoveComponent(Document source) {
		return new Button(new Icon(VaadinIcon.TRASH), (event) -> removeDocument(source));
	}

	private Anchor getFileIconComponent(Document source) {
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

	private TextField getDescriptionComponent(Document source) {
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
			TaskDocumentLink link = new TaskDocumentLink();
			link.taskID.permittedValues(taskID);
			link.ownerID.permittedValues(getUserID());
			Document docExample = new Document();
			allRows = getDatabase().getDBQuery(link,docExample).getAllInstancesOf(docExample);
			this.setVisible(!allRows.isEmpty()); 
			grid.setItems(allRows);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void removeDocument(Document doc) {
		try {
			TaskDocumentLink link = new TaskDocumentLink();
			link.documentID.permittedValues(doc.documentID);
			link.taskID.permittedValues(taskID);
			link.ownerID.permittedValues(getUserID());
			getDatabase().delete(link);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		setItems();
	}

	private void updateDescription(Document source, String value) {
		source.description.setValue(value);
		try {
			getDatabase().update(source);
			Globals.savedNotice();
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public void refresh() {
		setItems();
	}

	public void setReadOnly(boolean b) {
	}
}
