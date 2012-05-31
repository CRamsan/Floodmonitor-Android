package flood.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import flood.monitor.modules.kmlparser.Parser;
import flood.monitor.overlay.CustomOverlay;

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
	final static int WAITING_FOR_MARKER_BUTTON_PRESS = 0;
	final static int WAITING_FOR_MARKER_UPLOAD = 1;
	final static int RESTORE_INITIAL= 2;

	// ===========================================================
	// Fields
	// ===========================================================
	private MapView mapView;
	private Locator locator;
	private CustomOverlay overlay;
	private int markerState;

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

		ArrayList<OverlayItem> mOverlays = openAsset();
		overlay.setOverlay(mOverlays);
		mapOverlays.add(overlay);
		markerState = 0;
		invalidateOptionsMenu();
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
		case R.id.item1:// Upload
			
			intent = new Intent(MapViewActivity.this, UploadFormActivity.class);
			intent.putExtra("latitude", locator.getBestLocation().getLatitude());
			intent.putExtra("longitude", locator.getBestLocation()
					.getLongitude());
			startActivity(intent);
			markerState = RESTORE_INITIAL;
			invalidateOptionsMenu();
			return true;
		case R.id.item2:// Settings
			intent = new Intent(MapViewActivity.this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.item0://Update
			locator.updateListening(this);
			return true;
		case R.id.item6://PlaceMarker
			markerState = WAITING_FOR_MARKER_UPLOAD;
			invalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		switch (markerState) {
		case WAITING_FOR_MARKER_BUTTON_PRESS:
			menu.removeItem(R.id.item1);
			return true;
		case WAITING_FOR_MARKER_UPLOAD:
			menu.removeItem(R.id.item6);
			menu.add(R.id.item1);
			return true;
		case RESTORE_INITIAL:
			menu.removeItem(R.id.item1);
			menu.add(R.id.item6);
			return true;
		default:
			return false;
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

		if (requestCode == CAPTURE_GALLERY_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

			}
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
	public ArrayList<OverlayItem> openAsset() {
		String file = "";
		InputStream stream = null;
		AssetManager assetManager = getAssets();
		Parser parser = new Parser();
		ArrayList<OverlayItem> itemList = new ArrayList<OverlayItem>(0);
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