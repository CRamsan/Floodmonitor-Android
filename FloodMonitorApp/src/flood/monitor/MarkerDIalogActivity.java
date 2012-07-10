package flood.monitor;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MarkerDIalogActivity extends Activity {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private int latitude;
	private int longitude;
	private int mode;
	private boolean upload;
	private MarkerDIalogActivity activity;

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
			latitude = (markerData.getInt("latitude"));
			longitude = (markerData.getInt("longitude"));
			mode = markerData.getInt("mode");
			upload = markerData.getBoolean("upload");
			switch (mode) {
			case MapViewActivity.MARKER_LOCATION:
				TextView latView = (TextView) findViewById(R.id.textViewLatitude);
				TextView lonView = (TextView) findViewById(R.id.textViewLongitude);
				latView.setText(Integer.toString(latitude));
				lonView.setText(Integer.toString(longitude));
				ProgressBar circle = (ProgressBar) findViewById(R.id.progressBarAddress);
				circle.setVisibility(View.GONE);

				String title = markerData.getString("title");
				String desc = markerData.getString("desc");

				TextView titleView = (TextView) findViewById(R.id.textViewTitle);
				TextView descView = (TextView) findViewById(R.id.textViewDescription);
				TextView addressView = (TextView) findViewById(R.id.textViewAddress);

				titleView.setText("");
				descView.setText("");
				addressView.setText("");
				new GetAddressTask().execute();
				break;
			case MapViewActivity.MARKER_UPLOAD:

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
				float lat = latitude / 1000000f;
				float lon = longitude / 1000000f;
				intent.putExtra("latitude", lat);
				intent.putExtra("longitude", lon);
				startActivityForResult(intent, MapViewActivity.PROCESS_REQUEST);
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
		private String address;

		@Override
		protected void onPreExecute() {
			((ProgressBar) findViewById(R.id.progressBarAddress))
					.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
			List<Address> addressList;
			try {
				float lat = latitude / 1000000f;
				float lon = longitude / 1000000f;
				addressList = geocoder.getFromLocation(lat, lon, 1);
				if (addressList.size() == 0) {
					return null;
				}
				StringBuffer sb = new StringBuffer(50);
				String[] items = new String[addressList.size()];
				for (int i = 0; i < addressList.size(); i++) {
					int addSize = addressList.get(i).getMaxAddressLineIndex();
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

			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			((TextView) findViewById(R.id.textViewAddress)).setText(address);
			((ProgressBar) findViewById(R.id.progressBarAddress))
					.setVisibility(View.GONE);
		}
	}

}
