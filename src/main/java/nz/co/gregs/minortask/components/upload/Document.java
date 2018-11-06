/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.upload;

import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.annotations.DBTableName;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBLargeBinary;
import nz.co.gregs.dbvolution.datatypes.DBString;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
@DBTableName("documents")
public class Document extends DBRow {

	@DBPrimaryKey
	@DBAutoIncrement
	@DBColumn("document_id")
	public DBInteger documentID = new DBInteger();

	@DBColumn("task_id")
	@DBForeignKey(Task.class)
	public DBInteger taskID = new DBInteger();
	
	@DBColumn("owner_id")
	@DBForeignKey(User.class)
	public DBInteger userID = new DBInteger();

	@DBColumn("document_contents")
	public DBLargeBinary documentContents = new DBLargeBinary();

	@DBColumn("mediatype")
	public DBString mediaType = new DBString();

	@DBColumn("filename")
	public DBString filename = new DBString();
	
	@DBColumn("description")
	public DBString description = new DBString();
}
