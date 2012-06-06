package flood.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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

import flood.monitor.abstracts.ModuleEventListener;
import flood.monitor.modules.Connector;
import flood.monitor.modules.Locator;
import flood.monitor.modules.kmlparser.MarkerManager;
import flood.monitor.modules.kmlparser.Parser;
import flood.monitor.modules.kmlparser.Parser.Region;
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
	// STATES
	private final static int ENABLE_MARKER = 0;
	private final static int ENABLE_UPLOAD = 1;
	// UPLOAD STATES
	private final static int UPLOAD_RUNNING = 0;
	private final static int UPLOAD_COMPLETE = 1;
	private final static int UPLOAD_NOTCOMPLETED = 2;
	// REQUEST CODES
	private final static int UPLOAD_REQUEST = 100;
	private final static int DOWNLOAD_DIALOG = 200;
	private final static int REGION_SELECT_DIALOG = 300;
	// PREFERENCES
	private final static String PREFS_NAME = "MapViewPref";
	private final static String INSTALL_STATE = "Install_State";

	// ===========================================================
	// Fields
	// ===========================================================
	private Locator locator;
	private CustomOverlay overlay;
	private ArrayList<Region> regions;

	private ProgressDialog progressDialog;
	private ProgressThread progressThread;
	private Connector connector;

	private MapViewActivity activity = this;

	private int markerState;
	private boolean installedBefore;
	private boolean worldLoaded;

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
		if (savedInstanceState == null) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			installedBefore = settings.getBoolean(INSTALL_STATE, false);
			if (installedBefore) {
				onInstall();
			}
			onInitialize();
		} else {
			onRecreate();
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
		invalidateOptionsMenu();
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
		case R.id.menuItemRefresh:// Refresh
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
		case R.id.menuItemAbout: // ABout
			intent = new Intent(MapViewActivity.this, AboutActivity.class);
			startActivity(intent);
			return true;
		case R.id.menuItemHelp: // Help
			intent = new Intent(MapViewActivity.this, HelpActivity.class);
			startActivity(intent);
			return true;
		case R.id.menuItemCancel: // Cancel
			markerState = ENABLE_MARKER;
			overlay.stopDragMarker();
			invalidateOptionsMenu();
			((MapView) findViewById(R.id.mapview)).invalidate();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DOWNLOAD_DIALOG: {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Getting list of current events");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			return progressDialog;
		}
		case REGION_SELECT_DIALOG: {
			CharSequence[] items = new String[regions.size()];
			for(int i  = 0; i < regions.size(); i++){
				items[i] = regions.get(i).getName();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick a color");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	dismissDialog(REGION_SELECT_DIALOG);
			    }
			});
			builder.setCancelable(false);
			AlertDialog alert = builder.create();
			return alert;
		}
		default: {
			return null;
		}
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DOWNLOAD_DIALOG: {
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					int state = msg.arg1;
					if (state == UPLOAD_COMPLETE) {
						worldLoaded = true;
						activity.runOnUiThread(new Runnable() {
							public void run() {
								dismissDialog(DOWNLOAD_DIALOG);
								showDialog(REGION_SELECT_DIALOG);
							}
						});

					}else if(state == UPLOAD_NOTCOMPLETED){
						activity.runOnUiThread(new Runnable() {
							public void run() {
								dismissDialog(DOWNLOAD_DIALOG);
							}
						});
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == UPLOAD_REQUEST) {
			markerState = ENABLE_MARKER;
			overlay.stopDragMarker();
			invalidateOptionsMenu();
			((MapView) findViewById(R.id.mapview)).invalidate();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
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
		((MapView) findViewById(R.id.mapview)).invalidate();
	}

	private void onInstall() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		installedBefore = true;
		editor.putBoolean(INSTALL_STATE, installedBefore);
		editor.commit();
		// create DB
	}

	private void onInitialize() {
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		locator = new Locator();
		locator.updateActivity(this);
		locator.updateOldLocation();
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.default_marker);
		List<Overlay> mapOverlays = mapView.getOverlays();
		overlay = new CustomOverlay(drawable);
		overlay.updateActivity(this);
		mapOverlays.add(overlay);
		markerState = ENABLE_MARKER;
		connector = new Connector();
		showDialog(DOWNLOAD_DIALOG);

	}

	private void onRecreate() {
		locator.updateActivity(this);
		overlay.updateActivity(this);
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
			File worldToParse = connector.downloadXML(connector.WORLD, "world");
			openAsset();
			try {
				regions = getRegions(worldToParse.getPath());
				Message msg2 = mHandler.obtainMessage();
				msg2.arg1 = UPLOAD_COMPLETE;
				mHandler.sendMessage(msg2);
			} catch (FileNotFoundException e) {
				Message msg3 = mHandler.obtainMessage();
				msg3.arg1 = UPLOAD_NOTCOMPLETED;
				mHandler.sendMessage(msg3);
				e.printStackTrace();
			}

			
		}
	}

	// ===========================================================
	// Debug
	// ===========================================================

	private ArrayList<CustomOverlayItem> openAsset() {
		String file = "";
		InputStream stream = null;
		AssetManager assetManager = getAssets();
		Parser parser = new Parser();
		ArrayList<CustomOverlayItem> itemList = new ArrayList<CustomOverlayItem>(
				0);
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

	private ArrayList<Region> getRegions(String filename) throws FileNotFoundException {
		InputStream stream = new FileInputStream(filename);
		Parser parser = new Parser();
		File file = new File(filename);
		return parser.ParseRegions(filename, stream, this);
	}

	private void loadWorld() {

	}
}