package flood.monitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class UploadFormActivity extends Activity {

	public String file;
	public Context context = this;
	public UploadFormActivity activity = this;
	static final int UPLOADING_DIALOG = 1;
	public ProgressDialog progressDialog;
	public ProgressThread progressThread;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_GALLERY_IMAGE_REQUEST_CODE = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public Uri fileUri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form);

		Button buttonLoadImage = (Button) findViewById(R.id.pictureButton);
		buttonLoadImage.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showMenu(arg0);
			}
		});
		Button buttonUploadImage = (Button) findViewById(R.id.submitButton);
		buttonUploadImage.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(UPLOADING_DIALOG);
			}
		});
	}

	/*	public void showMenu(View arg0){
	PopupMenu popup = new PopupMenu(context, arg0);
	MenuInflater inflater = popup.getMenuInflater();
	popup.setOnMenuItemClickListener(this);
	inflater.inflate(R.menu.picturesource, popup.getMenu());
	popup.show();
	}*/
	
	public void showMenu(View arg0){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a Source");
		builder.setItems(R.array.picturesource, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        Toast.makeText(getApplicationContext(), "Blah blah", Toast.LENGTH_SHORT).show();
		    }
		});
		AlertDialog alert = builder.create();
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putBoolean("MyBoolean", true);
		savedInstanceState.putDouble("myDouble", 1.9);
		savedInstanceState.putInt("MyInt", 1);
		savedInstanceState.putString("MyString", "Welcome back to Android");
		// etc.
		super.onSaveInstanceState(savedInstanceState);
	}

/*	@Override
	public boolean onMenuItemClick(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.sourcecamera:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
			startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			return true;
		case R.id.sourcegallery:
			intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, CAPTURE_GALLERY_IMAGE_REQUEST_CODE);
			return true;
		default:
			return false;
		}
	}*/

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
		double myDouble = savedInstanceState.getDouble("myDouble");
		int myInt = savedInstanceState.getInt("MyInt");
		String myString = savedInstanceState.getString("MyString");
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case UPLOADING_DIALOG: {
			ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Please wait while loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			return progressDialog;
		}
		default: {
			return null;
		}
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case UPLOADING_DIALOG: {
			progressThread = new ProgressThread(handler);
			progressThread.start();
		}
		default: {
		}
		}
	}

	// Define the Handler that receives messages from the thread and update the
	// progress
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.arg1;
			if (total == 1) {
				dismissDialog(UPLOADING_DIALOG);
				progressThread.setState(ProgressThread.STATE_DONE);
			}
		}
	};
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	        int sd = 0;
	        sd++;
	    } else {
	        return null;
	    }

	    return mediaFile;
	}

	/** Nested class that performs progress calculations (counting) */
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;
		int total;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			mState = STATE_RUNNING;
			total = 0;
			UploadPicture(context, file);
			Message msg = mHandler.obtainMessage();
			msg.arg1 = 1;
			mHandler.sendMessage(msg);
		}

		/*
		 * sets the current state for the thread, used to stop the thread
		 */
		public void setState(int state) {
			mState = state;
		}
	}

	public void UploadPicture(Context context, String file) {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;

		String pathToOurFile = file;
		String urlServer = "http://buzz.acm.ndsu.nodak.edu/hosted/cramirez/floodmonitor/plog-mobupload.php";
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
			// THIS SHOULD BE LOGGED
			// Toast.makeText(context, "Error Message: " + ex.getMessage(),
			// 5000).show();
		} finally {

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAPTURE_GALLERY_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Uri targetUri = data.getData();
				file = getRealPathFromURI(targetUri);
				TextView path = (TextView) findViewById(R.id.pathView);
				path.setText("File: " + file);
			}
		}

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				file = fileUri.getEncodedPath();
				TextView path = (TextView) findViewById(R.id.pathView);
				path.setText("File: " + file);
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Image saved to:\n" + file,
						Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}
