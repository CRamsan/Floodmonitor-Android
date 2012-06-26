package flood.monitor.overlay;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import flood.monitor.LimitedMapView;
import flood.monitor.R;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Region;

public class EventsOverlay extends Overlay implements IOverlay {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private ArrayList<Event> events;
	private Activity activity;
	private int eventIndex;
	private OverlayItem currentLocationMarker;

	private int height = 0;
	private int width = 0;
	private int x;
	private int y;
	private boolean moved;

	// ===========================================================
	// Constructors
	// ===========================================================
	public EventsOverlay(ArrayList<Event> events, int height, int width) {
		this.events = events;
		this.height = height;
		this.width = width;
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

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
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
			for (int j = 0; j < event.getRegions().size(); j++) {
				Region region = event.getRegion(j);
				((LimitedMapView) activity.findViewById(R.id.mapview))
						.getProjection().toPixels(region.getNw(), nw);
				((LimitedMapView) activity.findViewById(R.id.mapview))
						.getProjection().toPixels(region.getSe(), se);
				if (nw.x < 0)
					nw.x = 0;
				else if (nw.x > width)
					nw.x = width;

				if (nw.y < 0)
					nw.y = 0;
				else if (nw.y > height)
					nw.y = height;

				if (se.x > width)
					se.x = width;
				else if (se.x < 0)
					se.x = 0;

				if (se.y > height)
					se.y = height;
				else if (se.y < 0)
					se.y = 0;
				canvas.drawRect(nw.x, nw.y, se.x, se.y, mPaint);
			}
		}

		if (currentLocationMarker != null) {
			Point center = new Point();
			((MapView) activity.findViewById(R.id.mapview)).getProjection()
					.toPixels(currentLocationMarker.getPoint(), center);
			canvas.drawCircle(center.x, center.y, 15, mPaint);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		final int action = e.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			x = (int) e.getX();
			y = (int) e.getY();
			moved = false;
			return false;
		} else if (action == MotionEvent.ACTION_MOVE) {
			moved = true;
			return false;
		} else if (action == MotionEvent.ACTION_UP) {
			if (!moved) {
				int hits = 0;
				for (int i = 0; i < events.size(); i++) {
					Event event = events.get(i);

					GeoPoint p = mapView.getProjection().fromPixels(x, y);

					if (p.getLatitudeE6() < event.getNw().getLatitudeE6()
							&& p.getLongitudeE6() > event.getNw()
									.getLongitudeE6()
							&& p.getLatitudeE6() > event.getSe()
									.getLatitudeE6()
							&& p.getLongitudeE6() < event.getSe()
									.getLongitudeE6()) {
						hits++;
					}
				}
				if (hits == 0) {
					return false;
				} else {
					showMarkerDialog(eventIndex);
					return false;
				}
			} else {
				return false;
			}
		} else {
			return (super.onTouchEvent(e, mapView));
		}
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

	@Override
	public void showMarkerDialog(int id) {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		Context mContext = activity;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.markerdialog,
				(ViewGroup) (activity).findViewById(R.id.markerLayout));

		TextView text = (TextView) layout.findViewById(R.id.textView1);
		ImageView image = (ImageView) layout.findViewById(R.id.imageView1);
		String pathToFile = "/mnt/sdcard/FloodMonitor/.cache/cute_cat.jpeg";
		image.setImageBitmap(BitmapFactory.decodeFile(pathToFile));
		TextView text2 = (TextView) layout.findViewById(R.id.textView2);

		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
		
	}
}
