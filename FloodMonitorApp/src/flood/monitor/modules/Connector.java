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
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the
																// bitmap
																// object
			byte[] b = baos.toByteArray();
			data = Base64.encodeToString(b, Base64.DEFAULT);
		}
		String parameters = "data=<phone><command>SubmitMarker</command><params><latitude>"
				+ lat
				+ "</latitude><longitude>"
				+ lon
				+ "</longitude><observationTime>"
				+ timeStamp
				+ "</observationTime><phoneNumber>"
				+ "111-123-1234"
				+ "</phoneNumber><severity>"
				+ severity
				+ "</severity><coverType>"
				+ 2
				+ "</coverType><coverHeight>"
				+ 2
				+ "</coverHeight><uploadTime>"
				+ timeStamp
				+ "</uploadTime><pictureData>"
				+ data
				+ "</pictureData></params></phone>";
		try {
			URL url = new URL(XML_COMMUNICATOR);

			/* Open a connection to that URL. */

			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
			
			 ucon.setDoOutput(true); ucon.setRequestProperty("Content-Type",
			 "application/x-www-form-urlencoded");
			 ucon.setRequestMethod("POST");
			 
			 request = new OutputStreamWriter(ucon.getOutputStream());
			 request.write(parameters); request.flush(); request.close();
			 
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

				/* Open a connection to that URL. */
				HttpURLConnection ucon = (HttpURLConnection) url
						.openConnection();

				/*
				 * Define InputStreams to read from the URLConnection.
				 */
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				/*
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1).
				 */
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}

				/* Convert the Bytes read to a String. */
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();

			} catch (IOException e) {
				Log.d("Connector", "Error: " + e);
			}
		}
		return file;
	}

	private static void UploadData(Context context, String latitude,
			String longitude, String hoursAgo, String minutesAgo,
			String runoff, String coverDepth, String coverType, String comment,
			String email) {
		try {
			URL siteUrl = new URL(
					"http://192.168.0.100/plogger/plog-admin/plog-mobupload.php");
			HttpURLConnection conn = (HttpURLConnection) siteUrl
					.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());

			HashMap<String, String> data = new HashMap<String, String>();
			// Name&Value
			data.put("latitude", latitude);
			data.put("longitude", longitude);
			data.put("hours", hoursAgo);
			data.put("min", minutesAgo);
			data.put("runoff", runoff);
			data.put("depth", coverDepth);
			data.put("cover", coverType);
			data.put("comment", comment);
			data.put("contact", email);

			Set<String> keys = data.keySet();
			Iterator<String> keyIter = keys.iterator();
			String content = "";
			for (int i = 0; keyIter.hasNext(); i++) {
				Object key = keyIter.next();
				if (i != 0) {
					content += "&";
				}
				content += key + "="
						+ URLEncoder.encode(data.get(key), "UTF-8");
			}
			System.out.println(content);
			out.writeBytes(content);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			in.close();
		} catch (Exception ex) {
			Toast.makeText(context, "Error Message: " + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private static void UploadPicture(Context context, String file) {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;

		String pathToOurFile = file;
		String urlServer = "http://192.168.0.100/plogger/plog-admin/plog-picture.php";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ pathToOurFile + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			// Responses from the server (code and message)
			// int serverResponseCode = connection.getResponseCode();
			// String serverResponseMessage = connection.getResponseMessage();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
		} catch (Exception ex) {
			Toast.makeText(context, "Error Message: " + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
}
