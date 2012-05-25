package flood.monitor.modules;

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

	// ===========================================================
	// Fields
	// ===========================================================
	private Location bestLocation;
	private Location currentLocation;
	private LocationManager locationManager;
	private LocationListener locationListener;

	// ===========================================================
	// Constructors
	// ===========================================================
	public Locator(Context context) {
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		locationListener = new MobileLocationListener();

		currentLocation = locationManager
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
				1000, 10f, locationListener);
	}

	public void stopListening(Context context) {
		locationManager.removeUpdates(locationListener);
	}

	private void updateLocation(Location newLocation) {
		this.bestLocation = newLocation;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class MobileLocationListener implements LocationListener {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				break;
			case LocationProvider.AVAILABLE:
				break;
			}
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.v("Locator", "Location Changed");
			Log.v("Locator", "Londitude: " + location.getLongitude() + "Latitude: " + location.getLatitude());
			Log.v("Locator", "Altitiude: " + location.getAltitude() + "Accuracy: " + location.getAccuracy());
			Log.v("Locator", "Londitude: " + location.getLongitude() + "Latitude: " + location.getLatitude());
			Log.v("Locator","Timestamp: " + location.getTime());
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

	}
}
