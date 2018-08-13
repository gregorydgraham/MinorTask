/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.GeneratedVaadinTextArea.ChangeEvent;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.SQLException;
import nz.co.gregs.minortask.datamodel.Images;
import nz.co.gregs.minortask.events.ImageChangedEvent;
import nz.co.gregs.minortask.events.ImageDeletedEvent;

/**
 *
 * @author gregorygraham
 */
@Tag("minortask-thumbnail")
public class ImageControl extends Component implements HasComponents, RequiresLogin {

	private final Long taskID;
	private final Image image;
	private final VerticalLayout layout;
	private final Label description;
	private final Images imageRow;

	public ImageControl(Long taskID, Images imageRow) {
		this.taskID = taskID;
		this.imageRow = imageRow;
		this.image = new Image(new ImageStreamResource(imageRow), "Task Image");
		this.description = new Label(imageRow.description.getValueWithDefaultValue(""));
		this.layout = new VerticalLayout(image, description, new Button("remove", (event) -> {
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

	public static class ImageStreamResource extends StreamResource {

		private final Images originalImage;

		public ImageStreamResource(Images image) {
			super(image.filename.getValue(), new InputStreamFactory() {
				@Override
				public InputStream createInputStream() {
					final InputStream inputStream = image.imageContents.getInputStream();
					
					return inputStream;
				}
			});
			originalImage = image;
		}

		public Images getOriginalImageRow() {
			return originalImage;
		}
	}
}
