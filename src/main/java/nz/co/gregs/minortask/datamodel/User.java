/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.datamodel;

import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.datatypes.DBDate;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBString;
import nz.co.gregs.dbvolution.datatypes.DBStringTrimmed;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class User extends DBRow {

	@DBColumn
	@DBPrimaryKey
	@DBAutoIncrement
	public DBInteger userID = new DBInteger();

	@DBColumn
	public DBStringTrimmed username = new DBStringTrimmed();

	@DBColumn
	public DBStringTrimmed email = new DBStringTrimmed();

	@DBColumn
	public DBString password = new DBString();
	
	@DBColumn
	public DBDate signupDate=new DBDate();
	
	@DBColumn
	public DBDate lastLoginDate=new DBDate();
}
