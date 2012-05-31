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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

public class Parser {

	private Context context;

	public ArrayList<OverlayItem> Parse(String file, InputStream stream,
			Context context) {

		this.context = context;
		/*
		 * Drawable drawable =
		 * this.getResources().getDrawable(R.drawable.ic_launcher); overlay =
		 * new CustomOverlay(drawable, this); List<Overlay> mapOverlays =
		 * mapView.getOverlays(); GeoPoint point = new
		 * GeoPoint(46901130,96792070); OverlayItem overlayitem = new
		 * OverlayItem(point, "Hello", "I'm in Athens, Greece!");
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
		private int severity;
		private GeoPoint point;
		private OverlayItem overlayitem;

		private String temp = "";

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

			temp = "";
			//Log.i(Parser.class.toString(), "Start Element :" + qName);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equalsIgnoreCase("DOCUMENT")) {
			} else if (qName.equalsIgnoreCase("PLACEMARK")) {
				overlayitem = new OverlayItem(point, name, description);
				Drawable icon = null;
				switch (severity) {
				case 1:
					icon = context.getResources().getDrawable(
							R.drawable.marker_green_large);
					break;
				case 2:
					icon = context.getResources().getDrawable(
							R.drawable.marker_green_yellow_large);
					break;
				case 3:
					icon = context.getResources().getDrawable(
							R.drawable.marker_yellow_large);
					break;
				case 4:
					icon = context.getResources().getDrawable(
							R.drawable.marker_orange_large);
					break;
				case 5:
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
			} else if (qName.equalsIgnoreCase("NAME")) {
				name = temp;
			} else if (qName.equalsIgnoreCase("DESCRIPTION")) {
				description = temp;
			} else if (qName.equalsIgnoreCase("POINT")) {
				point = new GeoPoint(latitude, longitude);
			} else if (qName.equalsIgnoreCase("COORDINATES")) {
				String coordinates = temp;
				latitude = (int) (Float.parseFloat(coordinates.substring(0,
						coordinates.indexOf(","))) * 1000000);
				longitude = (int) (Float.parseFloat(coordinates
						.substring(coordinates.indexOf(",") + 1)) * 1000000);
			} else if (qName.equalsIgnoreCase("SEVERITY")) {
				severity = Integer.parseInt(temp);
			}
			temp = "";
		}

		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			String input = new String(ch, start, length);
			temp = temp + input.replaceAll("\n", "").replaceAll("\t", "");
			if (!temp.isEmpty()) {
				//Log.i(Parser.class.toString(),"Content :" + input.replaceAll("\n", "").replaceAll("\t",""));
			}

		}

		public ArrayList<OverlayItem> getResult() {
			return (ArrayList<OverlayItem>) (mOverlay.clone());
		}

	}
}