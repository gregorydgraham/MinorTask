/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.datamodel;

import java.util.Date;
import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.datatypes.DBDate;
import nz.co.gregs.dbvolution.datatypes.DBInteger;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class TaskAssignment extends DBRow{
	
	
	@DBColumn(value = "taskid")
	@DBPrimaryKey
	@DBForeignKey(Task.class)
	public final DBInteger taskID = new DBInteger();

	@DBColumn
	@DBPrimaryKey
	@DBForeignKey(User.class)
	public final DBInteger userID = new DBInteger();
	
	@DBColumn
	public final DBDate invitationDate = new DBDate();
	
	@DBColumn
	public final DBDate acceptanceDate = new DBDate();
	
	@DBColumn
	public final DBDate declinedDate = new DBDate();

	public TaskAssignment(Task task, User user) {
		taskID.setValue(task.taskID);
		userID.setValue(user.getUserID());
		invitationDate.setValue(new Date());
	}

	public TaskAssignment() {
	}
}
