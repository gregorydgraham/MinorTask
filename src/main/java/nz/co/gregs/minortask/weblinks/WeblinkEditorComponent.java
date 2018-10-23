/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.weblinks;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import java.sql.SQLException;
import nz.co.gregs.minortask.components.HasDefaultButton;
import nz.co.gregs.minortask.components.RequiresLogin;
import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author gregorygraham
 */
public class WeblinkEditorComponent extends Div implements RequiresLogin, HasDefaultButton {

	private Long taskID;
	TextField locationText = new TextField("Web", "http://example.com/...");
	TextField descriptionText = new TextField("Description", "a useful website with many...");
	Button addButton = new Button("Add");
	private Registration defaultRegistration;

	public WeblinkEditorComponent() {
		super();
//		this.taskID = taskID;
		addClassName("weblink-editor-component");

		add(locationText);
		add(descriptionText);
		add(addButton);

//		locationText.setWidth("45%");
//		descriptionText.setWidth("45%");
		addButton.setEnabled(false);
		addButton.addClassName(DEFAULTBUTTON_CLASSNAME);

		addButton.addClickListener((event) -> {
			saveWeblink();
		});

		locationText.addValueChangeListener((event) -> {
			String[] schemes = {"http", "https", "ftp"}; // DEFAULT schemes = "http", "https", "ftp"
			UrlValidator urlValidator = new UrlValidator(schemes);
			if (urlValidator.isValid(event.getValue())) {
				System.out.println(event.getValue() + " URL is valid");
				locationText.setInvalid(false);
				addButton.setEnabled(true);
			} else {
				System.out.println(event.getValue() + " URL IS INVALID");
				locationText.setInvalid(true);
				addButton.setEnabled(false);
			}
		});

		locationText.addFocusListener((event) -> {
			addButton.isEnabled();
		});
	}

	public void setTaskID(Long id) {
		this.taskID = id;
	}

	private void saveWeblink() {
		addWeblink();
		removeAsDefaultButton(addButton, defaultRegistration);
		locationText.clear();
		descriptionText.clear();
		addButton.setEnabled(false);
	}

	private void addWeblink() {
		Weblink weblink = new Weblink();
		weblink.taskID.setValue(taskID);
		weblink.webURL.setValue(locationText.getValue());
		weblink.description.setValue(descriptionText.getValue());
		String iconURL = "";
		String[] bits = locationText.getValue().split("/");
		for (String bit : bits) {
			System.out.println("BIT: " + bit);
		}
		iconURL = bits[0] + "//" + bits[2] + "/favicon.ico";
		weblink.iconURL.setValue(iconURL);
		try {
			getDatabase().insert(weblink);
		} catch (SQLException ex) {
			sqlerror(ex);
		}

// Ultimately we need to inform our listeners that there is a new location
		fireEvent(new WeblinkAddedEvent(this, true));
	}

	public Registration addWeblinkAddedListener(
			ComponentEventListener<WeblinkAddedEvent> listener) {
		return addListener(WeblinkAddedEvent.class, listener);
	}

	public boolean isReadOnly() {
		return locationText.isReadOnly();
	}

	public void setReadOnly(boolean enabled) {
		locationText.setReadOnly(enabled);
		descriptionText.setReadOnly(enabled);
		addButton.setEnabled(false);
		removeAsDefaultButton(addButton, defaultRegistration);
	}

	@Override
	public void setEnabled(boolean enabled) {
		getElement().setEnabled(enabled);
		removeAsDefaultButton(addButton, defaultRegistration);
	}
}
