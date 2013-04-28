package flood.monitor.modules.kmlparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.google.android.maps.GeoPoint;

/**
 * This class will handle xml file or streams and it will retrieve the
 * information and convert it into usable objects.
 * 
 * @author Cesar
 * 
 */
public class Parser {

	/**
	 * @param filename
	 *            path to file to read.
	 * @return a list of regions objects obtained from the content.
	 */
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

	/**
	 * @param stream
	 *            input stream from the content to read.
	 * @return a list of regions objects obtained from the content.
	 */
	public static ArrayList<Region> ParseRegions(InputStream stream) {
		RegionHandler handler = new RegionHandler();
		try {
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

	/**
	 * @param filename
	 *            path to file to read.
	 * @return a list of events objects obtained from the content.
	 */
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

	/**
	 * @param stream
	 *            input stream from the content to read.
	 * @return a list of events objects obtained from the content.
	 */
	public static ArrayList<Event> ParseEvents(InputStream stream) {

		EventHandler handler = new EventHandler();

		try {
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

	public static ArrayList<KMLFile> ParseKMLFiles(String filename) {
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

		return handler.getKMLFiles();
	}

	public static ArrayList<KMLFile> ParseKMLFiles(InputStream stream) {
		PropertyHandler handler = new PropertyHandler();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, handler);
		} catch (SAXException e) {
			Log.d("Parser", e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.d("Parser", e.getMessage());
		} catch (FileNotFoundException e) {
			Log.d("Parser", e.getMessage());
		} catch (IOException e) {
			Log.d("Parser", e.getMessage());
		}

		return handler.getKMLFiles();
	}

	/**
	 * @param filename
	 *            path to file to read.
	 * @return a list of markers objects obtained from the content.
	 */
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

	/**
	 * @param stream
	 *            input stream from the content to read.
	 * @return a list of markers objects obtained from the content.
	 */
	public static ArrayList<Marker> ParseMarkers(InputStream stream) {

		MarkerHandler handler = new MarkerHandler();

		try {
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

	/**
	 * @author Cesar
	 * 
	 */
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
		private boolean inBoundary = false;
		private String name;
		private int boundaryId;
		private String boundaryName;
		private int regionId;
		private int south;
		private int east;
		private int north;
		private int west;

		/**
		 * Constructor that will initialize some required inner components.
		 */
		public RegionHandler() {
			super();
			this.temp = "";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
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
				inBoundary = true;
			} else if (qName.equalsIgnoreCase(BOUNDARY)) {

			} else if (qName.equalsIgnoreCase(NORTHWEST)) {

			} else if (qName.equalsIgnoreCase(SOUTHEAST)) {

			}
			temp = "";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(LIST)) {

			} else if (qName.equalsIgnoreCase(ITEM)) {
				Region region = new Region(regionId, name, boundaries);
				this.boundaries = null;
				this.regions.add(region);
			} else if (qName.equalsIgnoreCase(ID)) {
				if (inBoundary) {
					this.boundaryId = Integer.parseInt(temp);
				} else {
					this.regionId = Integer.parseInt(temp);
				}
			} else if (qName.equalsIgnoreCase(NAME)) {
				if (inBoundary) {
					this.boundaryName = temp;
				} else {
					this.name = temp;
				}
			} else if (qName.equalsIgnoreCase(BOUNDARIES)) {
				inBoundary = false;
			} else if (qName.equalsIgnoreCase(BOUNDARY)) {
				Boundary boundary = new Boundary();
				boundary.setEast(east);
				boundary.setWest(west);
				boundary.setSouth(south);
				boundary.setNorth(north);
				boundary.setRegionId(regionId);
				boundary.setName(boundaryName);
				boundary.setId(boundaryId);
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String input = new String(ch, start, length);
			temp = temp + input.replaceAll("\n", "").replaceAll("\t", "");
		}

		/**
		 * @return an array list of the found regions.
		 */
		public ArrayList<Region> getResult() {
			return regions;
		}
	}

	/**
	 * @author Cesar
	 * 
	 */
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

		/**
		 * Default constrcutor.
		 */
		public EventHandler() {
			super();
			this.temp = "";
			this.skip = false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String input = new String(ch, start, length);
			temp = temp + input.replaceAll("\n", "").replaceAll("\t", "");
		}

		/**
		 * @return a list with the found events.
		 */
		public ArrayList<Event> getResult() {
			return events;
		}
	}

	/**
	 * @author Cesar
	 * 
	 */
	private static class PropertyHandler extends DefaultHandler {

		private ArrayList<KMLFile> kmlfiles;

		private static final String KMLFILES = "kmlfiles";
		private static final String BASE = "base";
		private static final String DIFF = "diff";
		private static final String KMLFILE = "kmlfile";
		private static final String ID = "id";
		private static final String FILE = "file";
		private static final String VERSION = "version";

		private int fileId = -1;
		private int fileVersion = -1;
		private int regionId = -1;
		private int boundaryId = -1;
		private int eventId = -1;
		private boolean isBase = false;
		private String fileURL;

		private String temp = "";

		/**
		 * Defaulty constructor.
		 */
		public PropertyHandler() {
			super();
			this.kmlfiles = new ArrayList<KMLFile>(0);
		}

		public ArrayList<KMLFile> getKMLFiles() {
			return kmlfiles;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(KMLFILES)) {

			} else if (qName.equalsIgnoreCase(BASE)) {

			} else if (qName.equalsIgnoreCase(DIFF)) {

			} else if (qName.equalsIgnoreCase(KMLFILE)) {

			} else if (qName.equalsIgnoreCase(ID)) {

			} else if (qName.equalsIgnoreCase(FILE)) {

			} else if (qName.equalsIgnoreCase(VERSION)) {

			}

			temp = "";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(KMLFILES)) {

			} else if (qName.equalsIgnoreCase(BASE)) {
				isBase = true;
			} else if (qName.equalsIgnoreCase(DIFF)) {
				isBase = false;
			} else if (qName.equalsIgnoreCase(KMLFILE)) {
				kmlfiles.add(new KMLFile(fileId, fileVersion, fileURL, isBase));
			} else if (qName.equalsIgnoreCase(ID)) {
				fileId = Integer.parseInt(temp);
			} else if (qName.equalsIgnoreCase(FILE)) {
				fileURL = temp;
			} else if (qName.equalsIgnoreCase(VERSION)) {
				fileVersion = Integer.parseInt(temp);
			}
			temp = "";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String input = new String(ch, start, length);
			temp = temp + input.replaceAll("\n", "").replaceAll("\t", "");
		}
	}

