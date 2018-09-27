/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.datamodel;

import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.*;
import nz.co.gregs.dbvolution.datatypes.*;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class PasswordResetRequests extends DBRow{
	
	@DBAutoIncrement
	@DBPrimaryKey
	@DBColumn
	public DBInteger resetId = new DBInteger();
	
	@DBColumn
	public DBString resetCode = new DBString();
	
	@DBColumn
	@DBForeignKey(User.class)
	public DBInteger userId = new DBInteger();
	
	@DBColumn
	public DBDate expiryTime = new DBDate();
	
}
