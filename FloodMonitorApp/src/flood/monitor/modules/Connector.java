package flood.monitor.modules;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.util.ByteArrayBuffer;

import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Region;
import flood.monitor.overlay.Marker;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class Connector {

	public static final String XML_COMMUNICATOR = "http://flood.cs.ndsu.nodak.edu/~ander773/flood/server/index.php";
	public static final String DOWNLOAD_DIR = ".cache";

	public static File downloadEvents() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String filename = "events" + timeStamp + ".xml";
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), "FloodMonitor");
		File file = new File(mediaStorageDir.getPath() + File.separator
				+ DOWNLOAD_DIR + File.separator + filename);
		OutputStreamWriter request = null;
		String parameters = "data=<phone><command>GetEvents</command></phone>";

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
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
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
		return file;
	}

	public static File[] downloadRegions(Event event) {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), "FloodMonitor");
		String subDir = event.getName();
		File[] files = new File[event.getRegions().size()];
		
		for (int i = 0; i < event.getRegions().size(); i++) {
			File file = new File(mediaStorageDir.getPath() + File.separator
					+ DOWNLOAD_DIR + File.separator + subDir + File.separator
					+ event.getRegions().get(i).getName() + ".kml");

			try {
				URL url = new URL(event.getRegions().get(i).getKml());

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
				files[i] = file;

			} catch (IOException e) {
				Log.d("Connector", "Error: " + e);
			}
		}
		return files;
	}

	public static File downloadPicture(Marker marker, int id) {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), "FloodMonitor");
		String subDir = marker.getRegion().getEvent().getName() + File.separator + marker.getRegion().getName();
			File file = new File(mediaStorageDir.getPath() + File.separator
					+ DOWNLOAD_DIR + File.separator + subDir + File.separator
					+ marker.getId());

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
		return file;
	}
	
	public static void UploadData(Context context, String latitude,
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

			Set keys = data.keySet();
			Iterator keyIter = keys.iterator();
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
			Toast.makeText(context, "Error Message: " + ex.getMessage(), 5000)
					.show();
		}
	}

	public static void UploadPicture(Context context, String file) {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;

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
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
		} catch (Exception ex) {
			Toast.makeText(context, "Error Message: " + ex.getMessage(), 5000)
					.show();
		}
	}
}