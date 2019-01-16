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
public class Colleagues extends DBRow{
	
	
	@DBColumn
	@DBPrimaryKey
	@DBForeignKey(RequestingUser.class)
	public final DBInteger userID1 = new DBInteger();
	
	@DBColumn
	@DBPrimaryKey
	@DBForeignKey(RequestedUser.class)
	public final DBInteger userID2 = new DBInteger();
	
	@DBColumn
	public final DBDate requestDate = new DBDate();
	
	@DBColumn
	public final DBDate requestAcceptanceDate = new DBDate();

	public Colleagues(User user1, User user2) {
		userID1.setValue(user1.getUserID());
		userID2.setValue(user2.getUserID());
		requestDate.setValue(new Date());
	}

	public Colleagues() {
	}
	
	public static class RequestingUser extends User{
		
	}
	
	public static class RequestedUser extends User{
		
	}
}
