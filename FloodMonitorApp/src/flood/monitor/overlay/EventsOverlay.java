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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
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
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(2);

		Point nw = new Point();
		Point ne = new Point();
		Point se = new Point();
		Point sw = new Point();

		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			((MapView) activity.findViewById(R.id.mapview)).getProjection()
					.toPixels(event.getNw(), nw);
			((MapView) activity.findViewById(R.id.mapview)).getProjection()
					.toPixels(event.getNe(), ne);
			((MapView) activity.findViewById(R.id.mapview)).getProjection()
					.toPixels(event.getSe(), sw);
			((MapView) activity.findViewById(R.id.mapview)).getProjection()
					.toPixels(event.getSw(), se);
			//canvas.drawRect(nw.x, nw.y, se.x, se.y, mPaint);
			canvas.drawRect(50, 50, 122, 532, mPaint);
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

	private boolean checkHit(){
		return false;
	}
}
