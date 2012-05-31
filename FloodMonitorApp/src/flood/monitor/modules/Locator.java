package flood.monitor.modules;

import java.util.ArrayList;

import com.google.android.maps.OverlayItem;

import flood.monitor.MapViewActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class Locator {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int ONE_MINUTE = 1000 * 60;

	// ===========================================================
	// Fields
	// ===========================================================
	private Location bestLocation;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private MapViewActivity activity;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public Locator(MapViewActivity activity) {
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		this.activity = activity;
		locationListener = new MobileLocationListener();

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
	public void startListening(Context context) {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				ONE_MINUTE, 10f, locationListener);
	}

	public void stopListening(Context context) {
		locationManager.removeUpdates(locationListener);
	}

	private void updateLocation(Location newLocation) {
		if(isBetterLocation(newLocation, bestLocation))
		{
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
				Log.v("Locator", "Provider Status Changed: Temporarily Unavailable");
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
			Log.v("Locator", "Location Changed");
			Log.v("Locator", "Londitude: " + location.getLongitude()
					+ "Latitude: " + location.getLatitude());
			Log.v("Locator", "Altitiude: " + location.getAltitude()
					+ "Accuracy: " + location.getAccuracy());
			Log.v("Locator", "Londitude: " + location.getLongitude()
					+ "Latitude: " + location.getLatitude());
			Log.v("Locator", "Timestamp: " + location.getTime());
			updateLocation(location);
			activity.updateBestLocation();
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
