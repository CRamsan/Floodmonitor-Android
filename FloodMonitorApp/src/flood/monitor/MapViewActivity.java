package flood.monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import flood.monitor.modules.Connector;
import flood.monitor.modules.Locator;
import flood.monitor.modules.kmlparser.Boundary;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Marker;
import flood.monitor.modules.kmlparser.ObjectDataSource;
import flood.monitor.modules.kmlparser.Region;
import flood.monitor.overlay.IOverlay;
import flood.monitor.overlay.LimitedMapView;
import flood.monitor.overlay.MarkersOverlay;
import flood.monitor.overlay.RegionsOverlay;

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
	// BUTTON STATES
	public final static int ADD_BUTTON_ENABLED = 1;
	public final static int UPLOAD_BUTTON_ENABLED = 2;
	// MAP LEVEL
	public final static int MAP_LEVEL_REGION = 1;
	public final static int MAP_LEVEL_MARKER = 2;
	// MARKER DIALOG MODES
	public final static int MARKER_LOCATION = 1;
	public final static int MARKER_UPLOAD = 2;
	// INTENT CODES
	public final static int UPLOAD_INTENT = 100;
	public final static int DIALOG_INTENT = 150;
	// DIALOGS ID
	public final static int FIRST_RUN_DIALOG = 500;
	public final static int EVENT_DOWNLOAD_DIALOG = 100;
	public final static int REGION_DOWNLOAD_DIALOG = 300;
	public final static int MARKER_DOWNLOAD_DIALOG = 400;
	// START MODE
	public final static int START_INSTALL = 100;
	public final static int START_CREATE = 300;
	public final static int START_RECREATE = 400;

	// PREFERENCES
	public final static String INSTALL_STATE = "Install_State";
	public final static String MAP_STATE = "MapState";
	public final static String BUTTON_STATE = "buttonState";
	public final static String OVERLAY_STATE = "overlayLoaded";

	// ===========================================================
	// Fields
	// ===========================================================
	private static LocationOverlay locationOverlay;
	private static LimitedMapView limitedMapView;
	private static MarkersOverlay markersOverlay;
	private static RegionsOverlay regionsOverlay;
	private static IOverlay selectedOverlay;
	private static MapViewActivity activity;
	private static AsyncTask runningTask;
	private static Geocoder geocoder;

	private static Dialog downloadRegionsDialog;
	private static Dialog downloadEventsDialog;
	private static Dialog downloadMarkersDialog;

	private ObjectDataSource data;

	private Locator locator;

	public boolean init;
	public boolean install;
	private int buttonState;
	private int markersPerPage;
	private boolean overlayLoaded;

	// ===========================================================
	// Methods from Activity
	// ===========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// The activity is launched or restarted after been killed.
		super.onCreate(savedInstanceState);

		/*
		 * This methods are called every time the activity is created. This
		 * includes when the activity is called by the first time or after each
		 * configuration change(screen rotation).
		 */

		setContentView(R.layout.map);
		activity = this;
		limitedMapView = (LimitedMapView) findViewById(R.id.mapview);
		limitedMapView.displayZoomControls(true);

		locator = new Locator(this);
		locator.updateLocationFromLastKnownLocation();

		geocoder = new Geocoder(this);

		findViewById(R.id.buttonLock).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						lockGPS();
					}
				});

		findViewById(R.id.buttonAddMarker).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						addMarkerToMap();
					}
				});

		findViewById(R.id.buttoCancelMarker).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						removeMarkerFromMap();
					}
				});

		findViewById(R.id.buttonUploadMarker).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						uploadMarker();
					}
				});

		findViewById(R.id.buttonInfoMarker).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						locationOverlay.showInfoDialog();
					}
				});

		findViewById(R.id.buttonLayerUp).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						layerUp();
					}
				});

		findViewById(R.id.buttonNextPage).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						nextPage();
					}
				});

		findViewById(R.id.buttonPrevPage).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						previousPage();
					}
				});

		findViewById(R.id.buttonZoomIn).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						limitedMapView.getController().zoomIn();
					}
				});

		findViewById(R.id.buttonZoomOut).setOnClickListener(
				new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						limitedMapView.getController().zoomOut();
					}
				});

		/*
		 * We will check for any savedInstance, with this we will know if the
		 * activity is created for the first time(requires install of database),
		 * if the activity is been created or if it is been
		 * recreated(configuration change).
		 */

		install = false;
		if (savedInstanceState != null) {
			buttonState = savedInstanceState.getInt(BUTTON_STATE);
			overlayLoaded = savedInstanceState.getBoolean(OVERLAY_STATE);
			setMapLevel(savedInstanceState.getInt(MAP_STATE));
			init = true;

		} else {
			if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
					INSTALL_STATE, false)) {
				onInstall();
			}

			buttonState = ADD_BUTTON_ENABLED;
			setMapLevel(MAP_LEVEL_REGION);
			overlayLoaded = false;
			locationOverlay = new LocationOverlay(this.getResources()
					.getDrawable(R.drawable.marker_white));
			init = false;
		}
		if (overlayLoaded) {
			addOverlay((Overlay) selectedOverlay);
			selectedOverlay.updateActivity(this);
		}

		if (runningTask != null) {
			if (runningTask instanceof DownloadRegionsTask) {
				((DownloadRegionsTask) runningTask).setActivity(this);
			} else if (runningTask instanceof DownloadAndShowEventsTask) {
				((DownloadAndShowEventsTask) runningTask).setActivity(this);
			} else if (runningTask instanceof DownloadMarkersTask) {
				((DownloadMarkersTask) runningTask).setActivity(this);
			}
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		markersPerPage = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(this).getString("markersPerPage",
						"-1"));
		switch (getMapLevel()) {
		case MapViewActivity.MAP_LEVEL_REGION:
			selectedOverlay = regionsOverlay;
			break;
		case MapViewActivity.MAP_LEVEL_MARKER:
			selectedOverlay = markersOverlay;
			if (markersPerPage != -1) {
				markersOverlay.enablePageing(markersPerPage);
			} else {
				markersOverlay.disablePageing();
			}

			break;
		}
		bindOverlays();

		// The activity is about to become visible.
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"pref_GPSEnabled", false)) {
			locator.startListening(this);
		}

		limitedMapView.setSatellite(PreferenceManager
				.getDefaultSharedPreferences(this).getBoolean(
						"pref_UseSatellite", false));
		limitedMapView.invalidate();

		updateButton();

		if (!init) {
			downloadRegionsDialog();
		}
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
		// The activity is no longer visible (it is now "stopped")
		locator.stopListening(this);
		switch (getMapLevel()) {
		case MapViewActivity.MAP_LEVEL_REGION:
			break;
		case MapViewActivity.MAP_LEVEL_MARKER:
			markersOverlay.disablePageing();
			break;
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// The activity is about to be destroyed.
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (isProcessRunning()) {
			if (runningTask instanceof DownloadRegionsTask) {
				((DownloadRegionsTask) runningTask).removeActivity();
			} else if (runningTask instanceof DownloadAndShowEventsTask) {
				((DownloadAndShowEventsTask) runningTask).removeActivity();
			} else if (runningTask instanceof DownloadMarkersTask) {
				((DownloadMarkersTask) runningTask).removeActivity();
			}
		}
		switch (getMapLevel()) {
		case MapViewActivity.MAP_LEVEL_REGION:
			regionsOverlay.cancelDialog();
			break;
		case MapViewActivity.MAP_LEVEL_MARKER:
			break;
		}

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
		case R.id.menuItemSearch:// Search
			onSearchRequested();
			return true;
		case R.id.menuItemSettings:// Settings
			intent = new Intent(MapViewActivity.this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.menuItemAbout: // ABout
			intent = new Intent(MapViewActivity.this, AboutActivity.class);
			startActivity(intent);
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
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage(getResources().getString(
					R.string.text_GettingListofEvents));
			downloadEventsDialog = progressDialog;
			return progressDialog;
		}
		case REGION_DOWNLOAD_DIALOG: {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage(getResources().getString(
					R.string.text_GettingListofRegions));
			downloadRegionsDialog = progressDialog;
			return progressDialog;
		}
		case MARKER_DOWNLOAD_DIALOG: {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage(getResources().getString(
					R.string.text_GettingDataofEvent));
			downloadMarkersDialog = progressDialog;
			return progressDialog;
		}
		case FIRST_RUN_DIALOG: {
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.about);
			dialog.setCancelable(true);
			return dialog;
		}
		default: {
		}
		}

		return null;

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case EVENT_DOWNLOAD_DIALOG: {

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
	public boolean onSearchRequested() {
		ActivityUtil.updateOptionsMenu(this);
		return super.onSearchRequested();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case UPLOAD_INTENT:
			if (resultCode == RESULT_OK) {
				buttonState = ADD_BUTTON_ENABLED;
				locationOverlay.stopDragMarker();
				updateButton();
				limitedMapView.invalidate();
			}
			break;
		case DIALOG_INTENT:
			if (resultCode == RESULT_OK) {
				buttonState = ADD_BUTTON_ENABLED;
				locationOverlay.stopDragMarker();
				updateButton();
				limitedMapView.invalidate();
			}
			break;
		default:
			break;
		}
	}

	@Override
	@TargetApi(11)
	@SuppressLint("NewApi")
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putInt(BUTTON_STATE, buttonState);
		savedInstanceState.putInt(MAP_STATE, getMapLevel());
		savedInstanceState.putBoolean(OVERLAY_STATE, overlayLoaded);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from Parent
	// ===========================================================

	// ===========================================================
	// Methods from Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void loadOverlayUp() {
		removeOverlay(markersOverlay);
		addOverlay(regionsOverlay);
		selectedOverlay = regionsOverlay;
		this.setMapLevel(MAP_LEVEL_REGION);
	}

	public void loadOverlayDown(int regionId) {
	}

	public void loadMarkerOverlay(Overlay remove) {
		removeOverlay(remove);
		addOverlay(markersOverlay);
	}

	private void addOverlay(Overlay overlay) {
		List<Overlay> mapOverlays = limitedMapView.getOverlays();
		if (overlay instanceof RegionsOverlay) {
			((RegionsOverlay)overlay).updateActivity(this);
		} else if (overlay instanceof MarkersOverlay) {
			((MarkersOverlay)overlay).updateActivity(this);
		} 
		int index = mapOverlays.size() - 1;
		if (index > 0)
			mapOverlays.add(index, overlay);
		else
			mapOverlays.add(0, overlay);
	}

	private void removeOverlay(Overlay overlay) {
		List<Overlay> mapOverlays = limitedMapView.getOverlays();
		mapOverlays.remove(overlay);
	}

	public void downloadRegionsDialog() {
		if (!isProcessRunning()) {
			runningTask = new DownloadRegionsTask(this).execute();
		}

	}

	public void downloadEventsAndShowDialog(int regionId) {
		if (!isProcessRunning()) {
			runningTask = new DownloadAndShowEventsTask(this).execute(regionId);
		}
	}

	public void downloadMarkersDialog(Region region) {
		if (!isProcessRunning()) {
			runningTask = new DownloadMarkersTask(this).execute(region);
		}
	}

	public void updateBestLocation() {
		locationOverlay.updateBestLocation(locator.getBestLocation());
		limitedMapView.invalidate();
	}

	public void updateSearchResult(Address address) {
		locationOverlay.updateSearchResult(address);
		limitedMapView.invalidate();
	}

	private void onInstall() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(INSTALL_STATE, true);
		editor.commit();

		SharedPreferences updateInterval = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor updateIntervalEditor = updateInterval.edit();
		updateIntervalEditor.putInt("updateInterval", 30000);
		updateIntervalEditor.commit();
		install = true;
	}

	private void lockGPS() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean useGPS = sharedPrefs.getBoolean("pref_GPSEnabled", false);
		if (useGPS) {
			locator.updateListening(this);
		}
		switch (getMapLevel()) {
		case MapViewActivity.MAP_LEVEL_REGION:
			focus(locator.getBestLocation(), 10);
			break;
		case MapViewActivity.MAP_LEVEL_MARKER:
			focus(locator.getBestLocation(), 15);
			break;
		}
	}

	private boolean isProcessRunning() {
		if (runningTask == null) {
			return false;
		} else {
			if (!(runningTask.getStatus() == AsyncTask.Status.RUNNING)
					|| runningTask.getStatus() == AsyncTask.Status.FINISHED) {
				return false;
			} else {
				return true;
			}
		}
	}

	private void stopRunningProcess() {
		if (isProcessRunning()) {
			this.runningTask.cancel(true);
		}
	}

	protected void previousPage() {
		markersOverlay.previousPage();
		((TextView) findViewById(R.id.textViewPageNumber)).setText("Page"
				+ ": " + (markersOverlay.getPage() + 1) + "/"
				+ markersOverlay.getMaxPage());
		((MapView) findViewById(R.id.mapview)).invalidate();
	}

	protected void nextPage() {
		markersOverlay.nextPage();
		((TextView) findViewById(R.id.textViewPageNumber)).setText("Page"
				+ ": " + (markersOverlay.getPage() + 1) + "/"
				+ markersOverlay.getMaxPage());
		((MapView) findViewById(R.id.mapview)).invalidate();
	}

	protected void layerUp() {
		loadOverlayUp();
		updateButton();
		limitedMapView.invalidate();
		focus(limitedMapView.getMapCenter(), 12);

	}

	protected void uploadMarker() {
		Intent intent = new Intent(MapViewActivity.this,
				UploadFormActivity.class);
		intent.putExtra("latitude", locationOverlay.getMarkerLocation()
				.getLatitude());
		intent.putExtra("longitude", locationOverlay.getMarkerLocation()
				.getLongitude());
		startActivityForResult(intent, UPLOAD_INTENT);
	}

	protected void removeMarkerFromMap() {
		buttonState = ADD_BUTTON_ENABLED;
		locationOverlay.stopDragMarker();
		updateButton();
		((MapView) findViewById(R.id.mapview)).invalidate();
	}

	protected void addMarkerToMap() {
		buttonState = UPLOAD_BUTTON_ENABLED;
		locationOverlay.initiateDragMarker(limitedMapView.getMapCenter());
		updateButton();
		((MapView) findViewById(R.id.mapview)).invalidate();

	}

	private void getResults(Address address) {
		updateSearchResult(address);
		buttonState = UPLOAD_BUTTON_ENABLED;
		switch (getMapLevel()) {
		case MapViewActivity.MAP_LEVEL_REGION:
			focus(address.getLatitude(), address.getLongitude(), 9);
			break;
		case MapViewActivity.MAP_LEVEL_MARKER:
			focus(address.getLatitude(), address.getLongitude(), 15);
			break;
		}
		activity.updateButton();
		limitedMapView.invalidate();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class DownloadRegionsTask extends AsyncTask<Void, Void, Void> {
		protected boolean taskCompleted = false;
		protected MapViewActivity activity = null;

		public DownloadRegionsTask(MapViewActivity activity) {
			setActivity(activity);
		}

		@Override
		protected void onPreExecute() {
			showDialog(REGION_DOWNLOAD_DIALOG);
			data = new ObjectDataSource(activity);
			data.open();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				ArrayList<Region> regions = Connector.downloadGeoRegions();
				data.applyRegionDifferences(data.getAllRegions(), regions);
				Drawable defaultDrawable = activity.getResources().getDrawable(
						R.drawable.marker_green);
				regionsOverlay = new RegionsOverlay(defaultDrawable, regions);
				regionsOverlay.updateActivity(activity);
				selectedOverlay = regionsOverlay;
				addOverlay((RegionsOverlay) selectedOverlay);
				overlayLoaded = true;
				taskCompleted = true;
			} else {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity,
								"We could not contact the server.",
								Toast.LENGTH_LONG).show();
					}
				});
				ArrayList<Region> regions = data.getAllRegions();
				Drawable defaultDrawable = activity.getResources().getDrawable(
						R.drawable.marker_green);
				regionsOverlay = new RegionsOverlay(defaultDrawable, regions);
				regionsOverlay.updateActivity(activity);
				selectedOverlay = regionsOverlay;
				addOverlay((RegionsOverlay) selectedOverlay);
				overlayLoaded = true;
				taskCompleted = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			if (taskCompleted) {

				setMapLevel(MAP_LEVEL_REGION);
				limitedMapView.invalidate();
			}
			data.close();
			activity.init = true;
			dismissDialog(REGION_DOWNLOAD_DIALOG);
			runningTask = null;
			if (install) {
				showDialog(FIRST_RUN_DIALOG);
			}
		}

		public void setActivity(MapViewActivity activity) {
			this.activity = activity;
			Log.i("DownloadRegionsTask",
					"Activity set to " + activity.getTaskId());
		}

		public void removeActivity() {
			this.activity = null;
			Log.i("DownloadRegionsTask", "Activity set to null");
		}
	}

	private class DownloadAndShowEventsTask extends
			AsyncTask<Integer, Void, Void> {
		protected boolean taskCompleted = false;
		int regionId;
		protected MapViewActivity activity = null;

		public DownloadAndShowEventsTask(MapViewActivity activity) {
			setActivity(activity);
		}

		@Override
		protected void onPreExecute() {
			showDialog(EVENT_DOWNLOAD_DIALOG);
			data = new ObjectDataSource(activity);
			data.open();
			Log.i("MapViewActivity", "DownloadAndShowEventsTask started");
		}

		@Override
		protected Void doInBackground(Integer... params) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			regionId = params[0];
			if (networkInfo != null && networkInfo.isConnected()) {
				ArrayList<Event> events = Connector.downloadEvents(regionId);
				data.applyEventDifferences(data.getAllEvents(regionId), events);
				((RegionsOverlay) selectedOverlay).setEvents(regionId, events);
				taskCompleted = true;
			} else {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity,
								"We could not contact the server.",
								Toast.LENGTH_LONG).show();
					}
				});
				ArrayList<Event> events = data.getAllEvents(regionId);
				((RegionsOverlay) selectedOverlay).setEvents(regionId, events);
				taskCompleted = true;
			}
			Log.i("MapViewActivity", "DownloadAndShowEventsTask job finished");
			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			if (taskCompleted) {

				setMapLevel(MAP_LEVEL_REGION);
				limitedMapView.invalidate();
				selectedOverlay.updateActivity(activity);
				((RegionsOverlay) selectedOverlay).showMarkerDialog(regionId);

			}
			dismissDialog(EVENT_DOWNLOAD_DIALOG);
			data.close();
			Log.i("MapViewActivity", "DownloadAndShowEventsTask ended");
			runningTask = null;
		}

		public void setActivity(MapViewActivity newActivity) {
			this.activity = newActivity;
			Log.i("DownloadRegionsTask",
					"Activity set to " + activity.getTaskId());
		}

		public void removeActivity() {
			this.activity = null;
			Log.i("DownloadRegionsTask", "Activity set to null");
		}

	}

	private class DownloadMarkersTask extends AsyncTask<Region, Void, Void> {

		protected boolean taskCompleted = false;
		private Region selectedRegion;
		private Event selectedEvent;
		private MapViewActivity activity;
		private ArrayList<Marker> allMarkers;

		public DownloadMarkersTask(MapViewActivity activity) {
			setActivity(activity);
		}

		@Override
		protected void onPreExecute() {
			showDialog(MARKER_DOWNLOAD_DIALOG);
			data = new ObjectDataSource(activity);
			data.open();
			Log.i("MapViewActivity", "DownloadMarkersTask started");
		}

		@Override
		protected Void doInBackground(Region... params) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			selectedRegion = params[0];
			selectedEvent = selectedRegion.getEvents().get(
					selectedRegion.getSelectedEvent());
			allMarkers = new ArrayList<Marker>(0);
			if (networkInfo != null && networkInfo.isConnected()) {
				for (Boundary boundary : selectedRegion.getBoundaries()) {
					ArrayList<Marker> markers = Connector.downloadMarkers(
							boundary.getId(), selectedEvent.getEventId(),
							selectedRegion.getRegionId());
					data.applyMarkerDifferences(
							data.getAllMarkers(boundary.getId(),
									selectedEvent.getEventId()), markers);
					allMarkers.addAll(markers);
				}
				Collections.sort(allMarkers);

			} else {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity,
								"We could not contact the server.",
								Toast.LENGTH_LONG).show();
					}
				});
				for (Boundary boundary : selectedRegion.getBoundaries()) {
					ArrayList<Marker> markers = data.getAllMarkers(
							boundary.getId(), selectedEvent.getEventId());
					allMarkers.addAll(markers);
				}
			}
			taskCompleted = true;
			Log.i("MapViewActivity", "DownloadMarkersTask job finished");
			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			if (taskCompleted) {
				Drawable defaultDrawable = activity.getResources().getDrawable(
						R.drawable.marker_green);
				markersOverlay = new MarkersOverlay(defaultDrawable);
				markersOverlay.updateActivity(activity);
				markersOverlay.setOverlay(allMarkers);

				if (markersPerPage != -1) {
					markersOverlay.enablePageing(markersPerPage);
				}

				removeOverlay((Overlay) selectedOverlay);
				selectedOverlay = markersOverlay;
				addOverlay((MarkersOverlay) selectedOverlay);
				overlayLoaded = true;
				setMapLevel(MAP_LEVEL_MARKER);
				limitedMapView.invalidate();
				focus(selectedRegion.getCenter(), 15);
				activity.updateButton();
			}
			data.close();
			// dismissDialog(MARKER_DOWNLOAD_DIALOG);
			downloadMarkersDialog.cancel();
			Log.i("MapViewActivity", "DownloadMarkersTask ended");
			runningTask = null;
		}

		public void setActivity(MapViewActivity activity) {
			this.activity = activity;
			Log.i("DownloadRegionsTask",
					"Activity set to " + activity.getTaskId());
		}

		public void removeActivity() {
			this.activity = null;
			Log.i("DownloadRegionsTask", "Activity set to null");
		}
	}

	private void setMapLevel(int mapLevel) {
		limitedMapView.setMapLevel(mapLevel);
	}

	private int getMapLevel() {
		return limitedMapView.getMapLevel();
	}

	private void focus(Location locationToZoom, int level) {
		MapController mc = limitedMapView.getController();
		mc.animateTo(new GeoPoint(
				(int) (locationToZoom.getLatitude() * 1000000),
				(int) (locationToZoom.getLongitude() * 1000000)));
		mc.setZoom(level);
		limitedMapView.invalidate();
	}

	private void focus(GeoPoint geopointToZoom, int level) {
		MapController mc = limitedMapView.getController();
		mc.animateTo(geopointToZoom);
		mc.setZoom(level);
		limitedMapView.invalidate();
	}

	private void focus(double lat, double lon, int level) {
		MapController mc = limitedMapView.getController();
		mc.animateTo(new GeoPoint((int) (lat * 1000000), (int) (lon * 1000000)));
		mc.setZoom(level);
		limitedMapView.invalidate();
	}

	private void bindOverlays() {
		List<Overlay> overlays = limitedMapView.getOverlays();
		if (!overlays.contains(locationOverlay)) {
			overlays.add(locationOverlay);
		}
		locationOverlay.updateActivity(this);
	}

	private void updateButton() {
		switch (getMapLevel()) {
		case MAP_LEVEL_MARKER:
			switch (buttonState) {
			case ADD_BUTTON_ENABLED:
				findViewById(R.id.buttonUploadMarker).setVisibility(View.GONE);
				findViewById(R.id.buttoCancelMarker).setVisibility(View.GONE);
				findViewById(R.id.buttonAddMarker).setVisibility(View.VISIBLE);
				findViewById(R.id.buttonInfoMarker).setVisibility(View.GONE);
				break;
			case UPLOAD_BUTTON_ENABLED:
				findViewById(R.id.buttonUploadMarker).setVisibility(
						View.VISIBLE);
				findViewById(R.id.buttoCancelMarker)
						.setVisibility(View.VISIBLE);
				findViewById(R.id.buttonAddMarker).setVisibility(View.GONE);
				findViewById(R.id.buttonInfoMarker).setVisibility(View.VISIBLE);
				break;

			}

			if (markersPerPage != -1) {
				findViewById(R.id.textViewPageNumber).setVisibility(
						View.VISIBLE);
				findViewById(R.id.buttonNextPage).setVisibility(View.VISIBLE);
				findViewById(R.id.buttonPrevPage).setVisibility(View.VISIBLE);
				boolean useSatellite = PreferenceManager
						.getDefaultSharedPreferences(this).getBoolean(
								"pref_UseSatellite", false);
				TextView pageNumber = ((TextView) findViewById(R.id.textViewPageNumber));
				if (useSatellite) {
					pageNumber.setTextColor(Color.WHITE);
				} else {
					pageNumber.setTextColor(Color.BLACK);
				}
				pageNumber.setText("Page" + ": "
						+ (markersOverlay.getPage() + 1) + "/"
						+ markersOverlay.getMaxPage());

			} else {
				findViewById(R.id.textViewPageNumber).setVisibility(View.GONE);
				findViewById(R.id.buttonNextPage).setVisibility(View.GONE);
				findViewById(R.id.buttonPrevPage).setVisibility(View.GONE);
			}
			findViewById(R.id.buttonLayerUp).setVisibility(View.VISIBLE);
			break;
		case MAP_LEVEL_REGION:
			switch (buttonState) {
			case ADD_BUTTON_ENABLED:
				findViewById(R.id.buttoCancelMarker).setVisibility(View.GONE);
				findViewById(R.id.buttonAddMarker).setVisibility(View.VISIBLE);
				findViewById(R.id.buttonInfoMarker).setVisibility(View.GONE);
				break;
			case UPLOAD_BUTTON_ENABLED:
				findViewById(R.id.buttoCancelMarker)
						.setVisibility(View.VISIBLE);
				findViewById(R.id.buttonAddMarker).setVisibility(View.GONE);
				findViewById(R.id.buttonInfoMarker).setVisibility(View.VISIBLE);
				break;
			}
			findViewById(R.id.buttonUploadMarker).setVisibility(View.GONE);
			findViewById(R.id.buttonLayerUp).setVisibility(View.GONE);
			findViewById(R.id.buttonNextPage).setVisibility(View.GONE);
			findViewById(R.id.buttonPrevPage).setVisibility(View.GONE);
			findViewById(R.id.textViewPageNumber).setVisibility(View.GONE);
			break;
		default:
			break;
		}
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"pref_UseZoomControl", false)) {
			findViewById(R.id.buttonZoomIn).setVisibility(View.VISIBLE);
			findViewById(R.id.buttonZoomOut).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.buttonZoomIn).setVisibility(View.GONE);
			findViewById(R.id.buttonZoomOut).setVisibility(View.GONE);
		}
	}

	public static class ActivityUtil {

		@SuppressLint("NewApi")
		public static void updateOptionsMenu(Activity activity) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				activity.invalidateOptionsMenu();
			} else {

			}
		}
	}

	public class LocationOverlay extends ItemizedOverlay<OverlayItem> implements
			IOverlay {
		private OverlayItem currentLocationMarker;
		private OverlayItem draggerMarker;
		private ArrayList<OverlayItem> mOverlays;
		private Drawable uploadDrawable;
		private boolean isMarking;
		private boolean isTouching;
		private ImageView dragImage = null;
		private int xDragImageOffset = 0;
		private int yDragImageOffset = 0;
		private int xDragTouchOffset = 0;
		private int yDragTouchOffset = 0;

		public LocationOverlay(Drawable defaultMarker) {
			super(defaultMarker);
			mOverlays = new ArrayList<OverlayItem>(0);
			populate();
		}

		public void updateBestLocation(Location location) {
			mOverlays.remove(currentLocationMarker);
			currentLocationMarker = new OverlayItem(new GeoPoint(
					(int) (location.getLatitude() * 1000000),
					(int) (location.getLongitude() * 1000000)), "", "");
			Drawable icon = activity.getResources().getDrawable(
					R.drawable.location);
			icon.setBounds(-icon.getIntrinsicWidth() / 2,
					-icon.getIntrinsicHeight(), icon.getIntrinsicWidth() / 2, 0);
			currentLocationMarker.setMarker(icon);
			mOverlays.add(currentLocationMarker);
			populate();
		}

		public void updateSearchResult(Address address) {
			if (isMarking) {
				mOverlays.remove(draggerMarker);
				draggerMarker = new OverlayItem(new GeoPoint(
						(int) (address.getLatitude() * 1000000),
						(int) (address.getLongitude() * 1000000)), "", "");
				Drawable icon = activity.getResources().getDrawable(
						R.drawable.marker_white);
				icon.setBounds(-icon.getIntrinsicWidth() / 2,
						-icon.getIntrinsicHeight(),
						icon.getIntrinsicWidth() / 2, 0);
				draggerMarker.setMarker(icon);
				mOverlays.add(draggerMarker);
				populate();
			} else {
				initiateDragMarker(new GeoPoint(
						(int) (address.getLatitude() * 1000000),
						(int) (address.getLongitude() * 1000000)));
			}
		}

		public void initiateDragMarker(GeoPoint geoPoint) {
			uploadDrawable = activity.getResources().getDrawable(
					R.drawable.marker_white);
			draggerMarker = new OverlayItem(geoPoint, "", "");
			uploadDrawable.setBounds(-uploadDrawable.getIntrinsicWidth() / 2,
					-uploadDrawable.getIntrinsicHeight(),
					uploadDrawable.getIntrinsicWidth() / 2, 0);
			draggerMarker.setMarker(uploadDrawable);
			mOverlays.add(draggerMarker);
			isMarking = true;
			populate();
		}

		public Location getMarkerLocation() {
			Location temp = new Location("Picture Marker");
			temp.setLatitude(draggerMarker.getPoint().getLatitudeE6() / 1000000f);
			temp.setLongitude(draggerMarker.getPoint().getLongitudeE6() / 1000000f);
			return temp;
		}

		public void stopDragMarker() {
			isMarking = false;
			mOverlays.remove(draggerMarker);
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			return mOverlays.size();
		}

		@Override
		protected boolean onTap(int index) {
			showMarkerDialog(index);
			return true;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			if (isMarking) {
				final int action = event.getAction();
				final int x = (int) event.getX();
				final int y = (int) event.getY();

				if (action == MotionEvent.ACTION_DOWN) {
					Point p = new Point(0, 0);

					limitedMapView.getProjection().toPixels(
							draggerMarker.getPoint(), p);
					if (hitTest(draggerMarker, uploadDrawable, x - p.x, y - p.y)) {
						isTouching = true;
						mOverlays.remove(draggerMarker);
						populate();

						xDragTouchOffset = 0;
						yDragTouchOffset = 0;

						setDragImagePosition(p.x, p.y);
						dragImage.setVisibility(View.VISIBLE);

						xDragTouchOffset = x - p.x;
						yDragTouchOffset = y - p.y;
						return super.onTouchEvent(event, mapView);
					} else {
						return super.onTouchEvent(event, mapView);
					}
				} else if (action == MotionEvent.ACTION_MOVE
						&& draggerMarker != null && isTouching) {
					setDragImagePosition(x, y);
					return true;
				} else if (action == MotionEvent.ACTION_UP
						&& draggerMarker != null && isTouching) {
					dragImage.setVisibility(View.GONE);

					GeoPoint pt = limitedMapView.getProjection().fromPixels(
							x - xDragTouchOffset, y - yDragTouchOffset);

					Marker toDrop = new Marker(0, pt, draggerMarker.getTitle(),
							"", draggerMarker.getSnippet(), 0);

					Drawable icon = uploadDrawable;
					icon.setBounds(-icon.getIntrinsicWidth() / 2,
							-icon.getIntrinsicHeight(),
							icon.getIntrinsicWidth() / 2, 0);
					toDrop.setMarker(icon);

					mOverlays.remove(draggerMarker);
					mOverlays.add(toDrop);
					populate();

					draggerMarker = toDrop;
					isTouching = false;
					return true;
				} else {
					return (super.onTouchEvent(event, mapView));
				}
			} else {
				return (super.onTouchEvent(event, mapView));
			}
		}

		private void setDragImagePosition(int x, int y) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) dragImage
					.getLayoutParams();

			lp.setMargins(x - xDragImageOffset - xDragTouchOffset, y
					- yDragImageOffset - yDragTouchOffset, 0, 0);
			dragImage.setLayoutParams(lp);
		}

		@Override
		public void showMarkerDialog(int id) {

			Intent intent = new Intent(MapViewActivity.this,
					MarkerDialogActivity.class);
			intent.putExtra("latitude", createItem(id).getPoint()
					.getLatitudeE6() / 1000000d);
			intent.putExtra("longitude", createItem(id).getPoint()
					.getLongitudeE6() / 1000000d);
			intent.putExtra("mode", MARKER_LOCATION);
			boolean uploadButton = false;
			if (getMapLevel() == MapViewActivity.MAP_LEVEL_MARKER
					&& createItem(id) != draggerMarker)
				uploadButton = true;
			intent.putExtra("upload", uploadButton);
			startActivityForResult(intent, DIALOG_INTENT);
		}

		@Override
		public void updateActivity(MapViewActivity newActivity) {
			dragImage = (ImageView) newActivity.findViewById(R.id.drag);
			xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
			yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();
		}

		public void showInfoDialog() {
			int index = mOverlays.indexOf(draggerMarker);
			showMarkerDialog(index);
		}
	}

	public static class SearchActivity extends ListActivity {

		private List<Address> addressList;

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
			try {
				List<Address> addressList = geocoder.getFromLocationName(query,
						5);
				this.addressList = addressList;
				if (addressList.size() == 0) {
					return;
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
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, items);
				setListAdapter(adapter);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onResume() {
			super.onResume();
			if (addressList == null) {
				Toast.makeText(this, "No results found", Toast.LENGTH_SHORT)
						.show();
				this.finish();
			}
		}

		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			Address address = addressList.get(position);
			activity.getResults(address);
			this.finish();
		}

	}

}