/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.datamodel;

import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.datatypes.DBDate;
import nz.co.gregs.dbvolution.datatypes.DBInteger;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class TaskViews extends DBRow{
	
	
	@DBColumn(value = "taskid")
	@DBForeignKey(Task.class)
	public final DBInteger taskID = new DBInteger();

	@DBColumn
	@DBForeignKey(User.class)
	public final DBInteger userID = new DBInteger();
	
	@DBColumn
	public final DBDate lastviewed = new DBDate();
}
