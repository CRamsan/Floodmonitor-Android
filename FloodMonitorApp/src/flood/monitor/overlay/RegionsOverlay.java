package flood.monitor.overlay;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import flood.monitor.MapViewActivity;
import flood.monitor.R;
import flood.monitor.modules.kmlparser.Boundary;
import flood.monitor.modules.kmlparser.Event;
import flood.monitor.modules.kmlparser.Region;

/**
 * @author Cesar
 *
 */
public class RegionsOverlay extends ItemizedOverlay<OverlayItem> implements
		IOverlay {

	private ArrayList<OverlayItem> markers;
	private ArrayList<Region> regions;
	private MapViewActivity activity;
	private AlertDialog alertDialog;
	private int height = 0;
	private int width = 0;
	private int x;
	private int y;

	/**
	 * @param defaultMarker
	 * @param regions
	 */
	public RegionsOverlay(Drawable defaultMarker, ArrayList<Region> regions) {
		super(boundCenterBottom(defaultMarker));
		this.markers = new ArrayList<OverlayItem>(0);
		this.setRegions(regions);
	}

	/**
	 * @return
	 */
	public ArrayList<Region> getRegions() {
		return regions;
	}

	/**
	 * @param regions
	 */
	public void setRegions(ArrayList<Region> regions) {
		this.regions = regions;
		for (Region region : regions) {
			OverlayItem item = new OverlayItem(region.getCenter(),
					region.getName(), "");
			markers.add(item);
		}
		populate();
	}

	/**
	 * @param regionId
	 * @return
	 */
	public Region getRegionById(int regionId) {
		for (Region region : regions) {
			if (region.getRegionId() == regionId)
				return region;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return markers.get(i);
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		return markers.size();
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
	 */
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
				GeoPoint northwest = new GeoPoint(boundary.getNorth(),
						boundary.getWest());
				GeoPoint southeast = new GeoPoint(boundary.getSouth(),
						boundary.getEast());
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
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTouchEvent(android.view.MotionEvent, com.google.android.maps.MapView)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			x = (int) event.getX();
			y = (int) event.getY();
		} else if (action == MotionEvent.ACTION_UP) {
			if (Math.abs(x - event.getX()) < 10
					&& Math.abs(y - event.getY()) < 10) {
				GeoPoint p = mapView.getProjection().fromPixels(x, y);
				int id = checkHit(p);
				if (id != -1) {
					((MapViewActivity) activity)
							.downloadEventsAndShowDialog(id);
					return true;
				}
			}
		}
		return (super.onTouchEvent(event, mapView));

	}

	/* (non-Javadoc)
	 * @see flood.monitor.overlay.IOverlay#showMarkerDialog(int)
	 */
	@Override
	public void showMarkerDialog(final int id) {
		AlertDialog.Builder builder;

		Context mContext = activity;
		builder = new AlertDialog.Builder(mContext);

		ArrayList<Event> events = getRegionById(id).getEvents();

		int listSize = events.size();

		final CharSequence[] items = new CharSequence[listSize];

		for (int i = 0; i < listSize; i++) {
			items[i] = events.get(i).getName();
		}

		builder.setTitle("Choose the event to load");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				Region region = getRegionById(id);
				region.setSelectedEvent(item);
				((MapViewActivity) activity).downloadMarkersDialog(region);
			}
		});
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();

	}

	/* (non-Javadoc)
	 * @see flood.monitor.overlay.IOverlay#updateActivity(flood.monitor.MapViewActivity)
	 */
	@Override
	public void updateActivity(MapViewActivity newActivity) {
		this.activity = newActivity;
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;
	}

	/**
	 * @param p
	 * @return
	 */
	private int checkHit(GeoPoint p) {
		for (int i = 0; i < regions.size(); i++) {
			Region region = regions.get(i);
			for (int j = 0; j < region.getBoundaries().size(); j++) {
				Boundary boundary = region.getBoundaries().get(j);
				if (p.getLatitudeE6() < boundary.getNorth()
						&& p.getLongitudeE6() > boundary.getWest()
						&& p.getLatitudeE6() > boundary.getSouth()
						&& p.getLongitudeE6() < boundary.getEast()) {
					return region.getRegionId();
				}
			}
		}
		return -1;
	}

	/**
	 * @param regionId
	 * @param events
	 */
	public void setEvents(int regionId, ArrayList<Event> events) {
		getRegionById(regionId).setEvents(events);
	}

	/**
	 * 
	 */
	public void cancelDialog() {
		if (alertDialog != null) {
			this.alertDialog.cancel();
		}
	}
}
