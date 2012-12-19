package flood.monitor.overlay;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

import flood.monitor.MapViewActivity;

/**
 * @author Cesar
 *
 */
public class LimitedMapView extends MapView {
	// ========================================================================
	// MEMBERS
	// ========================================================================
	/**
	 * 
	 */
	public static final int REGION_MAX_ZOOM = 12;
	/**
	 * 
	 */
	private int mapLevel;

	// ========================================================================
	// CONSTRUCTORS
	// ========================================================================

	/**
	 * @param context
	 * @param apiKey
	 */
	public LimitedMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public LimitedMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public LimitedMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// ========================================================================
	// GETTERS / SETTERS
	// ========================================================================

	/**
	 * @param mapLevel
	 */
	public void setMapLevel(int mapLevel) {
		this.mapLevel = mapLevel;
	}

	/**
	 * @return
	 */
	public int getMapLevel() {
		return this.mapLevel;
	}

	// ========================================================================
	// EVENT HANDLERS
	// ========================================================================
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapView#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if (action == MotionEvent.ACTION_UP) {
			switch (mapLevel) {
			case MapViewActivity.MAP_LEVEL_REGION:
				/*if (this.getZoomLevel() > REGION_MAX_ZOOM) {
					this.getController().setZoom(REGION_MAX_ZOOM);
					Toast.makeText(activity, "Choose a region to zoom further",
							Toast.LENGTH_LONG).show();
					return true;
				}*/
				break;
			case MapViewActivity.MAP_LEVEL_MARKER:
				/*if (this.getZoomLevel() < REGION_MAX_ZOOM) {
					this.getController().setZoom(REGION_MAX_ZOOM);
					Toast.makeText(activity, "Minimun zoom level for marker layer",
							Toast.LENGTH_LONG).show();
					return true;
				}*/
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	// ===========================================================
	// Methods
	// ===========================================================

}
