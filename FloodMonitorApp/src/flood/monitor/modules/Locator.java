package flood.monitor.modules;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import flood.monitor.MapViewActivity;

public class Locator {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int ONE_MINUTE = 1000 * 60;

	// ===========================================================
	// Fields
	// ===========================================================
	private Activity activity;

	private Location bestLocation;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private boolean isLitening = false;

	// ===========================================================
	// Constructors
	// ===========================================================
	public Locator(MapViewActivity activity) {
		this.activity = activity;
		// Acquire a reference to the system Location Manager
		locationListener = new MobileLocationListener();
		locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public Location getBestLocation() {
		return bestLocation;
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
	public void updateActivity(Activity newActivity) {
		this.activity = newActivity;
		locationManager = (LocationManager) newActivity
				.getSystemService(Context.LOCATION_SERVICE);

	}

	public void updateLocationFromLastKnownLocation() {
		Location currentLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (currentLocation == null) {
			currentLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (currentLocation != null) {
			updateLocation(currentLocation);
		}
	}

	public void startListening(Context context) {
		if (!isLitening) {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(activity);
			int updateInterval = Integer.parseInt(sharedPrefs.getString("updates_interval", "0"));
			if (updateInterval != 0) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, updateInterval, 10f,
						locationListener);
				isLitening = true;
			}
		}
	}

	public void stopListening(Context context) {
		if (isLitening) {
			locationManager.removeUpdates(locationListener);
			isLitening = false;
		}
	}

	public void updateListening(Context context) {
		if (isLitening) {
			stopListening(context);
		}
		startListening(context);
	}

	private void updateLocation(Location newLocation) {
		if (isBetterLocation(newLocation, bestLocation)) {
			this.bestLocation = newLocation;
		}
	}

	private boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > ONE_MINUTE * 2;
		boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE * 2;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class MobileLocationListener implements LocationListener {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				Log.v("Locator", "Provider Status Changed: Out Of Service");
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.v("Locator",
						"Provider Status Changed: Temporarily Unavailable");
				break;
			case LocationProvider.AVAILABLE:
				Log.v("Locator", "Provider Status Changed: Available");
				break;
			default:
				Log.v("Locator", "Provider Status Changed: Not Specified");
				break;
			}
		}

		@Override
		public void onLocationChanged(Location location) {
			updateLocation(location);
			((MapViewActivity) activity).runOnUiThread(new Runnable() {
				public void run() {
					((MapViewActivity) activity).updateBestLocation();
				}
			});

		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.v("Locator", "Provider Enabled " + provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.v("Locator", "Provider Disabled " + provider);
		}

	}

}
