package flood.monitor.overlay;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

import flood.monitor.MapViewActivity;

/**
 * This class modifies the behavior of a map view by introducing some extra
 * method.
 * 
 * @author Cesar
 * 
 */
public class LimitedMapView extends MapView {

	public static final int REGION_MAX_ZOOM = 12;
	private int mapLevel;

	public LimitedMapView(Context arg0, String arg1) {
		super(arg0, arg1);
	}

	/**
	 * Set the map level to the specified value.
	 * 
	 * @param mapLevel
	 *            to set the map level value.
	 */
	public void setMapLevel(int mapLevel) {
		this.mapLevel = mapLevel;
	}

	/**
	 * Get the map level.
	 * 
	 * @return the current map level.
	 */
	public int getMapLevel() {
		return this.mapLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.android.maps.MapView#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if (action == MotionEvent.ACTION_UP) {
			switch (mapLevel) {
			case MapViewActivity.MAP_LEVEL_REGION:
				/*
				 * if (this.getZoomLevel() > REGION_MAX_ZOOM) {
				 * this.getController().setZoom(REGION_MAX_ZOOM);
				 * Toast.makeText(activity, "Choose a region to zoom further",
				 * Toast.LENGTH_LONG).show(); return true; }
				 */
				break;
			case MapViewActivity.MAP_LEVEL_MARKER:
				/*
				 * if (this.getZoomLevel() < REGION_MAX_ZOOM) {
				 * this.getController().setZoom(REGION_MAX_ZOOM);
				 * Toast.makeText(activity,
				 * "Minimun zoom level for marker layer",
				 * Toast.LENGTH_LONG).show(); return true; }
				 */
				break;
			}
		}
		return super.onTouchEvent(ev);
	}
}
