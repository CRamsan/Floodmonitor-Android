package flood.monitor.modules.kmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import flood.monitor.R;
import flood.monitor.overlay.CustomOverlay;
import flood.monitor.overlay.CustomOverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

public class Parser {

	public ArrayList<CustomOverlayItem> Parse(String file, InputStream stream,
			Context context) {

		KMLHandler handler = new KMLHandler(context);
		ArrayList<CustomOverlayItem> itemList = new ArrayList<CustomOverlayItem>(
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

		private ArrayList<CustomOverlayItem> mOverlay;

		private String observationtime;
		private String usercomment;
		private String image;
		private char covertype;
		private int coverHeight;

		private int latitude;
		private int longitude;

		private int severity;
		private GeoPoint point;
		private CustomOverlayItem overlayitem;

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
				mOverlay = new ArrayList<CustomOverlayItem>(0);

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
				overlayitem = new CustomOverlayItem(point, observationtime,
						usercomment);
				Drawable icon = null;
				switch (severity) {
				case 4:
					icon = context.getResources().getDrawable(
							R.drawable.marker_green_large);
					break;
				case 5:
					icon = context.getResources().getDrawable(
							R.drawable.marker_green_yellow_large);
					break;
				case 6:
					icon = context.getResources().getDrawable(
							R.drawable.marker_yellow_large);
					break;
				case 7:
					icon = context.getResources().getDrawable(
							R.drawable.marker_orange_large);
					break;
				case 8:
					icon = context.getResources().getDrawable(
							R.drawable.marker_red_large);
					break;
				default:
					break;
				}
				icon.setBounds(0, 0, icon.getIntrinsicWidth(),
						icon.getIntrinsicHeight());
				overlayitem.setMarker(icon);
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

		public ArrayList<CustomOverlayItem> getResult() {
			return (ArrayList<CustomOverlayItem>) (mOverlay.clone());
		}
	}

	public class Region {
		private String name;
		private int regionId;

		public Region(String name, int id) {
			this.setName(name);
			this.setRegionId(id);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getRegionId() {
			return regionId;
		}

		public void setRegionId(int regionId) {
			this.regionId = regionId;
		}

	}
}