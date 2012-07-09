package flood.monitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.Overlay;

import flood.monitor.modules.Connector;
import flood.monitor.overlay.Marker;
import flood.monitor.overlay.MarkersOverlay;
import flood.monitor.overlay.RegionsOverlay;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MarkerDIalogActivity extends Activity {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private int latitude;
	private int longitude;
	private MarkerDIalogActivity activity;
	// ===========================================================
	// Methods from Activity
	// ===========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Activity is first created.
		activity = this;
		Bundle markerData = getIntent().getExtras();
		if (markerData != null) {
			latitude = (markerData.getInt("latitude"));
			longitude = (markerData.getInt("longitude"));
			String title = markerData.getString("title");
			String desc = markerData.getString("desc");
			TextView titleView = (TextView) findViewById(R.id.textViewTitle);
			TextView descView = (TextView) findViewById(R.id.textViewDescription);
			TextView latView = (TextView) findViewById(R.id.textViewLatitude);
			TextView lonView = (TextView) findViewById(R.id.textViewLatitude);
			TextView addressView = (TextView) findViewById(R.id.textViewAddress);
			ProgressBar circle = (ProgressBar) findViewById(R.id.progressBarAddress);

			latView.setText(Integer.toString(latitude));
			lonView.setText(Integer.toString(latitude));
			titleView.setText(title);
			descView.setText(desc);
			addressView.setText("");
			circle.setVisibility(View.GONE);
		}
		setResult(RESULT_CANCELED);
		setContentView(R.layout.markerdialog);
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
				addressList = geocoder.getFromLocation(latitude/1000000, longitude/1000000, 1);
				if (addressList.size() == 0) {
					return null;
				}
				String[] items = new String[addressList.size()];
				for (int i = 0; i < addressList.size(); i++) {
					int addSize = addressList.get(i).getMaxAddressLineIndex();
					StringBuffer sb = new StringBuffer(100);
					for (int j = 0; j < addSize; j++) {
						sb.append(addressList.get(i).getAddressLine(j));
						if (j < addSize - 1)
							sb.append(", ");
					}
					items[i] = sb.toString();
				}
				((TextView) findViewById(R.id.textViewAddress)).setText(items[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			((ProgressBar) findViewById(R.id.progressBarAddress))
					.setVisibility(View.GONE);
		}
	}

}
