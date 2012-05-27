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

import flood.monitor.overlay.CustomOverlay;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

public class Parser {

	public ArrayList<OverlayItem> Parse(String file, InputStream stream) {

		/*
		 * Drawable drawable =
		 * this.getResources().getDrawable(R.drawable.ic_launcher); overlay =
		 * new CustomOverlay(drawable, this); List<Overlay> mapOverlays =
		 * mapView.getOverlays(); GeoPoint point = new
		 * GeoPoint(46901130,96792070); OverlayItem overlayitem = new
		 * OverlayItem(point, "Hello", "I'm in Athens, Greece!");
		 */

		/*
		 * <?xml version="1.0" encoding="UTF-8"?> <kml
		 * xmlns="http://www.opengis.net/kml/2.2"> <Document> <Placemark>
		 * <name>CDATA example</name> <description> This is an example, please
		 * dont erase me </description> <Point>
		 * <coordinates>46.901130,96.792070</coordinates> </Point> </Placemark>
		 * <Placemark> <name>CDATA example</name> <description> This is an
		 * example, please dont erase me </description> <Point>
		 * <coordinates>46.901132,96.792060</coordinates> </Point> </Placemark>
		 * <Placemark> <name>CDATA example</name> <description> This is an
		 * example, please dont erase me </description> <Point>
		 * <coordinates>46.901171,96.792072</coordinates> </Point> </Placemark>
		 * <Placemark> <name>CDATA example</name> <description> This is an
		 * example, please dont erase me </description> <Point>
		 * <coordinates>46.901131,96.792098</coordinates> </Point> </Placemark>
		 * <Placemark> <name>CDATA example</name> <description> This is an
		 * example, please dont erase me </description> <Point>
		 * <coordinates>46.901133,96.792077</coordinates> </Point> </Placemark>
		 * </Document> </kml>
		 */
		KMLHandler handler = new KMLHandler();
		ArrayList<OverlayItem> itemList = new ArrayList<OverlayItem>(0);
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

	private class KMLHandler extends DefaultHandler {

		private ArrayList<OverlayItem> mOverlay;

		private boolean DOCUMENT = false;
		private boolean PLACEMARK = false;
		private boolean NAME = false;
		private boolean DESCRIPTION = false;
		private boolean POINT = false;
		private boolean COORDINATES = false;;

		private String name;
		private String description;
		private int latitude;
		private int longitude;
		private GeoPoint point;
		private OverlayItem overlayitem;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("DOCUMENT")) {
				mOverlay = new ArrayList<OverlayItem>(0);
				DOCUMENT = true;
			} else if (qName.equalsIgnoreCase("PLACEMARK")) {
				PLACEMARK = true;
			} else if (qName.equalsIgnoreCase("NAME")) {
				NAME = true;
			} else if (qName.equalsIgnoreCase("DESCRIPTION")) {
				DESCRIPTION = true;
			} else if (qName.equalsIgnoreCase("POINT")) {
				POINT = true;
			} else if (qName.equalsIgnoreCase("COORDINATES")) {
				COORDINATES = true;
			}
			Log.i(Parser.class.toString(), "Start Element :" + qName);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equalsIgnoreCase("DOCUMENT")) {
			} else if (qName.equalsIgnoreCase("PLACEMARK")) {
				overlayitem = new OverlayItem(point, name, description);
				mOverlay.add(overlayitem);
			} else if (qName.equalsIgnoreCase("NAME")) {
			} else if (qName.equalsIgnoreCase("DESCRIPTION")) {
			} else if (qName.equalsIgnoreCase("POINT")) {
				point = new GeoPoint(latitude, longitude);
			} else if (qName.equalsIgnoreCase("COORDINATES")) {
			}

			Log.i(Parser.class.toString(), "End Element :" + qName);
		}

		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			if (DOCUMENT) {
				Log.i(Parser.class.toString(), "Document :");
				DOCUMENT = false;
			}
			if (PLACEMARK) {
				Log.i(Parser.class.toString(), "Placemark :");
				PLACEMARK = false;
			}
			if (NAME) {
				name = new String(ch, start, length);
				NAME = false;
				Log.i(Parser.class.toString(), "Name :" + name);
			}
			if (DESCRIPTION) {
				description = new String(ch, start, length);
				DESCRIPTION = false;
				Log.i(Parser.class.toString(), "Description :" + description);
			}
			if (POINT) {
				POINT = false;
				Log.i(Parser.class.toString(), "Point :");
			}
			if (COORDINATES) {
				COORDINATES = false;
				String coordinates = new String(ch, start, length);
				try {
					latitude = (int) (Float.parseFloat(coordinates.substring(0,
							coordinates.indexOf(","))) * 1000000);
					longitude = (int) (Float.parseFloat(coordinates
							.substring(coordinates.indexOf(",") + 1)) * 1000000);
				} catch (Exception e) {
					String s = e.getMessage();
					s = s + "s";
					s = "";
				}
				Log.i(Parser.class.toString(), "Coordinates :" + coordinates);
			}
		}

		public ArrayList<OverlayItem> getResult() {
			return (ArrayList<OverlayItem>) (mOverlay.clone());
		}

	}
}