/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import nz.co.gregs.minortask.imageprocessing.ThumbnailStreamResource;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import java.sql.SQLException;
import nz.co.gregs.minortask.datamodel.Images;
import nz.co.gregs.minortask.events.ImageChangedEvent;
import nz.co.gregs.minortask.events.ImageDeletedEvent;
import nz.co.gregs.minortask.imageprocessing.ImageStreamResource;

/**
 *
 * @author gregorygraham
 */
@Tag("minortask-thumbnail")
public class ImageControl extends Component implements HasComponents, RequiresLogin {

	private final Long taskID;
	private final Images imageRow;

	public ImageControl(Long taskID, Images imageRow) {
		this.taskID = taskID;
		this.imageRow = imageRow;
		Anchor anchor = new Anchor(new ImageStreamResource(imageRow),"");
		Image image = new Image(new ThumbnailStreamResource(imageRow), imageRow.filename.getValue());
		anchor.add(image);
		Label description = new Label(imageRow.description.getValueWithDefaultValue(""));
		VerticalLayout layout = new VerticalLayout(anchor, description, new Button("remove", (event) -> {
			removeImage(event);
		}));
		add(layout);
	}

	private void removeImage(ClickEvent<Button> event) {
		try {
			getDatabase().delete(imageRow);
			fireEvent(new ImageDeletedEvent(this, false));
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	public Registration addImageDeletedListener(
			ComponentEventListener<ImageDeletedEvent> listener) {
		return addListener(ImageDeletedEvent.class, listener);
	}

	public Registration addImageChangedListener(
			ComponentEventListener<ImageChangedEvent> listener) {
		return addListener(ImageChangedEvent.class, listener);
	}
}
