/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;


public class ImageSelector extends DocumentSelector {

	public ImageSelector(Long taskID) {
		super(taskID);
	}

	public ImageSelector() {
		super();
	}

	@Override
	protected Document getDocumentExampleForSelector() {
		Document docExample = new Document();
		docExample.userID.permittedValues(getUserID());
		docExample.mediaType.permittedPattern("image/%");
		return docExample;
	}
	
}
