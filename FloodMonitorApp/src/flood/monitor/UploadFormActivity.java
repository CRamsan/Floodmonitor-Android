package flood.monitor;

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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UploadFormActivity extends Activity {

	// ===========================================================
	// Constants
	// ===========================================================
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_GALLERY_IMAGE_REQUEST_CODE = 200;

	private static final int SOURCE_SELECTION_DIALOG = 10;
	private static final int UPLOADING_DIALOG = 20;

	private static final int MEDIA_TYPE_IMAGE = 1;

	private final static int UPLOAD_RUNNING = 0;
	private final static int UPLOAD_COMPLETE = 1;

	// ===========================================================
	// Fields
	// ===========================================================
	private UploadFormActivity activity = this;
	private Context context = this;

	private String file;

	private ProgressDialog progressDialog;
	private ProgressThread progressThread;
	private Uri fileUri;
	private double latitude;
	private double longitude;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from Activity
	// ===========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form);

		Button buttonLoadImage = (Button) findViewById(R.id.pictureButton);
		buttonLoadImage.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(SOURCE_SELECTION_DIALOG);
			}
		});
		Button buttonUploadImage = (Button) findViewById(R.id.submitButton);
		buttonUploadImage.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(UPLOADING_DIALOG);
			}
		});
		Button buttonCancel = (Button) findViewById(R.id.cancelButton);
		buttonCancel.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		Bundle location = getIntent().getExtras();
		if (location != null) {
			latitude = (location.getDouble("latitude"));
			longitude = (location.getDouble("longitude"));
			TextView latText = (TextView) findViewById(R.id.latitudeValueView);
			TextView lonText = (TextView) findViewById(R.id.longitudeValueView);
			latText.setText(Double.toString(latitude));
			lonText.setText(Double.toString(longitude));
		}
		setResult(RESULT_CANCELED);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
	}

	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be
		// "paused").
	}

	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// The activity is about to be destroyed.
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// The activity is been brought back to the front.
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putString("FilePathVar", file);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		TextView path = (TextView) findViewById(R.id.textFileName);
		path.setText(savedInstanceState.getString("FilePathVar"));
		file = savedInstanceState.getString("FilePathVar");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAPTURE_GALLERY_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Uri targetUri = data.getData();
				file = getRealPathFromURI(targetUri);
				TextView path = (TextView) findViewById(R.id.textFileName);
				path.setText(file);
			}
		}

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			MediaScannerConnection.scanFile(this,
					new String[] { file.toString() }, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						public void onScanCompleted(String path, Uri uri) {
							Log.i("ExternalStorage", "Scanned " + path + ":");
							Log.i("ExternalStorage", "-> uri=" + uri);
						}
					});
			if (resultCode == RESULT_OK) {
				try {
					// file = fileUri.getEncodedPath();
					TextView path = (TextView) findViewById(R.id.textFileName);
					path.setText(file);
					// Image captured and saved to fileUri specified in the
					// Intent
					Toast.makeText(this, "Image saved to:\n" + file,
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Log.i(UploadFormActivity.class.toString(),
							"Something went terribly bad and I still can't figure out what it is");
					Toast.makeText(
							this,
							"Error while retrieving picture, select this picture through the gallery please",
							Toast.LENGTH_LONG).show();
				}
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case UPLOADING_DIALOG: {
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Please wait while loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			return progressDialog;
		}
		case SOURCE_SELECTION_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick a Source");
			builder.setItems(R.array.picturesource,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							Intent intent;
							switch (item) {
							case 0:
								intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create
																					// a
																					// file
																					// to
																					// save
																					// the
																					// image
								if (fileUri == null) {
									Log.i(UploadFormActivity.class.toString(),
											"fileUri empty at createDialog()");
								}
								file = fileUri.getPath();
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										fileUri); // set the image file name
								startActivityForResult(intent,
										CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
								break;
							case 1:
								intent = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(intent,
										CAPTURE_GALLERY_IMAGE_REQUEST_CODE);
								break;
							default:
							}
						}
					});
			return builder.create();
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
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					int state = msg.arg1;
					if (state == UPLOAD_COMPLETE) {
						setResult(RESULT_OK);
						dismissDialog(UPLOADING_DIALOG);
						activity.finish();
					}
				}
			};
			progressThread = new ProgressThread(handler);
			progressThread.start();
		}
		default: {
		}
		}
	}

	// ===========================================================
	// Methods from Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), "FloodMonitor");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
			Log.i(UploadFormActivity.class.toString(), "fileUri created");
		} else {
			Log.i(UploadFormActivity.class.toString(),
					"fileUri empty at creation");
			return null;
		}

		return mediaFile;
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

	public void UploadPicture(Context context, String file) {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;

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
			//int serverResponseCode = connection.getResponseCode();
			//String serverResponseMessage = connection.getResponseMessage();

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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class ProgressThread extends Thread {
		private Handler mHandler;

		public ProgressThread(Handler h) {
			this.mHandler = h;
		}

		public void run() {
			Message msg1 = mHandler.obtainMessage();
			msg1.arg1 = UPLOAD_RUNNING;
			mHandler.sendMessage(msg1);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// UploadPicture(context, file);
			Message msg2 = mHandler.obtainMessage();
			msg2.arg1 = UPLOAD_COMPLETE;
			mHandler.sendMessage(msg2);
		}
	}

}
