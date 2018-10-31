/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.documentupload;

import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.annotations.DBTableName;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBString;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
@DBTableName("task_document_link")
public class TaskDocumentLink extends DBRow {
	
	public TaskDocumentLink(){
		super();
	}

	@DBAutoIncrement
	@DBColumn("task_doc_id")
	public DBInteger taskDocumentLinkID = new DBInteger();

	@DBColumn("task_id")
	@DBForeignKey(Task.class)
	public DBInteger taskID = new DBInteger();
	
	@DBColumn("owner_id")
	@DBForeignKey(TaskDocumentLink.Owner.class)
	public DBInteger ownerID = new DBInteger();

	@DBColumn("doc_id")
	@DBForeignKey(Document.class)
	public DBInteger documentID = new DBInteger();

	@DBColumn("desc")
	public DBString description = new DBString();
	
	public static class Owner extends User{}

}
