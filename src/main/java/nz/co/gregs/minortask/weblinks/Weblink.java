/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.weblinks;

import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.annotations.DBTableName;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBString;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
@DBTableName("weblinks")
public class Weblink extends DBRow {

	@DBPrimaryKey
	@DBAutoIncrement
	@DBColumn("weblink_id")
	public DBInteger locationID = new DBInteger();

	@DBColumn("task_id")
	@DBForeignKey(Task.class)
	public DBInteger taskID = new DBInteger();

	@DBColumn("weburl")
	public DBString webURL = new DBString();

	@DBColumn("iconurl")
	public DBString iconURL = new DBString();

	@DBColumn("description")
	public DBString description = new DBString();

	public Weblink() {
		super();
	}
}
