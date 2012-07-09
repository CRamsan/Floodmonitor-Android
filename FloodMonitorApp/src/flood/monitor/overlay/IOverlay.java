package flood.monitor.overlay;

import android.app.Activity;
import android.location.Location;

import com.google.android.maps.GeoPoint;

public interface IOverlay {
	public void updateActivity(Activity newActivity);

	public void updateBestLocation(Location location);
	
}
