package flood.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import flood.monitor.modules.Connector;
import flood.monitor.modules.Locator;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Parser;
import flood.monitor.modules.kmlparser.Region;
import flood.monitor.overlay.EventsOverlay;
import flood.monitor.overlay.IOverlay;
import flood.monitor.overlay.LimitedMapView;
import flood.monitor.overlay.Marker;
import flood.monitor.overlay.MarkersOverlay;
import flood.monitor.overlay.RegionsOverlay;

/**
 * MapViewActivity.java Purpose: Activity that represents the map
 * 
 * @author Cesar Ramirez
 * @version 25/05/12
 */

public class SearchActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.search);

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			search(query);
		}
	}

	public void search(String query) {
		/*try {
			List<Address> addressList = geocoder.getFromLocationName(query, 5);
			ArrayAdapter<Address> adapter = new ArrayAdapter<Address>(activity,
					R.layout.searchresult, addressList);
			setListAdapter(adapter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

}