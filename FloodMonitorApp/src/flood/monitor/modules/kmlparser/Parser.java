package flood.monitor.modules.kmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import flood.monitor.overlay.Marker;

public class Parser {

	public ArrayList<Marker> Parse(String file, InputStream stream,
			Context context) {

		KMLHandler handler = new KMLHandler();
		ArrayList<Marker> itemList = new ArrayList<Marker>(0);
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);

		} catch (SAXException e) {
			String message = e.getMessage();
			Log.i(Parser.class.toString(), message);
		} catch (ParserConfigurationException e) {
			String message = e.getMessage();
			Log.i(Parser.class.toString(), message);
		} catch (IOException e) {
			String message = e.getMessage();
			Log.i(Parser.class.toString(), message);
		}
		itemList = handler.getResult();
		return itemList;
	}

	public ArrayList<Marker> ParseMarkers(String file, InputStream stream) {

		KMLHandler handler = new KMLHandler();

		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);

		} catch (SAXException e) {
			String message = e.getMessage();
			Log.i(Parser.class.toString(), message);
		} catch (ParserConfigurationException e) {
			String message = e.getMessage();
			Log.i(Parser.class.toString(), message);
		} catch (IOException e) {
			String message = e.getMessage();
			Log.i(Parser.class.toString(), message);
		}
		return handler.getResult();
	}

	public ArrayList<Event> ParseEvents(String file, InputStream stream) {

		EventHandler handler = new EventHandler();

		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);

		} catch (SAXException e) {
			String message = e.getMessage();
			Log.i(Parser.class.toString(), message);
		} catch (ParserConfigurationException e) {
			String message = e.getMessage();
			Log.i(Parser.class.toString(), message);
		} catch (IOException e) {
			String message = e.getMessage();
			Log.i(Parser.class.toString(), message);
		}
		return handler.getResult();
	}

	public ArrayList<Region> ParseGeoRegions(String filename, InputStream stream) {
		ArrayList<Region> regions = new ArrayList<Region>();
		return  regions;
	}
	
	private class EventHandler extends DefaultHandler {

		private static final String EVENTS = "events";
		private static final String EVENT = "event";

		private static final String ID = "id";
		private static final String NAME = "name";
		private static final String FILE = "kmlfile";
		private static final String ACTIVE = "active";
		private static final String BEGINDATE = "begindate";
		private static final String ENDDATE = "enddate";
		private static final String BOUNDARIES = "boundaries";
		private static final String BOUNDARY = "boundary";
		private static final String NORTHWEST = "northwest";
		private static final String SOUTHEAST = "southeast";

		private GeoPoint northWest;
		private GeoPoint southEast;
		private int boundary_id;
		private String boundary_name;

		private ArrayList<Event> events;
		private ArrayList<Region> regions;
		private boolean boundaries;
		private String temp;

		private String originURL;
		private String beginDate;
		private String endDate;
		private String name;
		private int regionId;
		private boolean active;

		public EventHandler() {
			super();
			this.temp = "";
			boundaries = false;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(EVENTS)) {
				this.events = new ArrayList<Event>(0);
			} else if (qName.equalsIgnoreCase(EVENT)) {

			} else if (qName.equalsIgnoreCase(ID)) {

			} else if (qName.equalsIgnoreCase(NAME)) {

			} else if (qName.equalsIgnoreCase(FILE)) {

			} else if (qName.equalsIgnoreCase(ACTIVE)) {

			} else if (qName.equalsIgnoreCase(BEGINDATE)) {

			} else if (qName.equalsIgnoreCase(ENDDATE)) {

			} else if (qName.equalsIgnoreCase(BOUNDARIES)) {
				this.regions = new ArrayList<Region>(0);
				boundaries = true;
			} else if (qName.equalsIgnoreCase(BOUNDARY)) {

			} else if (qName.equalsIgnoreCase(NORTHWEST)) {

			} else if (qName.equalsIgnoreCase(SOUTHEAST)) {

			}
			temp = "";
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(EVENTS)) {

			} else if (qName.equalsIgnoreCase(EVENT)) {
				Event event = new Event(regionId, name, active, beginDate,
						endDate, regions);
				this.events.add(event);
			} else if (qName.equalsIgnoreCase(ID)) {
				if (!boundaries)
					this.regionId = Integer.parseInt(temp);
				else
					this.boundary_id = Integer.parseInt(temp);
			} else if (qName.equalsIgnoreCase(NAME)) {
				if (!boundaries)
					this.name = temp;
				else
					this.boundary_name = temp;
			} else if (qName.equalsIgnoreCase(FILE)) {
				this.originURL = temp;
			} else if (qName.equalsIgnoreCase(ACTIVE)) {
				this.active = Boolean.parseBoolean(temp);
			} else if (qName.equalsIgnoreCase(BEGINDATE)) {
				this.beginDate = temp;
			} else if (qName.equalsIgnoreCase(ENDDATE)) {
				this.endDate = temp;
			} else if (qName.equalsIgnoreCase(BOUNDARIES)) {
				boundaries = false;
			} else if (qName.equalsIgnoreCase(BOUNDARY)) {
				Region region = new Region(boundary_id, boundary_name,
						northWest, southEast);
				this.regions.add(region);
			} else if (qName.equalsIgnoreCase(NORTHWEST)) {
				int lon = (int) (Double.parseDouble(temp.substring(0, temp.indexOf(",")))  * 1000000);
				int lat = (int) (Double.parseDouble(temp.substring(temp.indexOf(",") + 1)) * 1000000);
				this.northWest = new GeoPoint(lat, lon);
			} else if (qName.equalsIgnoreCase(SOUTHEAST)) {
				int lon = (int) (Double.parseDouble(temp.substring(0, temp.indexOf(",")))  * 1000000);
				int lat = (int) (Double.parseDouble(temp.substring(temp.indexOf(",") + 1)) * 1000000);
				this.southEast = new GeoPoint(lat, lon);
			}
			temp = "";
		}

		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String input = new String(ch, start, length);
			temp = temp + input.replaceAll("\n", "").replaceAll("\t", "");
		}

		public ArrayList<Event> getResult() {
			return events;
		}
	}

	private class RegionHandler extends DefaultHandler {

		private static final String BOUNDARIES = "boundaries";
		private static final String BOUNDARY_NAME = "name";
		private static final String BOUNDARY_ID = "id";
		private static final String EVENTS = "events";
		private static final String EVENT = "event";

		private static final String ID = "id";
		private static final String NAME = "name";
		private static final String FILE = "kmlfile";
		private static final String ACTIVE = "active";
		private static final String BEGINDATE = "begindate";
		private static final String ENDDATE = "enddate";
		private static final String BOUNDARY = "boundary";
		private static final String NORTHWEST = "northwest";
		private static final String SOUTHEAST = "northeast";

		private ArrayList<Region> regions;
		private String northwest;
		private String southeast;
		private String temp;

		private String originURL;
		private String beginDate;
		private String endDate;
		private String name;
		private int regionId;
		private int latitude;
		private int longitud;
		private boolean active;

		public RegionHandler() {
			super();
			this.temp = "";
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(EVENTS)) {
				this.regions = new ArrayList<Region>(0);
			} else if (qName.equalsIgnoreCase(EVENT)) {

			} else if (qName.equalsIgnoreCase(ID)) {

			} else if (qName.equalsIgnoreCase(NAME)) {

			} else if (qName.equalsIgnoreCase(FILE)) {

			} else if (qName.equalsIgnoreCase(ACTIVE)) {

			} else if (qName.equalsIgnoreCase(BEGINDATE)) {

			} else if (qName.equalsIgnoreCase(ENDDATE)) {

			} else if (qName.equalsIgnoreCase(BOUNDARIES)) {

			} else if (qName.equalsIgnoreCase(BOUNDARY_ID)) {

			} else if (qName.equalsIgnoreCase(BOUNDARY_NAME)) {

			} else if (qName.equalsIgnoreCase(BOUNDARY)) {

			} else if (qName.equalsIgnoreCase(NORTHWEST)) {

			} else if (qName.equalsIgnoreCase(SOUTHEAST)) {

			}

			temp = "";
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(EVENTS)) {

			} else if (qName.equalsIgnoreCase(EVENT)) {
				Region region = new Region(regionId, name, originURL, latitude,
						longitud);
				this.regions.add(region);
			} else if (qName.equalsIgnoreCase(ID)) {
				this.regionId = Integer.parseInt(temp);
			} else if (qName.equalsIgnoreCase(NAME)) {
				this.name = temp;
			} else if (qName.equalsIgnoreCase(FILE)) {
				this.originURL = temp;
			} else if (qName.equalsIgnoreCase(ACTIVE)) {
				this.active = Boolean.parseBoolean(temp);
			} else if (qName.equalsIgnoreCase(BEGINDATE)) {
				this.beginDate = temp;
			} else if (qName.equalsIgnoreCase(ENDDATE)) {
				this.endDate = temp;
			} else if (qName.equalsIgnoreCase(BOUNDARIES)) {

			} else if (qName.equalsIgnoreCase(BOUNDARY_ID)) {

			} else if (qName.equalsIgnoreCase(BOUNDARY_NAME)) {

			} else if (qName.equalsIgnoreCase(BOUNDARY)) {

			} else if (qName.equalsIgnoreCase(NORTHWEST)) {
				this.northwest = temp;
			} else if (qName.equalsIgnoreCase(SOUTHEAST)) {
				this.southeast = temp;
			}

			temp = "";
		}

		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String input = new String(ch, start, length);
			temp = temp + input.replaceAll("\n", "").replaceAll("\t", "");
		}

		public ArrayList<Region> getResult() {
			return regions;
		}
	}

	private class KMLHandler extends DefaultHandler {

		private ArrayList<Marker> mOverlay;

		private String observationtime;
		private String usercomment;
		private String image;
		private int covertype;
		private int coverHeight;

		private int latitude;
		private int longitude;

		private int severity;
		private GeoPoint point;
		private Marker overlayitem;

		private static final String DOCUMENT = "Document";
		private static final String PLACEMARK = "Placemark";
		private static final String SEVERITY = "styleUrl";
		private static final String POINT = "Point";
		private static final String COORDINATES = "coordinates";
		private static final String OBSERVATIONTIME = "mark:ObservationTime";
		private static final String USERCOMMENT = "mark:UserComment";
		private static final String IMAGE = "mark:ImageUrl";
		private static final String COVERTYPE = "mark:CoverType";
		private static final String COVERHEIGHT = "mark:CoverHeight";

		private String temp = "";

		public KMLHandler() {
			super();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(DOCUMENT)) {
				mOverlay = new ArrayList<Marker>(0);

			} else if (qName.equalsIgnoreCase(PLACEMARK)) {

			} else if (qName.equalsIgnoreCase(SEVERITY)) {

			} else if (qName.equalsIgnoreCase(POINT)) {

			} else if (qName.equalsIgnoreCase(COORDINATES)) {

			} else if (qName.equalsIgnoreCase(OBSERVATIONTIME)) {

			} else if (qName.equalsIgnoreCase(USERCOMMENT)) {

			} else if (qName.equalsIgnoreCase(IMAGE)) {

			} else if (qName.equalsIgnoreCase(COVERTYPE)) {

			} else if (qName.equalsIgnoreCase(COVERHEIGHT)) {

			}

			temp = "";
			// Log.i(Parser.class.toString(), "Start Element :" + qName);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(DOCUMENT)) {

			} else if (qName.equalsIgnoreCase(PLACEMARK)) {
				overlayitem = new Marker(point, observationtime, usercomment,
						image, severity, covertype, coverHeight);

				overlayitem.setMarker(null);
				severity = 0;
				mOverlay.add(overlayitem);

			} else if (qName.equalsIgnoreCase(SEVERITY)) {
				String coordinates = temp;
				severity = Integer.parseInt(Character.toString(coordinates
						.charAt(coordinates.length() - 1)));
			} else if (qName.equalsIgnoreCase(POINT)) {
				point = new GeoPoint(latitude, longitude);
			} else if (qName.equalsIgnoreCase(COORDINATES)) {
				String coordinates = temp;
				longitude = (int) (Float.parseFloat(coordinates.substring(0,
						coordinates.indexOf(","))) * 1000000);
				latitude = (int) (Float.parseFloat(coordinates
						.substring(coordinates.indexOf(",") + 1)) * 1000000);
			} else if (qName.equalsIgnoreCase(OBSERVATIONTIME)) {
				observationtime = temp;
			} else if (qName.equalsIgnoreCase(USERCOMMENT)) {
				usercomment = temp;
			} else if (qName.equalsIgnoreCase(IMAGE)) {
				image = temp;
			} else if (qName.equalsIgnoreCase(COVERTYPE)) {
				covertype = (temp.charAt(0));
			} else if (qName.equalsIgnoreCase(COVERHEIGHT)) {
				coverHeight = Integer.parseInt(temp);
			}

			temp = "";
		}

		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String input = new String(ch, start, length);
			temp = temp + input.replaceAll("\n", "").replaceAll("\t", "");
		}

		public ArrayList<Marker> getResult() {
			return mOverlay;
		}
	}
}