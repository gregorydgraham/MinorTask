/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.style;

import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.*;
import nz.co.gregs.dbvolution.datatypes.*;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@DBTableName("userstyles")
public class StyleDefinition extends DBRow{
	
	@DBAutoIncrement
	@DBColumn
	@DBPrimaryKey
	DBInteger userStylePK = new  DBInteger();
	
	@DBForeignKey(User.class)
	@DBColumn
	DBInteger userID = new DBInteger();
	
	@DBColumn
	DBString selector = new DBString();
	
	@DBColumn
	DBString backgroundColour = new DBString();
	
	@DBColumn
	DBString colour = new DBString();
	
	@DBColumn
	DBString border = new DBString();
	
	@DBColumn
	DBString borderRadius = new DBString();
	
	@DBColumn
	DBString padding = new DBString();
	
	@DBColumn
	DBString margin = new DBString();
	
	@DBColumn
	DBString fontSize = new DBString();
	
	@DBColumn
	DBString fontName = new DBString();
	
}
