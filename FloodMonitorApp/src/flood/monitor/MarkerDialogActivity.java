package flood.monitor;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MarkerDialogActivity extends Activity {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private double latitude;
	private double longitude;
	private int mode;
	private boolean upload;
	private MarkerDialogActivity activity;

	// ===========================================================
	// Methods from Activity
	// ===========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Activity is first created.
		setContentView(R.layout.markerdialog);
		activity = this;
		upload = false;
		Bundle markerData = getIntent().getExtras();
		if (markerData != null) {
			latitude = (markerData.getDouble("latitude"));
			longitude = (markerData.getDouble("longitude"));
			mode = markerData.getInt("mode");
			upload = markerData.getBoolean("upload");
			TextView titleView;
			TextView descView;
			TextView imageView;
			ProgressBar imageCircle;
			TextView latView;
			TextView lonView;
			TextView addressView;
			ProgressBar circle;

			switch (mode) {
			case MapViewActivity.MARKER_LOCATION:
				titleView = (TextView) findViewById(R.id.textViewTitle);
				descView = (TextView) findViewById(R.id.textViewDescription);
				latView = (TextView) findViewById(R.id.textViewLatitude);
				lonView = (TextView) findViewById(R.id.textViewLongitude);
				imageView = (TextView) findViewById(R.id.textViewImageLoading);
				imageCircle = (ProgressBar) findViewById(R.id.progressBarImageLoading);
				addressView = (TextView) findViewById(R.id.textViewAddress);
				circle = (ProgressBar) findViewById(R.id.progressBarAddress);

				latView.setText(activity.getResources().getString(
						R.string.text_Latitude)
						+ ": " + Double.toString(latitude));
				lonView.setText(activity.getResources().getString(
						R.string.text_Longitude)
						+ ": " + Double.toString(longitude));

				titleView.setVisibility(View.GONE);
				descView.setVisibility(View.GONE);
				latView.setVisibility(View.VISIBLE);
				lonView.setVisibility(View.VISIBLE);
				addressView.setVisibility(View.VISIBLE);
				circle.setVisibility(View.GONE);
				imageView.setVisibility(View.GONE);
				imageCircle.setVisibility(View.GONE);

				new GetAddressTask().execute();
				break;
			case MapViewActivity.MARKER_UPLOAD:
				titleView = (TextView) findViewById(R.id.textViewTitle);
				descView = (TextView) findViewById(R.id.textViewDescription);
				imageView = (TextView) findViewById(R.id.textViewImageLoading);
				imageCircle = (ProgressBar) findViewById(R.id.progressBarImageLoading);
				latView = (TextView) findViewById(R.id.textViewLatitude);
				lonView = (TextView) findViewById(R.id.textViewLongitude);
				addressView = (TextView) findViewById(R.id.textViewAddress);
				circle = (ProgressBar) findViewById(R.id.progressBarAddress);

				String title = (markerData.getString("title"));
				String desc = (markerData.getString("desc"));
				titleView.setText(activity.getResources().getString(
						R.string.text_Title)
						+ ": " + title);
				descView.setText(activity.getResources().getString(
						R.string.text_Description)
						+ ": " + desc);
				latView.setText(activity.getResources().getString(
						R.string.text_Latitude)
						+ ": " + Double.toString(latitude));
				lonView.setText(activity.getResources().getString(
						R.string.text_Longitude)
						+ ": " + Double.toString(longitude));

				titleView.setVisibility(View.VISIBLE);
				descView.setVisibility(View.VISIBLE);
				latView.setVisibility(View.VISIBLE);
				lonView.setVisibility(View.VISIBLE);
				addressView.setVisibility(View.VISIBLE);
				circle.setVisibility(View.VISIBLE);
				imageView.setVisibility(View.VISIBLE);
				imageCircle.setVisibility(View.VISIBLE);
				new GetAddressTask().execute();
				new DownloadImageTask().execute();
				break;
			default:
				break;
			}
		}
		Button buttonCancel = (Button) findViewById(R.id.markerCancelButton);
		buttonCancel.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		Button buttonUpload = (Button) findViewById(R.id.markerSubmitButton);
		buttonUpload.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(activity, UploadFormActivity.class);
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
				startActivityForResult(intent, MapViewActivity.UPLOAD_INTENT);
			}
		});
		if (upload) {
			buttonUpload.setVisibility(View.VISIBLE);
		} else {
			buttonUpload.setVisibility(View.GONE);
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
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
	}

	private class GetAddressTask extends AsyncTask<Void, Void, Void> {
		protected boolean taskCompleted = false;
		private String address;

		@Override
		protected void onPreExecute() {
			((ProgressBar) findViewById(R.id.progressBarAddress))
					.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
				List<Address> addressList;
				try {
					addressList = geocoder.getFromLocation(latitude, longitude,
							1);
					if (addressList.size() == 0) {
						return null;
					}
					StringBuffer sb = new StringBuffer(50);
					String[] items = new String[addressList.size()];
					for (int i = 0; i < addressList.size(); i++) {
						int addSize = addressList.get(i)
								.getMaxAddressLineIndex();
						for (int j = 0; j < addSize; j++) {
							sb.append(addressList.get(i).getAddressLine(j));
							if (j < addSize - 1)
								sb.append(", ");
						}
					}
					items[0] = sb.toString();
					address = items[0];
				} catch (IOException e) {
					e.printStackTrace();
				}
				taskCompleted = true;
			} else {
				address = "Could not be determined";
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

		@Override
		protected void onPostExecute(Void none) {
			if (taskCompleted) {
				((TextView) findViewById(R.id.textViewAddress))
						.setText(activity.getResources().getString(
								R.string.text_Address)
								+ ": " + address);
				((ProgressBar) findViewById(R.id.progressBarAddress))
						.setVisibility(View.GONE);

			}
		}
	}

	private class DownloadImageTask extends AsyncTask<Void, Void, Void> {
		protected boolean taskCompleted = false;
		protected boolean imageLoaded = false;
		private String message;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				taskCompleted = true;

			} else {
				message = "Image could not be loaded";
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity,
								"We could not contact the server.",
								Toast.LENGTH_LONG).show();
					}
				});
				taskCompleted = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			if (taskCompleted) {
				if (imageLoaded) {
					((TextView) findViewById(R.id.textViewImageLoading))
							.setVisibility(View.GONE);
				} else {
					((TextView) findViewById(R.id.textViewImageLoading))
							.setVisibility(View.VISIBLE);
				}
			} else {
				((TextView) findViewById(R.id.textViewImageLoading))
						.setVisibility(View.VISIBLE);
			}
			((TextView) findViewById(R.id.textViewImageLoading))
			.setText(message);
			((ProgressBar) findViewById(R.id.progressBarImageLoading))
					.setVisibility(View.GONE);
		}
	}

}
