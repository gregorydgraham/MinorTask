/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.place;

import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import nz.co.gregs.minortask.components.HasDefaultButton;
import nz.co.gregs.minortask.components.SecureDiv;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author gregorygraham
 */
public class PlaceSearchComponent extends SecureDiv implements HasDefaultButton {

	private Long taskID;
	TextField locationText = new TextField("", "", "address to search for...");
	Button searchButton = new Button("Search For Location...");
	Button addButton = new Button("Add");
	PlacesBox comboBox = new PlacesBox();
	private Registration defaultRegistration;

	public PlaceSearchComponent() {
		super();
		addClassName("place-search-component");

		add(locationText);
		add(searchButton);
		add(comboBox);
		add(addButton);

		searchButton.addClickListener((event) -> {
			searchForLocation(locationText.getValue());
			comboBox.setOpened(true);
			comboBox.focus();
		});
		addButton.addClickListener((event) -> {
			addSelectedLocation();
		});
		locationText.addValueChangeListener((event) -> {
			searchForLocation(event.getSource().getValue());
		});
		locationText.addFocusListener((event) -> {
			if (locationText.isEnabled() && !locationText.isReadOnly()) {
				defaultRegistration = setAsDefaultButton(searchButton, (keyEvent) -> {
					searchForLocation(locationText.getValue());
					comboBox.setOpened(true);
					comboBox.focus();
				});
			}
		});
		Registration addBlurListener = locationText.addBlurListener((BlurNotifier.BlurEvent<TextField> event) -> {
			if (defaultRegistration != null) {
				removeAsDefaultButton(searchButton, defaultRegistration);
			}
		});
		comboBox.addValueChangeListener((event) -> {
			defaultRegistration = setAsDefaultButton(addButton, (keyEvent) -> {
				addSelectedLocation();
			});
		});
		comboBox.addBlurListener((event) -> {
			if (defaultRegistration != null) {
				removeAsDefaultButton(addButton, defaultRegistration);
			}
		});
	}

	public void setTaskID(Long taskID) {
		this.taskID = taskID;
	}

	private void addSelectedLocation() {
		OpenStreetMapPlace place = comboBox.getValue();
		Place location = new Place(this.taskID, place);
		try {
			getDatabase().insert(location);
		} catch (SQLException ex) {
			sqlerror(ex);
		}

// Ultimately we need to inform our listeners that there is a new location
		fireEvent(new PlaceAddedEvent(this, true));
	}

	private void searchForLocation(String searchString) {
		try {
			comboBox.setToEmptyList();
//			String searchString = event.getSource().getValue();
			StringBuilder url = new StringBuilder("https://nominatim.openstreetmap.org/search?q=");
			String charset = "UTF-8";
			String encodedSearchString = URLEncoder.encode(searchString, charset);
			url.append(encodedSearchString);
			url.append("&format=xml&polygon=1&addressdetails=1");
			String urlString = url.toString();
			System.out.println("URL: " + urlString);
			URLConnection urlConnection = new URL(urlString).openConnection();
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("accept-charset", charset);
			urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");

			try (InputStream is = urlConnection.getInputStream()) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document xmlDoc = builder.parse(is);

				System.out.println(xmlDoc);
				/*<?xml version="1.0" encoding="UTF-8" ?>
				<searchresults timestamp='Mon, 03 Sep 18 22:57:41 +0000' attribution='Data © OpenStreetMap contributors, ODbL 1.0. http://www.openstreetmap.org/copyright' querystring='8 willis str' polygon='true' exclude_place_ids='120784948' more_url='https://nominatim.openstreetmap.org/search.php?q=8+willis+str&amp;addressdetails=1&amp;polygon=1&amp;exclude_place_ids=120784948&amp;format=xml'>
				<place place_id='120784948' osm_type='way' osm_id='194473278' place_rank='30' boundingbox="53.9533056,53.953411,-1.0712879,-1.0710499" polygonpoints='[["-1.0712879","53.953392"],["-1.0711778","53.9533598"],["-1.0711946","53.9533386"],["-1.0710818","53.9533056"],["-1.0710499","53.9533458"],["-1.0712728","53.953411"],["-1.0712879","53.953392"]]' lat='53.9533528' lon='-1.07112858907571' display_name='8, Willis Street, The Groves, Fishergate, York, Yorkshire and the Humber, England, YO10 5BE, UK' class='building' type='yes' importance='0.311'>
				<house_number>8</house_number><road>Willis Street</road><neighbourhood>The Groves</neighbourhood><suburb>Fishergate</suburb><city>York</city><county>York</county><state_district>Yorkshire and the Humber</state_district><state>England</state><postcode>YO10 5BE</postcode><country>UK</country><country_code>gb</country_code></place></searchresults>
				 */
//				xmlDoc.normalizeDocument();
				System.out.println("XMLDOC: " + xmlDoc.toString());
				NodeList root = xmlDoc.getElementsByTagName("searchresults");
				System.out.println("ROOT: " + root);
				if (root != null) {
					NodeList placeNodes = xmlDoc.getElementsByTagName("place");
					System.out.println("PLACENODES: " + placeNodes);
					if (placeNodes != null) {
						System.out.println("PLACENODES: " + placeNodes.getLength());
						for (int i = 0; i < placeNodes.getLength(); i++) {
							OpenStreetMapPlace place = new OpenStreetMapPlace((Element) placeNodes.item(i));
							System.out.println(place.toCompleteString());
							this.comboBox.addPlace(place);
						}
					}
				}
			}
		} catch (UnsupportedEncodingException | MalformedURLException ex) {
			Logger.getLogger(PlaceSearchComponent.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException | ParserConfigurationException | SAXException ex) {
			Logger.getLogger(PlaceSearchComponent.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	public Registration addLocationAddedListener(
			ComponentEventListener<PlaceAddedEvent> listener) {
		return addListener(PlaceAddedEvent.class, listener);
	}

	public void setReadOnly(boolean b) {
		locationText.setReadOnly(b);
		comboBox.setReadOnly(b);

		addButton.setEnabled(!b);
		searchButton.setEnabled(!b);
	}

	private static class PlacesBox extends ComboBox<OpenStreetMapPlace> {

		List<OpenStreetMapPlace> placesList = new ArrayList<>();

		public PlacesBox() {
			super();
		}

		private void refreshList() {
			super.setItems(placesList);
		}

		@Override
		public void setItems(Collection<OpenStreetMapPlace> items) {
			placesList.addAll(items);
			refreshList();
		}

		public void addPlace(OpenStreetMapPlace place) {
			placesList.add(place);
			refreshList();
		}

		public void addPlaces(Collection<OpenStreetMapPlace> places) {
			this.setItems(places);
		}

		private void setToEmptyList() {
			this.placesList.clear();
			refreshList();
		}
	}
}
