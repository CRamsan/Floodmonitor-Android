package flood.monitor.overlay;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import flood.monitor.R;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Region;

public class EventsOverlay extends Overlay {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private static ArrayList<Event> events;
	private Activity activity;
	private int eventIndex;
	private OverlayItem currentLocationMarker;

	// ===========================================================
	// Constructors
	// ===========================================================
	public EventsOverlay(ArrayList<Event> events) {
		this.events = events;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public int getEventIndex() {
		return eventIndex;
	}

	public void setEventIndex(int eventIndex) {
		this.eventIndex = eventIndex;
	}

	public static ArrayList<Event> getEvents() {
		return events;
	}

	public static void setEvents(ArrayList<Event> events) {
		EventsOverlay.events = events;
	}

	// ===========================================================
	// Methods from Parent
	// ===========================================================
	@Override
	public void draw(Canvas canvas, MapView mapv, boolean shadow) {
		super.draw(canvas, mapv, shadow);
		Paint mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setAlpha(55);

		Point nw = new Point();
		Point se = new Point();

		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			((MapView) activity.findViewById(R.id.mapview)).getProjection()
					.toPixels(event.getNw(), nw);
			((MapView) activity.findViewById(R.id.mapview)).getProjection()
					.toPixels(event.getSe(), se);
			canvas.drawRect(nw.x, nw.y, se.x, se.y, mPaint);
			// canvas.drawRect(50, 50, 122, 532, mPaint);
		}

		if (currentLocationMarker != null) {
			Point center = new Point();
			((MapView) activity.findViewById(R.id.mapview)).getProjection()
					.toPixels(currentLocationMarker.getPoint(), center);
			canvas.drawCircle(center.x, center.y, 15, mPaint);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		final int action = event.getAction();
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		return (super.onTouchEvent(event, mapView));
	}

	// ===========================================================
	// Methods from Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	public void updateActivity(Activity newActivity) {
		this.activity = newActivity;
	}

	public void updateBestLocation(Location location) {
		currentLocationMarker = new Marker(new GeoPoint(
				(int) (location.getLatitude() * 1000000),
				(int) (location.getLongitude() * 1000000)), "You are here",
				"Description...", null, 0, 0, 0);
	}

	private boolean checkHit() {
		return false;
	}
}
