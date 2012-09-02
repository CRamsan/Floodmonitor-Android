package flood.monitor.modules;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Marker;
import flood.monitor.modules.kmlparser.Parser;
import flood.monitor.modules.kmlparser.Region;

public class Connector {

	public static final String XML_COMMUNICATOR = "http://flood.cs.ndsu.nodak.edu/~ander773/flood/server/index.php";
	public static final String DOWNLOAD_DIR = ".cache";
	public static final String PUBLIC_DIR = "FLoodMonitor";

	public static final int PIECE_SIZE = 30000;
	
	public static ArrayList<Region> downloadGeoRegions() {
		OutputStreamWriter request = null;
		String parameters = "data=<phone><command>GetRegions</command></phone>";

		ArrayList<Region> regions = new ArrayList<Region>(0);

		try {
			URL url = new URL(XML_COMMUNICATOR);

			/* Open a connection to that URL. */
			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
			ucon.setDoOutput(true);
			ucon.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			ucon.setRequestMethod("POST");

			request = new OutputStreamWriter(ucon.getOutputStream());
			request.write(parameters);
			request.flush();
			request.close();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			regions = Parser.ParseRegions(is);

		} catch (IOException e) {
			Log.d("Connector", "Error: " + e);
		}
		return regions;
	}

	public static ArrayList<Event> downloadEvents(int regionId) {

		OutputStreamWriter request = null;
		String parameters = "data=<phone><command>GetEventsByRegionID</command><params><regionid>"
				+ regionId + "</regionid></params></phone>";
		ArrayList<Event> events = new ArrayList<Event>(0);
		try {
			URL url = new URL(XML_COMMUNICATOR);

			/* Open a connection to that URL. */
			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
			ucon.setDoOutput(true);
			ucon.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			ucon.setRequestMethod("POST");

			request = new OutputStreamWriter(ucon.getOutputStream());
			request.write(parameters);
			request.flush();
			request.close();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			events = Parser.ParseEvents(is);
			for (Event event : events) {
				event.setRegionId(regionId);
			}
		} catch (IOException e) {
			Log.d("Connector", "Error: " + e);
		}
		return events;
	}

	public static ArrayList<Marker> downloadMarkers(int boundarytId, int eventId) {
		OutputStreamWriter request = null;
		String parameters = "data=<phone><command>GetMarkerFile</command><params><boundaryid>"
				+ boundarytId
				+ "</boundaryid><eventid>"
				+ eventId
				+ "</eventid></params></phone>";
		ArrayList<Marker> markers = new ArrayList<Marker>(0);
		try {
			URL url = new URL(XML_COMMUNICATOR);

			/* Open a connection to that URL. */
			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
			ucon.setDoOutput(true);
			ucon.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			ucon.setRequestMethod("POST");

			request = new OutputStreamWriter(ucon.getOutputStream());
			request.write(parameters);
			request.flush();
			request.close();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();

			String kmlURL = Parser.ParseFileNames(is);

			url = new URL(kmlURL);

			/* Open a connection to that URL. */
			ucon = (HttpURLConnection) url.openConnection();
			is = ucon.getInputStream();
			markers = Parser.ParseMarkers(is);
			for (Marker marker : markers) {
				marker.setEventId(eventId);
				marker.setBoundaryId(boundarytId);
			}
		} catch (IOException e) {
			Log.d("Connector", "Error: " + e);
		}
		return markers;
	}

	public static void SubmitMarker(Marker marker, File image) {
		OutputStreamWriter request = null;
		float lat = (marker.getPoint().getLatitudeE6() / 1000000f);
		float lon = (marker.getPoint().getLongitudeE6() / 1000000f);
		String timeStamp = new SimpleDateFormat("MM/dd/yyy HH:MM")
				.format(new Date());
		int severity = marker.getSeverity();
		String data = "";
		if (image != null) {
			Bitmap bm = BitmapFactory.decodeFile(image.getAbsolutePath());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 75, baos); // bm is the
																// bitmap
																// object
			byte[] b = baos.toByteArray();
			data = Base64.encodeToString(b, Base64.DEFAULT);
			StringBuilder sb = new StringBuilder();
			int pieces = data.length() / PIECE_SIZE;
			if(data.length() % PIECE_SIZE != 0){
				pieces++;
			}
			for (int i = 0; i < pieces; i++) {
				int offset = (i + 1) * 30000;
				if (offset > data.length()) {
					offset = data.length() - 1;
				}

				sb.append(data.substring(i * 30000, (offset)).replaceAll("\\+",
						"%2B"));
			}
			data = sb.toString();
		}
		String parameters = "data=<phone><command>SubmitMarker</command><params><latitude>"
				+ lat
				+ "</latitude><longitude>"
				+ lon
				+ "</longitude><observationTime>"
				+ "03/14/2012 12:12"
				+ "</observationTime><phoneNumber>"
				+ "111-123-1234"
				+ "</phoneNumber><severity>"
				+ 3
				+ "</severity><coverType>"
				+ 2
				+ "</coverType><coverHeight>"
				+ 2
				+ "</coverHeight><uploadTime>"
				+ "03/14/2012 12:12"
				+ "</uploadTime><pictureData>"
				+ data
				+ "</pictureData></params></phone>";
		try {
			URL url = new URL(XML_COMMUNICATOR);

			/* Open a connection to that URL. */

			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

			ucon.setDoOutput(true);
			ucon.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			ucon.setRequestMethod("POST");

			request = new OutputStreamWriter(ucon.getOutputStream());
			request.write(parameters);
			request.flush();
			request.close();

			// read it with BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(
					ucon.getInputStream()));
			StringBuilder sb = new StringBuilder();

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			line = sb.toString();
			br.close();

		} catch (IOException e) {
			Log.d("Connector", "Error: " + e);
		}
	}

	public static File downloadPicture(Marker marker, int id) {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), "FloodMonitor");
		String subDir = "tmp";
		File file = new File(mediaStorageDir.getPath() + File.separator
				+ DOWNLOAD_DIR + File.separator + subDir + File.separator
				+ marker.getId());
		if (!file.exists()) {
			try {
				URL url = new URL(marker.getImage());
				InputStream in = new BufferedInputStream(url.openStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int n = 0;
				while (-1 != (n = in.read(buf))) {
					out.write(buf, 0, n);
				}
				out.close();
				in.close();
				byte[] response = out.toByteArray();

				FileOutputStream fos = new FileOutputStream("");
				fos.write(response);
				fos.close();

			} catch (IOException e) {
				Log.d("Connector", "Error: " + e);
			}
		}
		return file;
	}
}
