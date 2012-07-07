package flood.monitor.overlay;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import flood.monitor.R;

public abstract class LocationOverlay extends ItemizedOverlay<OverlayItem>{
	private OverlayItem currentLocationMarker;
	private OverlayItem searchResultMarker;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>(0);

	public LocationOverlay(Drawable defaultMarker) {
		super(defaultMarker);
	}
	
	public void updateBestLocation(Location location, Drawable drawable) {
		mOverlays.remove(currentLocationMarker);
		currentLocationMarker = new Marker(new GeoPoint(
				(int) (location.getLatitude() * 1000000),
				(int) (location.getLongitude() * 1000000)), "You are here",
				"Description...", null, 0, 0, 0);
		Drawable icon = drawable;
		icon.setBounds(-icon.getIntrinsicWidth() /2, -icon.getIntrinsicHeight(),
				icon.getIntrinsicWidth()/2, 0);
		currentLocationMarker.setMarker(icon);
		mOverlays.add(currentLocationMarker);
	}	
	
	public void updateSearchResult(Location location, Drawable drawable) {
		mOverlays.remove(currentLocationMarker);
		currentLocationMarker = new Marker(new GeoPoint(
				(int) (location.getLatitude() * 1000000),
				(int) (location.getLongitude() * 1000000)), "You are here",
				"Description...", null, 0, 0, 0);
		Drawable icon = drawable;
		icon.setBounds(-icon.getIntrinsicWidth() /2, -icon.getIntrinsicHeight(),
				icon.getIntrinsicWidth()/2, 0);
		currentLocationMarker.setMarker(icon);
		mOverlays.add(currentLocationMarker);
	}	
}
