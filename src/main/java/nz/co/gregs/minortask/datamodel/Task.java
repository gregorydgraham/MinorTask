/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.datamodel;

import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.datatypes.DBDate;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBString;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class Task extends DBRow {

	@DBColumn(value = "taskid")
	@DBPrimaryKey
	@DBAutoIncrement
	public DBInteger taskID = new DBInteger();

	@DBColumn
	@DBForeignKey(User.class)
	public DBInteger userID = new DBInteger();

	@DBColumn
	@DBForeignKey(Project.class)
	public DBInteger projectID = new DBInteger();

	@DBColumn
	public DBString name = new DBString();

	@DBColumn
	public DBString description = new DBString();

	@DBColumn
	public DBDate startDate = new DBDate();

	@DBColumn
	public DBDate preferredDate = new DBDate();

	@DBColumn
	public DBDate finalDate = new DBDate();

//	@DBColumn
//	public DBLargeText notes = new DBLargeText();

}
