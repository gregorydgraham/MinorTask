/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.utilities;

import java.io.Serializable;
import org.w3c.dom.Element;

/**
 *
 * @author gregorygraham
 */
public class OpenStreetMapPlace implements Serializable {
	
	public static final long serialVersionUID = 1l;
	private boolean validPlace = false;
	private String placeID;
	private String osmType;
	private String osmID;
	private String placeRank;
	private String boundingBox;
	private String polygonPoints;
	private String latitude;
	private String longitude;
	private String displayName;
	private String type;
	private String importance;
	private String iconURL;

	public OpenStreetMapPlace(Element node) {
		if (node.getNodeName().equals("place")) {
			validPlace = true;
			placeID = node.getAttribute("place_id");
			osmType = node.getAttribute("osm_type");
			osmID = node.getAttribute("osm_id");
			placeRank = node.getAttribute("place_rank");
			boundingBox = node.getAttribute("boundingbox");
			polygonPoints = node.getAttribute("polygonpoints");
			latitude = node.getAttribute("lat");
			longitude = node.getAttribute("lon");
			displayName = node.getAttribute("display_name");
			type = node.getAttribute("type");
			iconURL = node.getAttribute("icon");
			importance = node.getAttribute("importance");
		}
	}

	@Override
	public String toString() {
		if (!validPlace) {
			return "{NOTAPLACE}";
		} else {
			return this.displayName;
		}
	}

	public String toCompleteString() {
		if (!validPlace) {
			return "{NOTAPLACE}";
		} else {
			return "{" + this.placeID + "|" + this.displayName + "|" + this.osmType + "|" + this.osmID + "|" + this.placeRank + "|" + this.boundingBox + "|" + this.latitude + "|" + this.longitude + "|" + this.type + "|" + this.iconURL + "|" + this.importance + "|" + this.polygonPoints + "}";
		}
	}

	/**
	 * @return the validPlace
	 */
	public boolean isValidPlace() {
		return validPlace;
	}

	/**
	 * @return the placeID
	 */
	public String getPlaceID() {
		return placeID;
	}

	/**
	 * @return the osmType
	 */
	public String getOsmType() {
		return osmType;
	}

	/**
	 * @return the osmID
	 */
	public String getOsmID() {
		return osmID;
	}

	/**
	 * @return the placeRank
	 */
	public String getPlaceRank() {
		return placeRank;
	}

	/**
	 * @return the boundingBox
	 */
	public String getBoundingBox() {
		return boundingBox;
	}

	/**
	 * @return the polygonPoints
	 */
	public String getPolygonPoints() {
		return polygonPoints;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the importance
	 */
	public String getImportance() {
		return importance;
	}

	/**
	 * @return the iconURL
	 */
	public String getIconURL() {
		return iconURL;
	}
	
}
