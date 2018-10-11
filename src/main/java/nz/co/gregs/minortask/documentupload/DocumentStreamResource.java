/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.documentupload;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import java.io.InputStream;
import nz.co.gregs.minortask.documentupload.TaskDocument;

/**
 *
 * @author gregorygraham
 */
public class DocumentStreamResource extends StreamResource {
	
	private final TaskDocument originalDoc;

	public DocumentStreamResource(TaskDocument doc) {
		super(doc.filename.getValue(), new DocumentInputStreamFactory(doc));
		originalDoc = doc;
	}

	public TaskDocument getOriginalDocumentRow() {
		return originalDoc;
	}

	public static class DocumentInputStreamFactory implements InputStreamFactory {

		private final TaskDocument doc;

		public DocumentInputStreamFactory(TaskDocument doc) {
			this.doc = doc;
		}

		@Override
		public InputStream createInputStream() {
			return doc.documentContents.getInputStream();
		}
	}
	
}
