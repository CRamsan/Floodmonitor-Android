package flood.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import flood.monitor.modules.Connector;
import flood.monitor.modules.Locator;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Parser;
import flood.monitor.modules.kmlparser.Region;
import flood.monitor.overlay.Marker;
import flood.monitor.overlay.MarkerOverlay;

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
	// PROCESS STATES
	private final static int PROCESS_RUNNING = 0;
	private final static int PROCESS_COMPLETE = 1;
	private final static int PROCESS_FAILED = 2;
	// REQUEST CODES
	private final static int PROCESS_REQUEST = 100;
	private final static int REQUEST_DOWNLOAD_EVENTS = 500;
	private final static int REQUEST_DOWNLOAD_REGIONS = 600;
	private final static int REQUEST_DOWNLOAD_MARKERS = 700;
	// DIALOGS ID
	private final static int EVENT_DOWNLOAD_DIALOG = 200;
	private final static int EVENT_SELECT_DIALOG = 300;
	private final static int REGION_DOWNLOAD_DIALOG = 350;
	private final static int MARKER_DOWNLOAD_DIALOG = 3750;
	// PREFERENCES
	private final static String PREFS_NAME = "MapViewPref";
	private final static String INSTALL_STATE = "Install_State";
	private final static String REGIONS_DATA = "Regions_Array";

	// ===========================================================
	// Fields
	// ===========================================================
	private Locator locator;
	private MarkerOverlay overlay;
	private static ArrayList<Event> events;
	private int eventIndex;

	private MapViewActivity activity;

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
		// The activity is launched or restarted after been killed.
		super.onCreate(savedInstanceState);
		// This is used to share the contect with other inner classes
		this.activity = this;

		setContentView(R.layout.map);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		locator = new Locator(this);
		locator.updateOldLocation();
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.default_marker);
		List<Overlay> mapOverlays = mapView.getOverlays();
		overlay = new MarkerOverlay(drawable);
		mapOverlays.add(overlay);
		markerState = ENABLE_MARKER;

		Button buttonUploadImage = (Button) findViewById(R.id.buttonLock);
		buttonUploadImage.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(EVENT_DOWNLOAD_DIALOG);
			}
		});

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
			startActivityForResult(intent, PROCESS_REQUEST);
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
		case EVENT_DOWNLOAD_DIALOG: {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Getting list of current events");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			return progressDialog;
		}
		case EVENT_SELECT_DIALOG: {
			CharSequence[] items = new String[events.size()];
			for (int i = 0; i < events.size(); i++) {
				items[i] = events.get(i).getName();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick an event");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					eventIndex = item;
					getActionBar()
							.setSubtitle(events.get(eventIndex).getName());
					dismissDialog(EVENT_SELECT_DIALOG);
					downloadRegionsDialog();
				}
			});
			builder.setCancelable(false);
			AlertDialog alert = builder.create();
			return alert;
		}
		case REGION_DOWNLOAD_DIALOG: {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Getting data of this event");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			return progressDialog;
		}
		case MARKER_DOWNLOAD_DIALOG: {
		}
		default: {
			return null;
		}
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case EVENT_DOWNLOAD_DIALOG: {

		}
		case EVENT_SELECT_DIALOG: {
		}
		case REGION_DOWNLOAD_DIALOG: {
		}
		case MARKER_DOWNLOAD_DIALOG: {
		}
		default: {
		}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PROCESS_REQUEST) {
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
	private void downloadEventDialog() {

		final Handler downloadEventDialoghandler = new Handler() {
			public void handleMessage(Message msg) {
				int state = msg.arg1;
				if (state == PROCESS_COMPLETE) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							dismissDialog(EVENT_DOWNLOAD_DIALOG);
							showDialog(EVENT_SELECT_DIALOG);
						}
					});

				} else if (state == PROCESS_FAILED) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							dismissDialog(EVENT_DOWNLOAD_DIALOG);
							Toast.makeText(activity,
									"Download of events failed", 500).show();
						}
					});
				}
			}
		};

		showDialog(EVENT_DOWNLOAD_DIALOG);
		ProgressThread progressThread = new ProgressThread(
				downloadEventDialoghandler, REQUEST_DOWNLOAD_EVENTS);
		progressThread.start();
	}

	private void downloadRegionsDialog() {
		final Handler downloadRegionsDialogHandler = new Handler() {
			public void handleMessage(Message msg) {
				int state = msg.arg1;
				if (state == PROCESS_COMPLETE) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
						}
					});
				} else if (state == PROCESS_RUNNING) {

				} else if (state == PROCESS_FAILED) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							dismissDialog(REGION_DOWNLOAD_DIALOG);
							Toast.makeText(activity,
									"Download of regions failed", 500).show();
						}
					});
				}
			}
		};

		showDialog(REGION_DOWNLOAD_DIALOG);
		ProgressThread progressThread = new ProgressThread(
				downloadRegionsDialogHandler, REQUEST_DOWNLOAD_REGIONS);
		progressThread.start();
	}

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
		downloadEventDialog();
	}

	private void onRecreate() {

	}

	private void downloadAllRegions(Event event) {
		for (int i = 0; i < event.getRegions().size(); i++) {

		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class ProgressThread extends Thread {
		private Handler mHandler;
		private int request;

		public ProgressThread(Handler h, int request) {
			this.mHandler = h;
			this.request = request;
		}

		public void run() {
			Message msg1 = mHandler.obtainMessage();
			msg1.arg1 = PROCESS_RUNNING;
			mHandler.sendMessage(msg1);

			switch (request) {
			case REQUEST_DOWNLOAD_EVENTS:
				File eventsFile = Connector.downloadEvents();
				try {
					events = getEvents(eventsFile.getPath());
					Message msg2 = mHandler.obtainMessage();
					msg2.arg1 = PROCESS_COMPLETE;
					mHandler.sendMessage(msg2);
				} catch (FileNotFoundException e) {
					Message msg3 = mHandler.obtainMessage();
					msg3.arg1 = PROCESS_FAILED;
					mHandler.sendMessage(msg3);
					e.printStackTrace();
				}
				break;
			case REQUEST_DOWNLOAD_REGIONS:
				File regionFiles[] = Connector.downloadRegions(events
						.get(eventIndex));
				for (int i = 0; i < regionFiles.length; i++) {
					try {
						overlay.addOverlay(getMarkers(regionFiles[i].getPath()));
					} catch (FileNotFoundException e) {

						e.printStackTrace();
					}
				}
				break;
			case REQUEST_DOWNLOAD_MARKERS:

				break;

			default:
				break;
			}

		}
	}

	// ===========================================================
	// Debug
	// ===========================================================

	/*
	 * private ArrayList<Marker> openAsset() { String file = ""; InputStream
	 * stream = null; AssetManager assetManager = getAssets(); Parser parser =
	 * new Parser(); ArrayList<Marker> itemList = new ArrayList<Marker>( 0); try
	 * { stream = assetManager.open("sample.kml"); itemList = parser.Parse(file,
	 * stream, this); } catch (IOException e) { // handle } finally { if (stream
	 * != null) { try { stream.close(); } catch (IOException e) { } } } return
	 * itemList; }
	 */

	private ArrayList<Marker> getMarkers(String filename)
			throws FileNotFoundException {
		InputStream stream = new FileInputStream(filename);
		Parser parser = new Parser();
		File file = new File(filename);
		return parser.ParseMarkers(filename, stream);
	}

	private ArrayList<Event> getEvents(String filename)
			throws FileNotFoundException {
		InputStream stream = new FileInputStream(filename);
		Parser parser = new Parser();
		File file = new File(filename);
		return parser.ParseEvents(filename, stream);
	}

	private void loadWorld() {

	}
}