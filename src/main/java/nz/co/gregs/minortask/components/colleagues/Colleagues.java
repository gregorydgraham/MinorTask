/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.colleagues;

import java.util.Date;
import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.datatypes.DBDate;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class Colleagues extends DBRow{
	
	
	@DBColumn("userid1")
	@DBPrimaryKey
	@DBForeignKey(RequestingUser.class)
	public final DBInteger requestor = new DBInteger();
	
	@DBColumn("userid2")
	@DBPrimaryKey
	@DBForeignKey(InvitedUser.class)
	public final DBInteger invited = new DBInteger();
	
	@DBColumn
	public final DBDate invitationDate = new DBDate();
	
	@DBColumn
	public final DBDate acceptanceDate = new DBDate();
	
	@DBColumn
	public final DBDate denialDate = new DBDate();

	public Colleagues(User user1, User user2) {
		requestor.setValue(user1.getUserID());
		invited.setValue(user2.getUserID());
		invitationDate.setValue(new Date());
	}

	public Colleagues() {
	}
	
	public static class RequestingUser extends User{
		
	}
	
	public static class InvitedUser extends User{
		
	}
}
