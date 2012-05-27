package flood.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

public class MapViewActivity extends MapActivity {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private MapView mapView;
	private Locator locator;
	private CustomOverlay overlay;

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

		// THIS IS TEMPORARY ONLY
		 GeoPoint point = new GeoPoint(46901130, -96792070);
		 OverlayItem overlayitem = new OverlayItem(point, "Hello",
		 "I'm in Athens, Greece!");
		//try {
			ArrayList<OverlayItem> mOverlays = openAsset();
			//overlay.setOverlay(mOverlays);
			for(int i = 0; i < mOverlays.size(); i++)
			{
				overlay.addOverlay(mOverlays.get(i));
			}
			overlay.addOverlay(overlayitem);
			mapOverlays.add(overlay);
			
		//} catch (Exception e) {
		//	Log.i("ERROR","WHY!");
		//}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
	}

	@Override
	protected void onResume() {
		super.onResume();
		// locator.startListening(this);
		// The activity has become visible (it is now "resumed").
	}

	@Override
	protected void onPause() {
		super.onPause();
		// locator.stopListening(this);
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
		switch (item.getItemId()) {
		case R.id.item1:// Setings
			return true;
		case R.id.item2:// Upload
			Intent intent = new Intent(MapViewActivity.this,
					UploadFormActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

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
			itemList = parser.Parse(file, stream);
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