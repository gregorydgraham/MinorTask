/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.minortask.components.RequiresLogin;

/**
 *
 * @author gregorygraham
 */
@Tag("document-upload")
public class DocumentSelector extends Div implements RequiresLogin {

	Button addExistingDoc = new Button("Attach Existing ...");
	ComboBox<Document> existingDocSelector = new ComboBox<>();
	protected Long taskID;

	public DocumentSelector(Long taskID) {
		this();
		this.taskID = taskID;
	}

	public DocumentSelector() {
		addExistingDoc.addClickListener((event) -> {
			showSelector(event);
		});
		add(addExistingDoc);

		existingDocSelector.setItemLabelGenerator((Document item) -> item.filename + ": " + item.description);
		existingDocSelector.getStyle().set("display", "none");
		existingDocSelector.addValueChangeListener((event) -> {
			addSelectedItem(event);
		});
		add(existingDocSelector);
	}

	public final void setTaskID(Long id) {
		this.taskID = id;
	}

	private void showSelector(ClickEvent<Button> event) {
		existingDocSelector.clear();
		Document docExample = getDocumentExampleForSelector();
		TaskDocumentLink linkExample = new TaskDocumentLink();
		linkExample.taskID.permittedValues(taskID);
		try {
			final DBQuery query = getDatabase().getDBQuery(docExample).addOptional(linkExample);
			query.addCondition(linkExample.column(linkExample.taskDocumentLinkID).isNull());
			List<Document> instances = query.getAllInstancesOf(docExample);
			getDatabase().print(instances);
			existingDocSelector.setItems(instances);
			existingDocSelector.getStyle().set("display", "inline-block");
		} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
			Logger.getLogger(DocumentSelector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	protected Document getDocumentExampleForSelector() {
		Document docExample = new Document();
		docExample.userID.permittedValues(getUserID());
		docExample.mediaType.excludedPattern("image/%");
		return docExample;
	}

	private void addSelectedItem(AbstractField.ComponentValueChangeEvent<ComboBox<Document>, Document> event) {
		Document doc = event.getValue();
		if (doc != null) {
			existingDocSelector.clear();
			existingDocSelector.getStyle().set("display", "none");
			fireEvent(new DocumentAddedEvent(this, doc, false));
		}
	}

	public Registration addDocumentAddedListener(
			ComponentEventListener<DocumentAddedEvent> listener) {
		return addListener(DocumentAddedEvent.class, listener);
	}
}
