package flood.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import flood.monitor.modules.Connector;
import flood.monitor.modules.Locator;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Parser;
import flood.monitor.overlay.EventsOverlay;
import flood.monitor.overlay.Marker;
import flood.monitor.overlay.MarkersOverlay;

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
	public final static int ENABLE_MARKER = 0;
	public final static int ENABLE_UPLOAD = 1;
	public final static int MAP_EVENT_LEVEL = 0;
	public final static int MAP_EVENT_REGION = 1;
	public final static int MAP_EVENT_MARKER = 2;
	// PROCESS STATES
	public final static int PROCESS_RUNNING = 0;
	public final static int PROCESS_COMPLETE = 1;
	public final static int PROCESS_FAILED = 2;
	// REQUEST CODES
	public final static int PROCESS_REQUEST = 100;
	public final static int REQUEST_DOWNLOAD_EVENTS = 500;
	public final static int REQUEST_DOWNLOAD_REGIONS = 600;
	public final static int REQUEST_DOWNLOAD_MARKERS = 700;
	// DIALOGS ID
	public final static int EVENT_DOWNLOAD_DIALOG = 200;
	public final static int EVENT_SELECT_DIALOG = 300;
	public final static int REGION_DOWNLOAD_DIALOG = 350;
	public final static int MARKER_DOWNLOAD_DIALOG = 3750;
	// PREFERENCES
	public final static String PREFS_NAME = "MapViewPref";
	public final static String INSTALL_STATE = "Install_State";
	public final static String MAP_STATE = "Map_State";
	public final static String REGIONS_DATA = "Regions_Array";
	public final static String MARKER_STATE = "markerState";
	public final static String SUBTITLE_TEXT = "subtitleText";

	// ===========================================================
	// Fields
	// ===========================================================
	private Locator locator;
	private static MarkersOverlay overlayMarker;
	private static EventsOverlay eventsMarker;

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

		locator = new Locator(this);
		locator.updateOldLocation();

		ImageButton buttonLock = (ImageButton) findViewById(R.id.buttonLock);
		buttonLock.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				locator.updateListening(activity);
				focus(locator.getBestLocation(), 14);
			}
		});

		ImageButton addMarker = (ImageButton) findViewById(R.id.buttonAddMarker);
		addMarker.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				markerState = ENABLE_UPLOAD;
				overlayMarker.initiateDragMarker(locator.getBestLocation());
				updateButton();
				((MapView) findViewById(R.id.mapview)).invalidate();
			}
		});

		ImageButton cancelMarker = (ImageButton) findViewById(R.id.buttoCancelMarker);
		cancelMarker.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				markerState = ENABLE_MARKER;
				overlayMarker.stopDragMarker();
				updateButton();
				((MapView) findViewById(R.id.mapview)).invalidate();

			}
		});

		ImageButton uploadMarker = (ImageButton) findViewById(R.id.buttonUploadMarker);
		uploadMarker.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MapViewActivity.this,
						UploadFormActivity.class);
				intent.putExtra("latitude", overlayMarker.getMarkerLocation()
						.getLatitude());
				intent.putExtra("longitude", overlayMarker.getMarkerLocation()
						.getLongitude());
				startActivityForResult(intent, PROCESS_REQUEST);
			}
		});

		if (savedInstanceState != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				getActionBar().setSubtitle(
						savedInstanceState.getCharSequence(SUBTITLE_TEXT));
			}
			markerState = savedInstanceState.getInt(MARKER_STATE);
			onRecreate();
		} else {
			markerState = ENABLE_MARKER;
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			installedBefore = settings.getBoolean(INSTALL_STATE, false);
			if (installedBefore) {
				onInstall();
			}
			onInitialize();
		}
		updateButton();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateButton();
		locator.startListening(this);
		// The activity has become visible (it is now "resumed").
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to
		// be"paused")
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
		overlayMarker.stopDragMarker();

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
		case R.id.menuItemSettings:// Settings
			intent = new Intent(MapViewActivity.this, SettingsActivity.class);
			startActivity(intent);
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
		case android.R.id.home:
			downloadEventDialog();
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
			/*
			 * CharSequence[] items = new String[events.size()]; for (int i = 0;
			 * i < events.size(); i++) { items[i] = events.get(i).getName(); }
			 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
			 * builder.setTitle("Pick an event"); builder.setItems(items, new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int item) {
			 * eventsMarker.setEventIndex(item); if (Build.VERSION.SDK_INT >=
			 * Build.VERSION_CODES.HONEYCOMB) { getActionBar().setSubtitle(
			 * events.get(eventsMarker.getEventIndex()).getName());
			 * 
			 * } else {
			 * 
			 * } dismissDialog(EVENT_SELECT_DIALOG); downloadRegionsDialog(); }
			 * }); builder.setCancelable(false); AlertDialog alert =
			 * builder.create(); return alert;
			 */
			return null;
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
			if (eventsMarker == null) {
				dialog.setCancelable(false);
			} else {
				dialog.setCancelable(true);
			}
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
			overlayMarker.stopDragMarker();
			updateButton();
			((MapView) findViewById(R.id.mapview)).invalidate();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			savedInstanceState.putCharSequence(SUBTITLE_TEXT, getActionBar()
					.getSubtitle());
		}
		savedInstanceState.putInt(MARKER_STATE, markerState);

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

	private void addOverlay(Overlay overlay) {
		MapView mapView = (MapView) findViewById(R.id.mapview);
		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.add(overlay);
	}

	private void removeOverlay() {
		MapView mapView = (MapView) findViewById(R.id.mapview);
		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.remove(overlayMarker);
	}

	private void downloadEventDialog() {
		new DownloadEventsTask().execute();
	}

	private void downloadRegionsDialog() {
		final Handler downloadRegionsDialogHandler = new Handler() {
			public void handleMessage(Message msg) {
				int state = msg.arg1;
				if (state == PROCESS_COMPLETE) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							dismissDialog(REGION_DOWNLOAD_DIALOG);
							focus(locator.getBestLocation(), 11);
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

		ProgressThread progressThread = new ProgressThread(
				downloadRegionsDialogHandler, REQUEST_DOWNLOAD_REGIONS);
		progressThread.start();
	}

	public void loadEvents() {

	}

	public void updateBestLocation() {
		overlayMarker.updateBestLocation(locator.getBestLocation());
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
		mapView.setSatellite(false);
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.marker_blue);
		List<Overlay> mapOverlays = mapView.getOverlays();
		overlayMarker = new MarkersOverlay(drawable);
		mapOverlays.add(overlayMarker);
		overlayMarker.updateActivity(this);
		downloadEventDialog();
	}

	private void onRecreate() {
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.add(overlayMarker);
		overlayMarker.updateActivity(this);
		if (markerState == ENABLE_UPLOAD) {
			overlayMarker.initiateDragMarker(locator.getBestLocation());
			((MapView) findViewById(R.id.mapview)).invalidate();
		}
	}

	private void downloadAllRegions(Event event) {
		for (int i = 0; i < event.getRegions().size(); i++) {

		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class DownloadEventsTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showDialog(EVENT_DOWNLOAD_DIALOG);
		}

		@Override
		protected Void doInBackground(Void... params) {
			File eventsFile = Connector.downloadEvents();
			try {
				ArrayList<Event> events = getEvents(eventsFile.getPath());
				eventsMarker = new EventsOverlay(events);
				addOverlay(eventsMarker);
				eventsMarker.updateActivity(activity);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			dismissDialog(EVENT_DOWNLOAD_DIALOG);
			loadEvents();
		}
	}

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
					ArrayList<Event> events = getEvents(eventsFile.getPath());
					eventsMarker = new EventsOverlay(events);
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
				File regionFiles[] = Connector.downloadRegions(eventsMarker
						.getEvents().get(eventsMarker.getEventIndex()));
				for (int i = 0; i < regionFiles.length; i++) {
					try {
						overlayMarker.addOverlay(getMarkers(regionFiles[i]
								.getPath()));
					} catch (FileNotFoundException e) {

						e.printStackTrace();
					}
				}
				Message msg2 = mHandler.obtainMessage();
				msg2.arg1 = PROCESS_COMPLETE;
				mHandler.sendMessage(msg2);
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

	private void focus(Location locationToZoom, int level) {
		MapView map = ((MapView) findViewById(R.id.mapview));
		MapController mc = map.getController();
		mc.animateTo(new GeoPoint(
				(int) (locationToZoom.getLatitude() * 1000000),
				(int) (locationToZoom.getLongitude() * 1000000)));
		mc.setZoom(level);
		map.invalidate();
	}

	private void updateButton() {
		switch (markerState) {
		case ENABLE_MARKER:
			findViewById(R.id.buttonUploadMarker).setVisibility(View.GONE);
			findViewById(R.id.buttoCancelMarker).setVisibility(View.GONE);
			findViewById(R.id.buttonAddMarker).setVisibility(View.VISIBLE);
			break;
		case ENABLE_UPLOAD:
			findViewById(R.id.buttonUploadMarker).setVisibility(View.VISIBLE);
			findViewById(R.id.buttoCancelMarker).setVisibility(View.VISIBLE);
			findViewById(R.id.buttonAddMarker).setVisibility(View.GONE);
			break;
		}
	}

	public static class ActivityUtil {

		public static void updateOptionsMenu(Activity activity) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				activity.invalidateOptionsMenu();
			} else {

			}
		}
	}
}