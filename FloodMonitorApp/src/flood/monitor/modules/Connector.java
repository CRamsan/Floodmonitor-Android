package flood.monitor.modules;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.KMLFile;
import flood.monitor.modules.kmlparser.Marker;
import flood.monitor.modules.kmlparser.Parser;
import flood.monitor.modules.kmlparser.Region;

/**
 * Class that encapsulates all network and file interations.
 * 
 * @author Cesar
 * 
 */
public class Connector {

	public static final String XML_COMMUNICATOR = "http://flood.cs.ndsu.nodak.edu/~ander773/flood/server/index.php";
	public static final String DOWNLOAD_DIR = ".cache";
	public static final String PUBLIC_DIR = "FloodMonitor";

	public static final int PIECE_SIZE = 30000;

	/**
	 * Query the XML_COMMUNICATOR for the current list of regions.
	 * 
	 * @return a list containing all the regions.
	 */
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
		}
		return regions;
	}

	/**
	 * Query the XML_COMMUNICATOR for the current list of events in the
	 * specified region.
	 * 
	 * @param regionId
	 *            of the region to search events for.
	 * @return a list containing all the events in the given region.
	 */
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
		}
		return events;
	}

	public static ArrayList<KMLFile> downloadKML(int boundarytId, int eventId,
			int regionId) {
		OutputStreamWriter request = null;
		String parameters = "data=<phone><command>GetMarkerFile</command><params><boundaryid>"
				+ boundarytId
				+ "</boundaryid><eventid>"
				+ eventId
				+ "</eventid></params></phone>";

		ArrayList<KMLFile> kmlFiles = null;
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

			// read it with BufferedReader
			/*
			 * BufferedReader br = new BufferedReader(new
			 * InputStreamReader(is)); StringBuilder sb = new StringBuilder();
			 * 
			 * String line; while ((line = br.readLine()) != null) {
			 * sb.append(line); } line = sb.toString();
			 */

			kmlFiles = Parser.ParseKMLFiles(is);

			// br.close();

		} catch (IOException e) {
		}
		return kmlFiles;
	}

	public static ArrayList<KMLFile> downloadKML(int baseId, int boundarytId,
			int eventId, int regionId) {
		OutputStreamWriter request = null;
		String parameters = "data=<phone><command>GetMarkerFile</command><params><baseid>"
				+ baseId + "</baseid></params></phone>";

		ArrayList<KMLFile> kmlFiles = null;
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

			kmlFiles = Parser.ParseKMLFiles(is);

		} catch (IOException e) {
		}
		return kmlFiles;
	}

	public static ArrayList<KMLFile> downloadKML(int diffId, int baseId,
			int boundarytId, int eventId, int regionId) {
		OutputStreamWriter request = null;
		String parameters = "data=<phone><command>GetMarkerFile</command><params><baseid>"
				+ baseId
				+ "</baseid><diffid>"
				+ diffId
				+ "</diffid></params></phone>";

		ArrayList<KMLFile> kmlFiles = null;
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

			// read it with BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			line = sb.toString();

			kmlFiles = Parser.ParseKMLFiles(is);

			br.close();
		} catch (IOException e) {
		}
		return kmlFiles;
	}

	public static ArrayList<Marker> downloadMarkers(ArrayList<KMLFile> kmlFiles) {
		InputStream is = null;
		ArrayList<Marker> markers = new ArrayList<Marker>(0);
		try {
			URL url = new URL(XML_COMMUNICATOR);

			/* Open a connection to that URL. */
			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
			ucon.setDoOutput(true);
			ucon.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			ucon.setRequestMethod("POST");
			for (KMLFile file : kmlFiles) {
				String kmlURL = file.getFileURL();

				url = new URL(kmlURL);

				/* Open a connection to that URL. */
				ucon = (HttpURLConnection) url.openConnection();
				is = ucon.getInputStream();
				markers = Parser.ParseMarkers(is);
				for (Marker marker : markers) {
					marker.setEventId(file.getEventId());
					marker.setBoundaryId(file.getBoundaryId());
					marker.setRegionId(file.getRegionId());
				}

			}

		} catch (IOException e) {
		}
		return markers;
	}

	/**
	 * Upload a marker to the server.
	 * 
	 * @param marker
	 *            as an object containing all the required info.
	 * @param image
	 *            optional file that can also be uploaded.
	 * @return an integer greater than 0 representing the id of the submited
	 *         marker. If the return value is less than 0, then an error
	 *         ocurred. A return value of 0 represents a generic network error.
	 */
	public static int SubmitMarker(Marker marker, File image, String coverType,
			int coverHeight, String email) {
		OutputStreamWriter request = null;
		ArrayList<Marker> markers;
		double lat = (marker.getLatitude());
		double lon = (marker.getLongitude());
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
			if (data.length() % PIECE_SIZE != 0) {
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
				+ marker.getObservationTime()
				+ "</observationTime><severity>"
				+ marker.getSeverity()
				+ "</severity><coverType>"
				+ coverType
				+ "</coverType><coverHeight>"
				+ coverHeight
				+ "</coverHeight><uploadTime>"
				+ marker.getObservationTime()
				+ "</uploadTime><email>"
				+ email
				+ "</email><pictureData>"
				+ data + "</pictureData></params></phone>";
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

			InputStream is = ucon.getInputStream();

			// read it with BufferedReader
			/*
			 * BufferedReader br = new BufferedReader(new InputStreamReader(
			 * ucon.getInputStream())); StringBuilder sb = new StringBuilder();
			 * 
			 * String line; while ((line = br.readLine()) != null) {
			 * sb.append(line); } line = sb.toString(); br.close();
			 */

			markers = Parser.ParseMarkers(is);

		} catch (IOException e) {
			return 0;
		}
		return markers.get(0).getId();
	}

	/**
	 * Download the picture associated with the given marker.
	 * 
	 * @param marker
	 *            which image will be downloaded.
	 * @return the file downloaded.
	 */
	public static File downloadPicture(Marker marker) {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), PUBLIC_DIR
						+ File.separator + DOWNLOAD_DIR + File.separator
						+ marker.getRegionId() + File.separator
						+ marker.getEventId() + File.separator
						+ marker.getBoundaryId() + File.separator);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		File file = new File(mediaStorageDir.getAbsoluteFile() + File.separator
				+ marker.getId() + ".jpg");

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

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(response);
				fos.close();

			} catch (IOException e) {
			}
		} else {
			return file;
		}
		return file;
	}

	/**
	 * The folder where we will be storing our information. This folder is
	 * stored on the sd card.
	 * 
	 * @return folder used to store data.
	 */
	public static File getPublicDir() {
		return new File(Environment.getExternalStorageDirectory(), PUBLIC_DIR);
	}

	/**
	 * A folder inside our public directory where we can place temporary files
	 * and other files that should be invisible to the user.
	 * 
	 * @return folder used to hide data.
	 */
	public static File getCacheDir() {
		return new File(Environment.getExternalStorageDirectory(), PUBLIC_DIR
				+ File.separator + DOWNLOAD_DIR);
	}
}
