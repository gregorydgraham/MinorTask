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
import nz.co.gregs.dbvolution.annotations.DBTableName;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBLargeBinary;
import nz.co.gregs.dbvolution.datatypes.DBString;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
@DBTableName("thumbnails")
public class Thumbnail extends DBRow {

	@DBPrimaryKey
	@DBAutoIncrement
	@DBColumn("thumbnail_id")
	public DBInteger thumbnailID = new DBInteger();

	@DBColumn("image_id")
	@DBForeignKey(Images.class)
	public DBInteger imageID = new DBInteger();

	@DBColumn("image_contents")
	public DBLargeBinary imageContents = new DBLargeBinary();

	@DBColumn("mediatype")
	public DBString mediaType = new DBString();

	@DBColumn("filename")
	public DBString filename = new DBString();
}