	/**
	 * @author Cesar
	 * 
	 */
	private static class MarkerHandler extends DefaultHandler {

		private ArrayList<Marker> mOverlay;

		private String observationtime;
		private String usercomment;
		private String image;
		private int latitude;
		private int longitude;
		private int id;

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

		private String temp = "";

		/**
		 * Default constructor.
		 */
		public MarkerHandler() {
			super();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(DOCUMENT)) {
				mOverlay = new ArrayList<Marker>(0);

			} else if (qName.equalsIgnoreCase(PLACEMARK)) {
				id = Integer.parseInt(attributes.getValue("id"));
			} else if (qName.equalsIgnoreCase(SEVERITY)) {

			} else if (qName.equalsIgnoreCase(POINT)) {

			} else if (qName.equalsIgnoreCase(COORDINATES)) {

			} else if (qName.equalsIgnoreCase(OBSERVATIONTIME)) {

			} else if (qName.equalsIgnoreCase(USERCOMMENT)) {

			} else if (qName.equalsIgnoreCase(IMAGE)) {

			}
			temp = "";
			// Log.i(Parser.class.toString(), "Start Element :" + qName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(DOCUMENT)) {

			} else if (qName.equalsIgnoreCase(PLACEMARK)) {
				overlayitem = new Marker(id, point, observationtime,
						usercomment, image, severity);

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
			}
			temp = "";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String input = new String(ch, start, length);
			temp = temp + input.replaceAll("\n", "").replaceAll("\t", "");
		}

		/**
		 * @return a list of the found markers.
		 */
		public ArrayList<Marker> getResult() {
			return mOverlay;
		}
	}
}