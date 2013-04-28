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

/**
 * This class encapsulates the GPS location and tracking. In order to get
 * notifications from this class, an activity needs to be referenced by the
 * activity variable.
 * 
 * @author Cesar
 * 
 */
public class Locator {

	private static final int ONE_MINUTE = 1000 * 60;

	private Activity activity;

	private Location bestLocation;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private boolean isLitening = false;
	private boolean isLocking = false;

	/**
	 * @param activity
	 *            will receive the GPS events.
	 */
	public Locator(MapViewActivity activity) {
		this.activity = activity;
		// Acquire a reference to the system Location Manager
		locationListener = new MobileLocationListener();
		locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);

	}

	/**
	 * @return best current location.
	 */
	public Location getBestLocation() {
		return bestLocation;
	}

	/**
	 * @param newActivity
	 *            will receive the GPS events.
	 */
	public void updateActivity(Activity newActivity) {
		this.activity = newActivity;
		locationManager = (LocationManager) newActivity
				.getSystemService(Context.LOCATION_SERVICE);

	}

	/**
	 * check if there location stored in cache.
	 */
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

	/**
	 * Start listening for a GPS lock.
	 */
	public void startListening() {
		if (!isLitening) {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(activity);
			int updateInterval = Integer.parseInt(sharedPrefs.getString(
					"updates_interval", "30000"));
			if (updateInterval != 0) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, updateInterval, 10f,
						locationListener);
				isLitening = true;
			}
		}
	}

	/**
	 * Stop listening for a GPS lock.
	 */
	public void stopListening() {
		if (isLitening) {
			locationManager.removeUpdates(locationListener);
			isLitening = false;
		}
	}

	/**
	 * Restart the listening process.
	 */
	public void updateListening() {
		if (isLitening) {
			stopListening();
		}
		startListening();
	}

	/**
	 * Handle new location, this location will be checked to make sure is more
	 * suitable than the current best location.
	 * 
	 * @param newLocation
	 *            to be tested as possible best current location.
	 */
	private void updateLocation(Location newLocation) {
		if (isBetterLocation(newLocation, bestLocation)) {
			this.bestLocation = newLocation;
		}
	}

	/**
	 * Method used to compare he current best location and new location. The
	 * best location will be decided based on lock precision and longevity .
	 * 
	 * @param location
	 *            new location to test.
	 * @param currentBestLocation
	 *            best currently stored location.
	 * @return true if new location is better then the old one, false otherwise.
	 */
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

	/**
	 * Check if two location providers(represented as strings), are equal.
	 * 
	 * @param provider1
	 *            first provider to test.
	 * @param provider2
	 *            second provider to test.
	 * @return true if both providers are equal, false otherwise.
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public void activateLocking(){
		setLocking(true);
	}
	
	public void deactivateLocking(){
		setLocking(false);
	}
	
	public boolean isLocking() {
		return isLocking;
	}

	public void setLocking(boolean isLocking) {
		this.isLocking = isLocking;
	}

	/**
	 * Class that will report the GPS events to the activity registered to the
	 * Locator object.
	 * 
	 * @author Cesar
	 * 
	 */
	private class MobileLocationListener implements LocationListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onStatusChanged(java.lang.String,
		 * int, android.os.Bundle)
		 */
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onLocationChanged(android.location
		 * .Location)
		 */
		@Override
		public void onLocationChanged(Location location) {
			updateLocation(location);
			((MapViewActivity) activity).runOnUiThread(new Runnable() {
				public void run() {
					((MapViewActivity) activity).updateBestLocation();
				}
			});
			deactivateLocking();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onProviderEnabled(java.lang.String)
		 */
		@Override
		public void onProviderEnabled(String provider) {
			Log.v("Locator", "Provider Enabled " + provider);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onProviderDisabled(java.lang.String
		 * )
		 */
		@Override
		public void onProviderDisabled(String provider) {
			Log.v("Locator", "Provider Disabled " + provider);
		}

	}

}
