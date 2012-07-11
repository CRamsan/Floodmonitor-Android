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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Marker;
import flood.monitor.modules.kmlparser.Parser;
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
	public final static int ENABLE_ADD = 1;
	public final static int ENABLE_UPLOAD = 2;
	// MARKER DIALOG MODES
	public final static int MARKER_LOCATION = 1;
	public final static int MARKER_UPLOAD = 2;
	// MAP OVERLAY LEVEL
	public final static int MAP_LEVEL_REGION = 1;
	public final static int MAP_LEVEL_MARKER = 2;
	// INTENT CODES
	public final static int UPLOAD_INTENT = 100;
	public final static int DIALOG_INTENT = 150;
	// DIALOGS ID
	public final static int EVENT_DOWNLOAD_DIALOG = 100;
	public final static int REGION_DOWNLOAD_DIALOG = 300;
	public final static int MARKER_DOWNLOAD_DIALOG = 400;

	// PREFERENCES
	public final static String PREFS_NAME = "MapViewPref";
	public final static String INSTALL_STATE = "Install_State";
	public final static String MARKER_STATE = "markerState";
	public final static String OVERLAY_STATE = "overlayState";
	public final static String SUBTITLE_TEXT = "subtitleText";
	public final static String SEARCH_TEXT = "searchText";

	// ===========================================================
	// Fields
	// ===========================================================
	private Locator locator;
	private static Geocoder geocoder;
	private static LocationOverlay locationOverlay;// 4
	private static MarkersOverlay markersOverlay;// 3
	private static RegionsOverlay georegionsOverlay;// 2
	private static IOverlay selectedOverlay;

	private static MapViewActivity activity;
	private static LimitedMapView limitedMapView;

	private int markerState;
	private int mapLevel;
	private boolean installedBefore;

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

		MapViewActivity.activity = this;
		setContentView(R.layout.map);

		limitedMapView = (LimitedMapView) findViewById(R.id.mapview);
		limitedMapView.updateActivity(this);
		limitedMapView.displayZoomControls(true);

		locator = new Locator(this);
		locator.updateOldLocation();

		geocoder = new Geocoder(this);

		locationOverlay = new LocationOverlay(this.getResources().getDrawable(
				R.drawable.marker_white));
		limitedMapView.getOverlays().add(locationOverlay);

		ImageButton buttonLock = (ImageButton) findViewById(R.id.buttonLock);
		buttonLock.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences sharedPrefs = PreferenceManager
						.getDefaultSharedPreferences(activity);
				boolean useGPS = sharedPrefs.getBoolean("pref_GPSEnabled",
						false);
				if (useGPS) {
					locator.updateListening(activity);
				}
				switch (mapLevel) {
				case MapViewActivity.MAP_LEVEL_REGION:
					focus(locator.getBestLocation(), 10);
					break;
				case MapViewActivity.MAP_LEVEL_MARKER:
					focus(locator.getBestLocation(), 15);
					break;
				}
			}
		});

		ImageButton addMarker = (ImageButton) findViewById(R.id.buttonAddMarker);
		addMarker.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				markerState = ENABLE_UPLOAD;
				locationOverlay.initiateDragMarker(limitedMapView
						.getMapCenter());
				updateButton();
				((MapView) findViewById(R.id.mapview)).invalidate();
			}
		});

		ImageButton cancelMarker = (ImageButton) findViewById(R.id.buttoCancelMarker);
		cancelMarker.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				markerState = ENABLE_ADD;
				locationOverlay.stopDragMarker();
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
				intent.putExtra("latitude", locationOverlay.getMarkerLocation()
						.getLatitude());
				intent.putExtra("longitude", locationOverlay
						.getMarkerLocation().getLongitude());
				startActivityForResult(intent, UPLOAD_INTENT);
			}
		});

		/*
		 * We will check for any savedInstance, with this we will know if the
		 * activity is created for the first time(requires install of database),
		 * if the activity is been created or if it is been
		 * recreated(configuration change).
		 */
		if (savedInstanceState != null) {
			markerState = savedInstanceState.getInt(MARKER_STATE);
			mapLevel = savedInstanceState.getInt(OVERLAY_STATE);
		} else {
			markerState = ENABLE_ADD;
			mapLevel = MAP_LEVEL_REGION;
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			installedBefore = settings.getBoolean(INSTALL_STATE, false);
			if (installedBefore) {
				onInstall();
			}
			downloadRegionsDialog();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		switch (mapLevel) {
		case MapViewActivity.MAP_LEVEL_REGION:
			selectedOverlay = georegionsOverlay;
			break;
		case MapViewActivity.MAP_LEVEL_MARKER:
			selectedOverlay = markersOverlay;
			break;
		}
		// The activity is about to become visible.
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		boolean useGPS = sharedPrefs.getBoolean("pref_GPSEnabled", false);
		if (useGPS) {
			locator.startListening(this);
		}
		boolean useSatellite = sharedPrefs.getBoolean("pref_UseSatellite",
				false);
		limitedMapView.setSatellite(useSatellite);
		limitedMapView.invalidate();
		updateButton();
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
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// The activity is about to be destroyed.
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		switch (mapLevel) {
		case MapViewActivity.MAP_LEVEL_REGION:
			break;
		case MapViewActivity.MAP_LEVEL_MARKER:
			break;
		}
		locationOverlay.stopDragMarker();
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
		ActivityUtil.updateOptionsMenu(activity);
		return super.onSearchRequested();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case UPLOAD_INTENT:
			markerState = ENABLE_ADD;
			locationOverlay.stopDragMarker();
			updateButton();
			limitedMapView.invalidate();
			break;
		case DIALOG_INTENT:
			if (resultCode == RESULT_OK) {
				markerState = ENABLE_ADD;
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

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			savedInstanceState.putCharSequence(SUBTITLE_TEXT, getActionBar()
					.getSubtitle());
		}
		savedInstanceState.putInt(MARKER_STATE, markerState);
		savedInstanceState.putInt(OVERLAY_STATE, mapLevel);
		// savedInstanceState.putString(SEARCH_TEXT, mapLevel);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getMapLevel() {
		return this.mapLevel;
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

	public void loadMarkerOverlay(Overlay remove) {
		removeOverlay(remove);
		addOverlay(markersOverlay);
	}

	private void addOverlay(Overlay overlay) {
		List<Overlay> mapOverlays = limitedMapView.getOverlays();
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
		new DownloadRegionsTask().execute();
	}

	/*
	 * public void downloadEventsDialog(int regionId) { new
	 * DownloadEventsTask().execute(regionId); }
	 */

	public void downloadEventsAndShowDialog(int regionId) {
		new DownloadAndShowEventsTask().execute(regionId);
	}

	public void downloadMarkersDialog(int regionId, int eventId) {
		new DownloadMarkersTask().execute(regionId, eventId);
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
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		installedBefore = true;
		editor.putBoolean(INSTALL_STATE, installedBefore);
		editor.commit();
		// create DB
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class DownloadRegionsTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showDialog(EVENT_DOWNLOAD_DIALOG);
		}

		@Override
		protected Void doInBackground(Void... params) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			File eventsFile = null;
			if (networkInfo != null && networkInfo.isConnected()) {
				eventsFile = Connector.downloadGeoRegions();
			} else {

			}
			if (eventsFile == null) {
				return null;
			}
			try {
				ArrayList<Region> regions = getGeoRegions(eventsFile.getPath());
				georegionsOverlay = new RegionsOverlay(regions);
				georegionsOverlay.updateActivity(activity);
				selectedOverlay = georegionsOverlay;
				addOverlay((RegionsOverlay) selectedOverlay);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			mapLevel = MAP_LEVEL_REGION;
			limitedMapView.setMapLevel(mapLevel);
			limitedMapView.invalidate();
			dismissDialog(EVENT_DOWNLOAD_DIALOG);
		}
	}

	private class DownloadAndShowEventsTask extends
			AsyncTask<Integer, Void, Void> {
		int regionId;

		@Override
		protected void onPreExecute() {
			showDialog(EVENT_DOWNLOAD_DIALOG);
		}

		@Override
		protected Void doInBackground(Integer... params) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			regionId = params[0];
			File eventsFile = null;
			if (networkInfo != null && networkInfo.isConnected()) {
				eventsFile = Connector.downloadEvents(regionId);
			} else {

			}
			if (eventsFile == null) {
				return null;
			}
			try {
				((RegionsOverlay) selectedOverlay).setEvents(regionId, getEvents(eventsFile.getPath()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			mapLevel = MAP_LEVEL_REGION;
			limitedMapView.setMapLevel(mapLevel);
			limitedMapView.invalidate();
			dismissDialog(EVENT_DOWNLOAD_DIALOG);
			((RegionsOverlay) selectedOverlay).showMarkerDialog(regionId);
		}
	}

	private class DownloadMarkersTask extends AsyncTask<Integer, Void, Void> {
		@Override
		protected void onPreExecute() {
			showDialog(EVENT_DOWNLOAD_DIALOG);
		}

		@Override
		protected Void doInBackground(Integer... params) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			File eventsFile = null;
			if (networkInfo != null && networkInfo.isConnected()) {
				eventsFile = Connector.downloadMarkers(params[0], params[1]);
			} else {

			}
			if (eventsFile == null) {
				return null;
			}
			try {
				ArrayList<Marker> markers = getMarkers(eventsFile.getPath());
				Drawable defaultDrawable = activity.getResources().getDrawable(
						R.drawable.marker_green);
				markersOverlay = new MarkersOverlay(defaultDrawable);
				markersOverlay.updateActivity(activity);
				markersOverlay.setOverlay(markers);
				removeOverlay((Overlay) selectedOverlay);
				selectedOverlay = markersOverlay;
				addOverlay((MarkersOverlay) selectedOverlay);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void none) {
			mapLevel = MAP_LEVEL_MARKER;
			limitedMapView.setMapLevel(mapLevel);
			limitedMapView.invalidate();
			dismissDialog(EVENT_DOWNLOAD_DIALOG);
		}
	}

	// ===========================================================
	// Debug
	// ===========================================================

	private ArrayList<Marker> getMarkers(String filename)
			throws FileNotFoundException {
		InputStream stream = new FileInputStream(filename);
		Parser parser = new Parser();
		return parser.ParseMarkers(filename, stream);
	}

	private ArrayList<Event> getEvents(String filename)
			throws FileNotFoundException {
		InputStream stream = new FileInputStream(filename);
		Parser parser = new Parser();
		return parser.ParseEvents(filename, stream);
	}

	private ArrayList<Region> getGeoRegions(String filename)
			throws FileNotFoundException {
		InputStream stream = new FileInputStream(filename);
		Parser parser = new Parser();
		return parser.ParseGeoRegions(filename, stream);
	}

	private static void focus(Location locationToZoom, int level) {
		MapController mc = limitedMapView.getController();
		mc.animateTo(new GeoPoint(
				(int) (locationToZoom.getLatitude() * 1000000),
				(int) (locationToZoom.getLongitude() * 1000000)));
		mc.setZoom(level);
		limitedMapView.invalidate();
	}

	private static void focus(double lat, double lon, int level) {
		MapController mc = limitedMapView.getController();
		mc.animateTo(new GeoPoint((int) (lat * 1000000), (int) (lon * 1000000)));
		mc.setZoom(level);
		limitedMapView.invalidate();
	}

	private void updateButton() {
		switch (mapLevel) {
		case MAP_LEVEL_MARKER:
			switch (markerState) {
			case ENABLE_ADD:
				findViewById(R.id.buttonUploadMarker).setVisibility(View.GONE);
				findViewById(R.id.buttoCancelMarker).setVisibility(View.GONE);
				findViewById(R.id.buttonAddMarker).setVisibility(View.VISIBLE);
				break;
			case ENABLE_UPLOAD:
				findViewById(R.id.buttonUploadMarker).setVisibility(
						View.VISIBLE);
				findViewById(R.id.buttoCancelMarker)
						.setVisibility(View.VISIBLE);
				findViewById(R.id.buttonAddMarker).setVisibility(View.GONE);
				break;
			}
			break;
		case MAP_LEVEL_REGION:
			switch (markerState) {
			case ENABLE_ADD:
				findViewById(R.id.buttonUploadMarker).setVisibility(View.GONE);
				findViewById(R.id.buttoCancelMarker).setVisibility(View.GONE);
				findViewById(R.id.buttonAddMarker).setVisibility(View.VISIBLE);
				break;
			case ENABLE_UPLOAD:
				findViewById(R.id.buttonUploadMarker).setVisibility(View.GONE);
				findViewById(R.id.buttoCancelMarker)
						.setVisibility(View.VISIBLE);
				findViewById(R.id.buttonAddMarker).setVisibility(View.GONE);
				break;
			}

			break;
		default:
			break;
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

	public class LocationOverlay extends ItemizedOverlay<OverlayItem> {
		private OverlayItem currentLocationMarker;
		private OverlayItem searchResultMarker;
		private OverlayItem draggerMarker;
		private ArrayList<OverlayItem> mOverlays;
		private Drawable uploadDrawable;
		private boolean isMarking;
		private boolean isTouching;
		private boolean isMoving;
		private ImageView dragImage = null;
		private int xDragImageOffset = 0;
		private int yDragImageOffset = 0;
		private int xDragTouchOffset = 0;
		private int yDragTouchOffset = 0;

		public LocationOverlay(Drawable defaultMarker) {
			super(defaultMarker);
			mOverlays = new ArrayList<OverlayItem>(0);
			uploadDrawable = activity.getResources().getDrawable(
					R.drawable.marker_white);
			dragImage = (ImageView) activity.findViewById(R.id.drag);
			xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
			yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();
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
			mOverlays.remove(searchResultMarker);
			searchResultMarker = new OverlayItem(new GeoPoint(
					(int) (address.getLatitude() * 1000000),
					(int) (address.getLongitude() * 1000000)), "", "");
			Drawable icon = activity.getResources().getDrawable(
					R.drawable.marker_white);
			icon.setBounds(-icon.getIntrinsicWidth() / 2,
					-icon.getIntrinsicHeight(), icon.getIntrinsicWidth() / 2, 0);
			searchResultMarker.setMarker(icon);
			mOverlays.add(searchResultMarker);
			populate();
		}

		public void initiateDragMarker(GeoPoint geoPoint) {
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
			temp.setLatitude(draggerMarker.getPoint().getLatitudeE6());
			temp.setLongitude(draggerMarker.getPoint().getLongitudeE6());
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
						isMoving = false;
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
					isMoving = true;
					return true;
				} else if (action == MotionEvent.ACTION_UP
						&& draggerMarker != null && isTouching) {
					dragImage.setVisibility(View.GONE);

					GeoPoint pt = limitedMapView.getProjection().fromPixels(
							x - xDragTouchOffset, y - yDragTouchOffset);

					Marker toDrop = new Marker(pt, draggerMarker.getTitle(),
							draggerMarker.getSnippet(), null, 0, 0, 0);

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
					if (isMoving)
						return true;
					else
						return super.onTouchEvent(event, mapView);
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

		public void showMarkerDialog(int id) {

			Intent intent = new Intent(MapViewActivity.this,
					MarkerDialogActivity.class);
			intent.putExtra("latitude", createItem(id).getPoint()
					.getLatitudeE6());
			intent.putExtra("longitude", createItem(id).getPoint()
					.getLongitudeE6());
			intent.putExtra("mode", MARKER_LOCATION);
			boolean uploadButton = false;
			if (activity.mapLevel == MapViewActivity.MAP_LEVEL_MARKER)
				uploadButton = true;
			intent.putExtra("upload", uploadButton);
			startActivityForResult(intent, DIALOG_INTENT);
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
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						activity, android.R.layout.simple_list_item_1, items);
				setListAdapter(adapter);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onResume() {
			super.onResume();
			if (addressList == null) {
				Toast.makeText(activity, "No results found", Toast.LENGTH_SHORT)
						.show();
				this.finish();
			}
		}

		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			Address address = addressList.get(position);
			activity.updateSearchResult(address);
			switch (activity.mapLevel) {
			case MapViewActivity.MAP_LEVEL_REGION:
				focus(address.getLatitude(), address.getLongitude(), 9);
				break;
			case MapViewActivity.MAP_LEVEL_MARKER:
				focus(address.getLatitude(), address.getLongitude(), 15);
				break;
			}
			this.finish();
		}

	}
}