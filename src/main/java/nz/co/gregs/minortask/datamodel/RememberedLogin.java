/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.datamodel;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.actions.DBActionList;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.dbvolution.datatypes.DBDate;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBLocalDateTime;
import nz.co.gregs.dbvolution.datatypes.DBString;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class RememberedLogin extends DBRow {

	@DBColumn
	@DBPrimaryKey
	@DBAutoIncrement
	public DBInteger pk = new DBInteger();
	
	@DBColumn
	@DBForeignKey(User.class)
	public DBInteger userid = new DBInteger();
	
	@DBColumn
	public DBString rememberCode = new DBString();
	
	@DBColumn
	public DBLocalDateTime expires = new DBLocalDateTime();

	public RememberedLogin() {
		super();
	}

	public RememberedLogin(Long userID, String identifier, LocalDateTime expiryDate) {
		this();
		this.userid.setValue(userID);
		this.rememberCode.setValue(identifier);
		this.expires.setValue(expiryDate);
	}
	
	public static DBActionList cleanUpTable(DBDatabase db) throws SQLException{
		RememberedLogin example = new RememberedLogin();
		example.expires.permittedRangeExclusive(null, LocalDateTime.now().minusDays(30));
		db.print(db.getByExample(example));
		return db.delete(example);
	}
	
}
