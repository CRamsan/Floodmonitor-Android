package flood.monitor.overlay;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import flood.monitor.MapViewActivity;
import flood.monitor.R;
import flood.monitor.modules.kmlparser.Boundary;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Region;

public class RegionsOverlay extends Overlay implements IOverlay {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private static ArrayList<Region> regions;
	private Activity activity;
	private int index;
	private RegionsOverlay overlay = this;
	private Location currentLocation;
	private Drawable currentLocationDrawable;
	private OverlayItem currentLocationMarker;
	private int height = 0;
	private int width = 0;
	private int x;
	private int y;
	private boolean moved;

	// ===========================================================
	// Constructors
	// ===========================================================
	public RegionsOverlay(ArrayList<Region> regions) {
		this.regions = regions;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public static ArrayList<Region> getRegions() {
		return regions;
	}

	public static void setRegions(ArrayList<Region> regions) {
		RegionsOverlay.regions = regions;
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

		for (int i = 0; i < regions.size(); i++) {
			Region region = regions.get(i);
			for (int j = 0; j < region.getBoundaries().size(); j++) {
				Boundary boundary = region.getBoundaries().get(j);
				GeoPoint northwest = new GeoPoint(boundary.north, boundary.west);
				GeoPoint southeast = new GeoPoint(boundary.south, boundary.east);
				((MapView) activity.findViewById(R.id.mapview)).getProjection()
						.toPixels(northwest, nw);
				((MapView) activity.findViewById(R.id.mapview)).getProjection()
						.toPixels(southeast, se);

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
			((MapView) activity.findViewById(R.id.mapview))
					.getProjection()
					.toPixels(
							new GeoPoint(
									(int) (currentLocation.getLatitude() * 1000000),
									(int) (currentLocation.getLongitude()) * 1000000),
							center);
			canvas.drawCircle(center.x, center.y, 15, mPaint);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			x = (int) event.getX();
			y = (int) event.getY();
		} else if (action == MotionEvent.ACTION_UP) {
			int samplex = (int) Math.abs(x - event.getX());
			int sampley = (int) Math.abs(y - event.getY());
			if (Math.abs(x - event.getX()) < 10
					&& Math.abs(y - event.getY()) < 10) {
				GeoPoint p = mapView.getProjection().fromPixels(x, y);
				int id = checkHit(p);
				if (id != -1) {
					((MapViewActivity)activity).downloadEventsAndShowDialog(id);
					return true;
				}
			}
		}
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
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;
	}

	public void updateBestLocation(Location location) {
		currentLocationMarker = new Marker(new GeoPoint(
				(int) (location.getLatitude() * 1000000),
				(int) (location.getLongitude() * 1000000)), "You are here",
				"Description...", null, 0, 0, 0);
		Drawable icon = activity.getResources()
				.getDrawable(R.drawable.location);
		icon.setBounds(-icon.getIntrinsicWidth() / 2,
				-icon.getIntrinsicHeight(), icon.getIntrinsicWidth() / 2, 0);
		currentLocationMarker.setMarker(icon);
		currentLocation = location;
		currentLocationDrawable = icon;
	}

	private int checkHit(GeoPoint p) {
		for (int i = 0; i < regions.size(); i++) {
			Region region = regions.get(i);
			for (int j = 0; j < region.getBoundaries().size(); j++) {
				Boundary boundary = region.getBoundaries().get(j);			
				if (p.getLatitudeE6() < boundary.north
						&& p.getLongitudeE6() > boundary.west
						&& p.getLatitudeE6() > boundary.south
						&& p.getLongitudeE6() < boundary.east) {
					return region.getRegionId();
				}
			}
		}
		return -1;
	}


	public void showMarkerDialog(final int id, ArrayList<Event> events) {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		Context mContext = activity;
		builder = new AlertDialog.Builder(mContext);

		int listSize = events.size();

		final CharSequence[] items = new CharSequence[listSize];

		for (int i = 0; i < listSize; i++) {
			items[i] = events.get(i).getName();
		}

		builder.setTitle("Choose the event to load");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				((MapViewActivity) activity).downloadMarkersDialog(id, item);
			}
		});
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();

	}
}
