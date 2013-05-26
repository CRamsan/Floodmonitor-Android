package flood.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

import flood.monitor.modules.Connector;
import flood.monitor.modules.kmlparser.Marker;
import flood.monitor.modules.kmlparser.ObjectDataSource;

/**
 * @author Cesar
 * 
 */
public class UploadFormActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_GALLERY_IMAGE_REQUEST_CODE = 200;

	private static final int SOURCE_SELECTION_DIALOG = 10;
	private static final int UPLOADING_DIALOG = 20;

	private static final int MEDIA_TYPE_IMAGE = 1;

	// private final static int UPLOAD_RUNNING = 0;
	// private final static int UPLOAD_COMPLETE = 1;

	private UploadFormActivity activity = this;
	private Context context = this;

	private String file;
	private Uri fileUri;

	private double latitude;
	private double longitude;
	private int coverHeight;
	private String coverType;
	private int severity;
	private String comment;
	private String email;

	private DatePicker date;
	private TimePicker time;
	private EditText commentTextField;
	private EditText coverheightTextField;
	private EditText emailTextField;
	private Spinner severitySpinner;
	private Spinner coverTypeSpinner;

	private int selectedRegionId;
	private int selectedEventId;
	private int selectedBoundaryId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form);

		Button buttonLoadImage = (Button) findViewById(R.id.pictureButton);
		buttonLoadImage.setOnClickListener(new Button.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				showDialog(SOURCE_SELECTION_DIALOG);
			}
		});
		Button buttonSubmitMarker = (Button) findViewById(R.id.submitButton);
		buttonSubmitMarker.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				uploadMarkerDialog();
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

		commentTextField = (EditText) findViewById(R.id.commentText);
		coverheightTextField = (EditText) findViewById(R.id.coverheightText);

		Bundle location = getIntent().getExtras();
		if (location != null) {
			latitude = (location.getDouble("latitude"));
			longitude = (location.getDouble("longitude"));
			TextView latText = (TextView) findViewById(R.id.latitudeValueView);
			TextView lonText = (TextView) findViewById(R.id.longitudeValueView);
			latText.setText(Double.toString(latitude));
			lonText.setText(Double.toString(longitude));

			selectedBoundaryId = location.getInt("boundaryId", -1);
			selectedEventId = location.getInt("eventId", -1);
			selectedRegionId = location.getInt("regionId", -1);

		}

		severitySpinner = (Spinner) findViewById(R.id.severitySpinner);
		severitySpinner.setSelection(0);
		coverTypeSpinner = (Spinner) findViewById(R.id.covertypeSpinner);
		coverTypeSpinner.setSelection(0);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			((DatePicker) findViewById(R.id.datePicker1))
					.setCalendarViewShown(false);
		}

		emailTextField = (EditText) findViewById(R.id.emailText);
		date = (DatePicker) findViewById(R.id.datePicker1);
		time = (TimePicker) findViewById(R.id.timePicker1);

		file = "";
		setResult(RESULT_CANCELED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be
		// "paused").
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		// The activity is about to be destroyed.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// The activity is been brought back to the front.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putString("FilePathVar", file);
		super.onSaveInstanceState(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		TextView path = (TextView) findViewById(R.id.textFileName);
		path.setText(savedInstanceState.getString("FilePathVar"));
		file = savedInstanceState.getString("FilePathVar");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case UPLOADING_DIALOG: {
			ProgressDialog progressDialog = new ProgressDialog(context);
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

	/**
	 * Start the async task to upload a marker with all the information.
	 */
	public void uploadMarkerDialog() {
		if (severitySpinner.getSelectedItemPosition() == 0) {
			Toast.makeText(this, "Please choose a severity", Toast.LENGTH_LONG)
					.show();
		} else {
			new UploadMarkerTask().execute();
		}
	}

	/**
	 * Create a file Uri for saving an image or video
	 * 
	 * @param type
	 *            of media to save
	 * @return a Uri to the file where media will be saved.
	 */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * Create a File for saving an image or video
	 * 
	 * @param type
	 *            of media
	 * @return the file created.
	 */
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
		} else {
			return null;
		}

		return mediaFile;
	}

	/**
	 * Retrieve a string representing the file path of a media file identified
	 * by their Uri.
	 * 
	 * @param contentUri
	 *            of media file to search.
	 * @return path as a String.
	 */
	public String getRealPathFromURI(Uri contentUri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		//TODO Review method used and look for alternative
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

	/**
	 * Async task to upload the marker.
	 * 
	 * @author Cesar
	 * 
	 */
	private class UploadMarkerTask extends AsyncTask<Void, Void, Void> {
		protected boolean taskCompleted = false;
		protected Marker marker = null;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			showDialog(UPLOADING_DIALOG);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				comment = commentTextField.getText().toString();
				email = emailTextField.getText().toString();
				try {
					coverHeight = Integer.parseInt(coverheightTextField
							.getText().toString());
				} catch (Exception e) {
					coverHeight = 0;
				}
				try {
					severity = Integer.parseInt(severitySpinner
							.getSelectedItem().toString());
				} catch (Exception e) {
					severity = 5;
				}
				coverType = coverTypeSpinner.getSelectedItem().toString();
				String observationTime = date.getMonth() + "/"
						+ date.getDayOfMonth() + "/" + date.getYear() + " "
						+ time.getCurrentHour() + ":" + time.getCurrentMinute();
				marker = new Marker(0, new GeoPoint((int) (latitude * 1000000),
						(int) (longitude * 1000000)), observationTime, comment,
						"", severity);

				marker.setBoundaryId(selectedBoundaryId);
				marker.setEventId(selectedEventId);
				marker.setRegionId(selectedRegionId);

				File image = null;
				try {
					if (!file.equalsIgnoreCase("")) {
						image = new File(file);
					}
					int markerId = Connector.SubmitMarker(marker, image,
							coverType, coverHeight, email);
					marker.setId(markerId);

					File mediaStorageDir = new File(
							Environment.getExternalStorageDirectory(),
							Connector.PUBLIC_DIR + File.separator
									+ Connector.DOWNLOAD_DIR + File.separator
									+ marker.getRegionId() + File.separator
									+ marker.getEventId() + File.separator
									+ marker.getBoundaryId() + File.separator);

					if (!mediaStorageDir.exists()) {
						if (!mediaStorageDir.mkdirs()) {
							return null;
						}
					}

					File cacheImage = new File(
							mediaStorageDir.getAbsoluteFile() + File.separator
									+ marker.getId() + ".jpg");

					InputStream inStream = null;
					OutputStream outStream = null;

					try {

						inStream = new FileInputStream(image);
						outStream = new FileOutputStream(cacheImage);

						byte[] buffer = new byte[1024];

						int length;
						// copy the file content in bytes
						while ((length = inStream.read(buffer)) > 0) {

							outStream.write(buffer, 0, length);

						}

						inStream.close();
						outStream.close();

						file = cacheImage.getName();
						marker.setImage(file);

					} catch (Exception e) {
						e.printStackTrace();
					}

					ObjectDataSource data = new ObjectDataSource(activity);
					data.open();
					data.insertMarker(marker);
					data.close();
					taskCompleted = true;
				} catch (Exception e) {

					activity.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(activity,
									"Error while trying to upload data",
									Toast.LENGTH_LONG).show();
						}
					});

					taskCompleted = false;
				}
			} else {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity,
								"We could not contact the server.",
								Toast.LENGTH_LONG).show();
					}
				});
				taskCompleted = true;
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Void none) {
			dismissDialog(UPLOADING_DIALOG);
			if (taskCompleted) {
				Intent markerUploaded = new Intent();
				markerUploaded.putExtra("latitude", marker.getLatitude());
				markerUploaded.putExtra("longitude", marker.getLongitude());
				markerUploaded.putExtra("id", marker.getId());
				markerUploaded.putExtra("severity", marker.getSeverity());
				markerUploaded.putExtra("title", marker.getTitle());
				markerUploaded.putExtra("snippet", marker.getSnippet());
				markerUploaded.putExtra("fileImage", marker.getImage());

				setResult(RESULT_OK, markerUploaded);
				finish();
			}
		}
	}

}
