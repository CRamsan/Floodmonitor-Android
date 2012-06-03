package flood.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import flood.monitor.modules.Locator;
import flood.monitor.modules.kmlparser.MarkerManager;
import flood.monitor.modules.kmlparser.Parser;
import flood.monitor.modules.kmlparser.SQLliteManager;
import flood.monitor.overlay.CustomOverlay;
import flood.monitor.overlay.CustomOverlayItem;

/**
 * MapViewActivity.java Purpose: Activity that represents the map
 * 
 * @author Cesar Ramirez
 * @version 25/05/12
 */

public class MapViewActivity extends MapActivity implements OnTouchListener {

	// ===========================================================
	// Constants
	// ===========================================================
	private final static int ENABLE_MARKER = 0;
	private final static int ENABLE_UPLOAD = 1;
	private final static int UPLOAD_REQUEST = 100;
	private final static String PREFS_NAME = "MapViewPref";

	// ===========================================================
	// Fields
	// ===========================================================
	private MapView mapView;
	private Locator locator;
	private CustomOverlay overlay;
	private MarkerManager manager;
	private int markerState;
	private boolean installedBefore;

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
		// The activity is launched or restarted after been killed.

		setContentView(R.layout.map);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		locator = new Locator(this);
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.ic_launcher);
		overlay = new CustomOverlay(drawable, this);
		List<Overlay> mapOverlays = mapView.getOverlays();

		ArrayList<CustomOverlayItem> mOverlays = openAsset();
		overlay.setOverlay(mOverlays);
		mapOverlays.add(overlay);
		markerState = ENABLE_MARKER;
		invalidateOptionsMenu();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		installedBefore = settings.getBoolean("Install_State", false);

		if (installedBefore) {
			//Proceed to load from db
		} else {
			SharedPreferences.Editor editor = settings.edit();
			installedBefore = true;
			editor.putBoolean("Install_State", installedBefore);
			editor.commit();
			//create DB
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
	}

	@Override
	protected void onResume() {
		super.onResume();
		locator.startListening(this);
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
		locator.stopListening(this);
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
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.menuItemUpload:// Upload
			intent = new Intent(MapViewActivity.this, UploadFormActivity.class);
			intent.putExtra("latitude", overlay.getMarkerLocation()
					.getLatitude());
			intent.putExtra("longitude", overlay.getMarkerLocation()
					.getLongitude());
			startActivityForResult(intent, UPLOAD_REQUEST);
			return true;
		case R.id.menuItemSettings:// Settings
			intent = new Intent(MapViewActivity.this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.menuItemRefresh:// Update
			locator.updateListening(this);
			return true;
		case R.id.menuItemPlaceMarker:// PlaceMarker
			markerState = ENABLE_UPLOAD;
			overlay.initiateDragMarker(locator.getBestLocation());
			invalidateOptionsMenu();
			return true;
		case R.id.menuItemExit:// Exit
			finish();
			return true;
		case R.id.menuItemAbout:
			intent = new Intent(MapViewActivity.this, AboutActivity.class);
			startActivity(intent);
			return true;
		case R.id.menuItemHelp:
			intent = new Intent(MapViewActivity.this, HelpActivity.class);
			startActivity(intent);
			return true;
		case R.id.menuItemCancel:
			markerState = ENABLE_MARKER;
			overlay.stopDragMarker();
			invalidateOptionsMenu();
			mapView.invalidate();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		switch (markerState) {
		case ENABLE_MARKER:
			menu.removeItem(R.id.menuItemUpload);
			menu.removeItem(R.id.menuItemCancel);
			return true;
		case ENABLE_UPLOAD:
			menu.removeItem(R.id.menuItemPlaceMarker);
			return true;
		default:
			return true;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == UPLOAD_REQUEST) {
			markerState = ENABLE_MARKER;
			overlay.stopDragMarker();
			invalidateOptionsMenu();
			mapView.invalidate();
		}
	}

	// ===========================================================
	// Methods from Parent
	// ===========================================================

	// ===========================================================
	// Methods from Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	public void updateBestLocation() {
		overlay.updateBestLocation(locator.getBestLocation());
		mapView.invalidate();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Debug
	// ===========================================================
	public ArrayList<CustomOverlayItem> openAsset() {
		String file = "";
		InputStream stream = null;
		AssetManager assetManager = getAssets();
		Parser parser = new Parser();
		ArrayList<CustomOverlayItem> itemList = new ArrayList<CustomOverlayItem>(0);
		try {
			stream = assetManager.open("sample.kml");
			itemList = parser.Parse(file, stream, this);
		} catch (IOException e) {
			// handle
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		return itemList;
	}

}