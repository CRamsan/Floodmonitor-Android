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

		KMLHandler handler = new KMLHandler(context);
		ArrayList<Marker> itemList = new ArrayList<Marker>(
				0);
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

	public ArrayList<Region> ParseRegions(String file, InputStream stream,
			Context context) {

		XMLHandler handler = new XMLHandler(context);
		String[] itemList;
		
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

	private class XMLHandler extends DefaultHandler {

		private static final String DOCUMENT = "Document";
		private static final String PLACEMARK = "Placemark";
		private static final String DATE = "date";
		private static final String COORDINATES = "coordinates";
		private static final String NAME = "name";

		private ArrayList<Region> options;
		private Context context;
		private String name;
		private int id;
		private String temp = "";

		public XMLHandler(Context context) {
			super();
			this.context = context;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(DOCUMENT)) {
				options = new ArrayList<Region>(0);

			} else if (qName.equalsIgnoreCase(PLACEMARK)) {

			} else if (qName.equalsIgnoreCase(NAME)) {

			}

			temp = "";
			// Log.i(Parser.class.toString(), "Start Element :" + qName);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(DOCUMENT)) {

			} else if (qName.equalsIgnoreCase(PLACEMARK)) {
				Region reg = new Region(name, id);
				options.add(reg);
			} else if (qName.equalsIgnoreCase(NAME)) {
				name = temp;
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
			return options;
		}
	}

	private class KMLHandler extends DefaultHandler {

		private ArrayList<Marker> mOverlay;

		private String observationtime;
		private String usercomment;
		private String image;
		private char covertype;
		private int coverHeight;

		private int latitude;
		private int longitude;

		private int severity;
		private GeoPoint point;
		private Marker overlayitem;

		private Context context;

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

		public KMLHandler(Context context) {
			super();
			this.context = context;
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
				overlayitem = new Marker(point, observationtime,
						usercomment);
				
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
			return (ArrayList<Marker>) (mOverlay.clone());
		}
	}
}