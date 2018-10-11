/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.place;

import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.annotations.DBTableName;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBLargeText;
import nz.co.gregs.dbvolution.datatypes.DBNumber;
import nz.co.gregs.dbvolution.datatypes.DBString;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
@DBTableName("locations")
public class Place extends DBRow {

	@DBPrimaryKey
	@DBAutoIncrement
	@DBColumn("location_id")
	public DBInteger locationID = new DBInteger();

	@DBColumn("task_id")
	@DBForeignKey(Task.class)
	public DBInteger taskID = new DBInteger();

	@DBColumn("address")
	public DBString displayName = new DBString();

	@DBColumn("url")
	public DBString iconURL = new DBString();

	@DBColumn("description")
	public DBString description = new DBString();
	@DBColumn
	public DBInteger placeID = new DBInteger();
	@DBColumn
	public DBString osmType = new DBString();
	@DBColumn
	public DBInteger osmID = new DBInteger();
	@DBColumn
	public DBInteger placeRank = new DBInteger();
	@DBColumn
	public DBString boundingBox = new DBString();
	@DBColumn("polygon_pts")
	public DBLargeText polygonPoints = new DBLargeText();
	@DBColumn
	public DBNumber latitude = new DBNumber();
	@DBColumn
	public DBNumber longitude = new DBNumber();
	@DBColumn
	public DBNumber osmImportance = new DBNumber();

	public Place() {
		super();
	}

	/*
		private String placeID;
		private String osmType;
		private String osmID;
		private String placeRank;
		private String boundingBox;
		private String polygonPoints;
		private String latitude;
		private String longitude;
		private String displayName;
		--private String type;
		private String importance;
		private String iconURL;
	 */

	public Place(Long taskID, OpenStreetMapPlace place) {
		this.taskID.setValue(taskID);
		displayName.setValue(place.getDisplayName());
		iconURL.setValue(place.getIconURL());
		placeID.setValue(place.getPlaceID());
		osmType.setValue(place.getOsmType());
		osmID.setValue(place.getOsmID());
		placeRank.setValue(place.getPlaceRank());
		boundingBox.setValue(place.getBoundingBox());
		polygonPoints.setValue(place.getPolygonPoints());
		latitude.setValue(place.getLatitude());
		longitude.setValue(place.getLongitude());
		osmImportance.setValue(place.getImportance());
	}
}
