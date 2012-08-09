package flood.monitor.modules.kmlparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.android.maps.GeoPoint;

public class Parser {

	public static ArrayList<Region> ParseRegions(String filename) {
		RegionHandler handler = new RegionHandler();
		InputStream stream;
		try {
			stream = new FileInputStream(filename);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}
		return handler.getResult();
	}

	public static ArrayList<Event> ParseEvents(String filename) {

		EventHandler handler = new EventHandler();

		InputStream stream;
		try {
			stream = new FileInputStream(filename);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}
		return handler.getResult();
	}

	public static int ParseFileVersion(String filename) {
		PropertyHandler handler = new PropertyHandler();

		InputStream stream;
		try {
			stream = new FileInputStream(filename);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}
		return handler.getIntResult();
	}

	public static int ParseFileVersion(InputStream stream) {
		PropertyHandler handler = new PropertyHandler();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}
		return handler.getIntResult();
	}

	public static String ParseFileNames(String filename) {
		PropertyHandler handler = new PropertyHandler();

		InputStream stream;
		try {
			stream = new FileInputStream(filename);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}

		return handler.getStringResult();
	}

	public static String ParseFileNames(InputStream stream) {
		PropertyHandler handler = new PropertyHandler();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}

		return handler.getStringResult();
	}

	public static ArrayList<Marker> ParseMarkers(String filename) {

		MarkerHandler handler = new MarkerHandler();

		InputStream stream;
		try {
			stream = new FileInputStream(filename);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}

		return handler.getResult();
	}

	private static class RegionHandler extends DefaultHandler {

		private static final String BOUNDARIES = "boundaries";
		private static final String LIST = "regions";
		private static final String ITEM = "region";

		private static final String ID = "id";
		private static final String NAME = "name";
		private static final String BOUNDARY = "boundary";
		private static final String NORTHWEST = "northwest";
		private static final String SOUTHEAST = "southeast";

		private ArrayList<Region> regions;
		private ArrayList<Boundary> boundaries;
		private String temp;

		private String name;
		private int regionId;
		private int south;
		private int east;
		private int north;
		private int west;

		public RegionHandler() {
			super();
			this.temp = "";
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(LIST)) {
				this.regions = new ArrayList<Region>(0);
			} else if (qName.equalsIgnoreCase(ITEM)) {

			} else if (qName.equalsIgnoreCase(ID)) {

			} else if (qName.equalsIgnoreCase(NAME)) {

			} else if (qName.equalsIgnoreCase(BOUNDARIES)) {
				this.boundaries = new ArrayList<Boundary>(0);
			} else if (qName.equalsIgnoreCase(BOUNDARY)) {

			} else if (qName.equalsIgnoreCase(NORTHWEST)) {

			} else if (qName.equalsIgnoreCase(SOUTHEAST)) {

			}
			temp = "";
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(LIST)) {

			} else if (qName.equalsIgnoreCase(ITEM)) {
				Region region = new Region(regionId, name, boundaries);
				this.boundaries = null;
				this.regions.add(region);
			} else if (qName.equalsIgnoreCase(ID)) {
				this.regionId = Integer.parseInt(temp);
			} else if (qName.equalsIgnoreCase(NAME)) {
				this.name = temp;
			} else if (qName.equalsIgnoreCase(BOUNDARIES)) {

			} else if (qName.equalsIgnoreCase(BOUNDARY)) {
				Boundary boundary = new Boundary();
				boundary.east = east;
				boundary.west = west;
				boundary.south = south;
				boundary.north = north;
				boundaries.add(boundary);
			} else if (qName.equalsIgnoreCase(NORTHWEST)) {
				west = (int) (Double.parseDouble(temp.substring(0,
						temp.indexOf(","))) * 1000000);
				north = (int) (Double.parseDouble(temp.substring(temp
						.indexOf(",") + 1)) * 1000000);
			} else if (qName.equalsIgnoreCase(SOUTHEAST)) {
				east = (int) (Double.parseDouble(temp.substring(0,
						temp.indexOf(","))) * 1000000);
				south = (int) (Double.parseDouble(temp.substring(temp
						.indexOf(",") + 1)) * 1000000);
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

	private static class EventHandler extends DefaultHandler {

		private static final String EVENTS = "events";
		private static final String EVENT = "event";

		private static final String ID = "id";
		private static final String NAME = "name";
		private static final String ACTIVE = "active";
		private static final String BEGINDATE = "begindate";
		private static final String ENDDATE = "enddate";
		private static final String BOUNDARIES = "boundaries";
		private static final String BOUNDARY = "boundary";

		private ArrayList<Event> events;
		private String temp;

		private String beginDate;
		private String endDate;
		private String name;
		private int regionId;
		private boolean active;
		private boolean skip;

		public EventHandler() {
			super();
			this.temp = "";
			this.skip = false;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(EVENTS)) {
				this.events = new ArrayList<Event>(0);
			} else if (qName.equalsIgnoreCase(EVENT)) {

			} else if (qName.equalsIgnoreCase(ID)) {

			} else if (qName.equalsIgnoreCase(NAME)) {

			} else if (qName.equalsIgnoreCase(ACTIVE)) {

			} else if (qName.equalsIgnoreCase(BEGINDATE)) {

			} else if (qName.equalsIgnoreCase(ENDDATE)) {

			} else if (qName.equalsIgnoreCase(BOUNDARIES)) {
				skip = true;
			} else if (qName.equalsIgnoreCase(BOUNDARY)) {

			}
			temp = "";
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(BOUNDARIES)) {
				skip = false;
			} else if (skip)
				return;

			if (qName.equalsIgnoreCase(EVENTS)) {

			} else if (qName.equalsIgnoreCase(EVENT)) {
				Event event = new Event(regionId, name, active, beginDate,
						endDate);
				this.events.add(event);
			} else if (qName.equalsIgnoreCase(ID)) {
				this.regionId = Integer.parseInt(temp);
			} else if (qName.equalsIgnoreCase(NAME)) {
				this.name = temp;
			} else if (qName.equalsIgnoreCase(ACTIVE)) {
				this.active = Boolean.parseBoolean(temp);
			} else if (qName.equalsIgnoreCase(BEGINDATE)) {
				this.beginDate = temp;
			} else if (qName.equalsIgnoreCase(ENDDATE)) {
				this.endDate = temp;
			} else if (qName.equalsIgnoreCase(BOUNDARIES)) {
				skip = false;
			} else if (qName.equalsIgnoreCase(BOUNDARY)) {
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

	private static class PropertyHandler extends DefaultHandler {

		private int version;
		private String file;

		private static final String VERSION = "version";
		private static final String KMLFILE = "kmlfile";

		private String temp = "";

		public PropertyHandler() {
			super();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(VERSION)) {
				version = 0;
			} else if (qName.equalsIgnoreCase(KMLFILE)) {
				file = "";
			}

			temp = "";
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(VERSION)) {
				version = Integer.parseInt(temp);
			} else if (qName.equalsIgnoreCase(KMLFILE)) {
				file = temp;
			}
			temp = "";
		}

		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String input = new String(ch, start, length);
			temp = temp + input.replaceAll("\n", "").replaceAll("\t", "");
		}

		public int getIntResult() {
			return version;
		}

		public String getStringResult() {
			return file;
		}
	}

	private static class MarkerHandler extends DefaultHandler {

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

		public MarkerHandler() {
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