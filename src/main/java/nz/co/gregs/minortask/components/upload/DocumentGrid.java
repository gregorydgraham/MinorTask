/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import nz.co.gregs.minortask.components.images.ImageIconFromDocument;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.task.SecureTaskDiv;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@Tag("document-grid")
public class DocumentGrid extends SecureTaskDiv {

	private final Grid<Document> grid = new Grid<>();
	private final List<Document> allRows = new ArrayList<Document>();

	public DocumentGrid() {
		makeComponent();
	}

	@Override
	public final void setTaskAndProject(Task.TaskAndProject task) {
		super.setTaskAndProject(task);
		refresh();
	}

	@SuppressWarnings("unchecked")
	private void makeComponent() {
		removeAll();
		grid.setHeightByRows(true);
		grid.addComponentColumn((Document source) -> getFileIconComponent(source));
		grid.addComponentColumn((Document source) -> getDescriptionComponent(source))
				.setFlexGrow(20);
		grid.addComponentColumn((Document source) -> getRemoveComponent(source));
		grid.setItems(allRows.stream());
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
			icon = new ImageIconFromDocument(source);
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

	private void removeDocument(Document doc) {
		try {
			TaskDocumentLink link = new TaskDocumentLink();
			link.documentID.permittedValues(doc.documentID);
			link.taskID.permittedValues(getTaskID());
			link.ownerID.permittedValues(getCurrentUserID());
			getDatabase().delete(link);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		refresh();
	}

	private void updateDescription(Document source, String value) {
		source.description.setValue(value);
		try {
			getDatabase().update(source);
			Globals.savedNotice();
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		refresh();
	}

	public void refresh() {
		allRows.clear();
		grid.setItems(allRows);
		queryDatabase();
		grid.setItems(allRows.stream());
	}

	public void setReadOnly(boolean b) {
	}

	public int getCountOfRows() {
		return allRows.size();
	}

	private void queryDatabase() {
		try {
			TaskDocumentLink link = new TaskDocumentLink();
			link.taskID.permittedValues(getTaskID());
			link.ownerID.permittedValues(getCurrentUserID());
			Document docExample = new Document();
			allRows.clear();
			allRows.addAll(getDatabase()
					.getDBQuery(link, docExample)
					.setSortOrder(link.column(link.documentID).ascending())
					.getAllInstancesOf(docExample)
			);
		} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
			sqlerror(ex);
		}
	}
}
