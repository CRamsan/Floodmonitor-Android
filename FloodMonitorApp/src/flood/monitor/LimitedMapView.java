package flood.monitor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import flood.monitor.modules.kmlparser.Event;

public class LimitedMapView extends MapView {
	// ========================================================================
	// MEMBERS
	// ========================================================================

	private MapViewActivity activity;
	private int mapLevel;

	// ========================================================================
	// CONSTRUCTORS
	// ========================================================================

	public LimitedMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public LimitedMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LimitedMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// ========================================================================
	// GETTERS / SETTERS
	// ========================================================================

	public void setMapLevel(int mapLevel) {
		this.mapLevel = mapLevel;
	}

	public int getMapLever() {
		return this.mapLevel;
	}

	// ========================================================================
	// EVENT HANDLERS
	// ========================================================================
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if (action == MotionEvent.ACTION_UP) {
			switch (mapLevel) {
			case MapViewActivity.MAP_LEVEL_EVENT:
				if (this.getZoomLevel() > 10) {
					this.getController().setZoom(10);
				}
				break;
			case MapViewActivity.MAP_LEVEL_REGION:
				break;
			case MapViewActivity.MAP_LEVEL_MARKER:
				break;
			}
			return super.onTouchEvent(ev);
		} else {
			return super.onTouchEvent(ev);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void updateActivity(MapViewActivity newActivity) {
		this.activity = newActivity;
		this.mapLevel = activity.getMapLevel();
	}
}