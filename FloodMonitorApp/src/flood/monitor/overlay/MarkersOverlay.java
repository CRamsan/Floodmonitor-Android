package flood.monitor.overlay;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import flood.monitor.R;
import flood.monitor.modules.kmlparser.MarkerManager;

public class MarkersOverlay extends ItemizedOverlay<OverlayItem> implements IOverlay{

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	private OverlayItem currentLocationMarker;
	private OverlayItem uploadLocationMarker;

	private Drawable defaultDrawable;
	private Drawable uploadDrawable;
	private ImageView dragImage = null;
	private MarkerManager manager;
	private Activity activity;

	private int xDragImageOffset = 0;
	private int yDragImageOffset = 0;
	private int xDragTouchOffset = 0;
	private int yDragTouchOffset = 0;

	private boolean isMarking;
	private boolean isTouching;

	// ===========================================================
	// Constructors
	// ===========================================================

	public MarkersOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.mOverlays = new ArrayList<OverlayItem>(0);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Location getMarkerLocation() {
		Location temp = new Location("Picture Marker");
		temp.setLatitude(uploadLocationMarker.getPoint().getLatitudeE6());
		temp.setLongitude(uploadLocationMarker.getPoint().getLongitudeE6());
		return temp;
	}

	public ArrayList<OverlayItem> getmOverlays() {
		return mOverlays;
	}

	public void setmOverlays(ArrayList<OverlayItem> mOverlays) {
		this.mOverlays = mOverlays;
	}

	public void setOverlay(ArrayList<OverlayItem> overlay) {
		mOverlays = overlay;

		for (OverlayItem marker : overlay) {
			addOverlayMarker((Marker) marker);
		}

		if (currentLocationMarker != null) {
			addOverlayItem(currentLocationMarker);
		}
		populate();
	}

	private void setDragImagePosition(int x, int y) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) dragImage
				.getLayoutParams();

		lp.setMargins(x - xDragImageOffset - xDragTouchOffset, y
				- yDragImageOffset - yDragTouchOffset, 0, 0);
		dragImage.setLayoutParams(lp);
	}

	// ===========================================================
	// Methods from Parent
	// ===========================================================
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);

		if (item == currentLocationMarker || item == uploadLocationMarker) {
			return true;
		}

		showMarkerDialog(index);
		
		/*
		 * AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		 * dialog.setTitle(item.getTitle());
		 * dialog.setMessage(item.getSnippet()); dialog.show();
		 */

		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		if (mOverlays.size() == 0) {
			return false;
		}
		if (isMarking) {
			final int action = event.getAction();
			final int x = (int) event.getX();
			final int y = (int) event.getY();

			if (action == MotionEvent.ACTION_DOWN) {
				Point p = new Point(0, 0);

				((MapView) activity.findViewById(R.id.mapview)).getProjection()
						.toPixels(uploadLocationMarker.getPoint(), p);

				if (hitTest(uploadLocationMarker, uploadDrawable, x - p.x, y
						- p.y)) {
					isTouching = true;
					mOverlays.remove(uploadLocationMarker);
					populate();

					xDragTouchOffset = 0;
					yDragTouchOffset = 0;

					setDragImagePosition(p.x, p.y);
					dragImage.setVisibility(View.VISIBLE);

					xDragTouchOffset = x - p.x;
					yDragTouchOffset = y - p.y;
					return true;
				} else {
					return super.onTouchEvent(event, mapView);
				}
			} else if (action == MotionEvent.ACTION_MOVE
					&& uploadLocationMarker != null && isTouching) {
				setDragImagePosition(x, y);
				return true;
			} else if (action == MotionEvent.ACTION_UP
					&& uploadLocationMarker != null && isTouching) {
				dragImage.setVisibility(View.GONE);

				GeoPoint pt = ((MapView) activity.findViewById(R.id.mapview))
						.getProjection().fromPixels(x - xDragTouchOffset,
								y - yDragTouchOffset);

				Marker toDrop = new Marker(pt, uploadLocationMarker.getTitle(),
						uploadLocationMarker.getSnippet(), null, 0, 0, 0);

				Drawable icon = uploadDrawable;
				icon.setBounds(-icon.getIntrinsicWidth() /2, -icon.getIntrinsicHeight(),
						icon.getIntrinsicWidth()/2, 0);
				toDrop  .setMarker(icon);
				
				mOverlays.remove(uploadLocationMarker);
				mOverlays.add(toDrop);
				populate();

				uploadLocationMarker = toDrop;
				isTouching = false;
				return true;
			} else {
				return (super.onTouchEvent(event, mapView));
			}
		} else {
			return super.onTouchEvent(event, mapView);
		}
	}

	/*
	 * @Override protected boolean hitTest(OverlayItem Item, Drawable marker,
	 * int hitX, int hitY) {
	 * 
	 * return false; }
	 */

	// ===========================================================
	// Methods from Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	public void updateActivity(Activity newActivity) {
		this.activity = newActivity;
		this.defaultDrawable = activity.getResources().getDrawable(
				R.drawable.marker_blue);
		this.uploadDrawable = activity.getResources().getDrawable(
				R.drawable.marker_white);
		dragImage = (ImageView) activity.findViewById(R.id.drag);
		xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
		yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();

	}

	public void addOverlayMarker(Marker overlayItem) {
		Drawable icon = null;
		switch (overlayItem.getSeverity()) {
		case 4:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_blue);
			break;
		case 5:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_green);
			break;
		case 6:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_yellow);
			break;
		case 7:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_orange);
			break;
		case 8:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_red);
			break;
		default:
			break;
		}
		icon.setBounds(-icon.getIntrinsicWidth() / 2,
				-icon.getIntrinsicHeight(), icon.getIntrinsicWidth() / 2, 0);
		overlayItem.setMarker(icon);
		mOverlays.add(overlayItem);
	}

	public void addOverlayItem(OverlayItem overlayItem) {
		mOverlays.add(overlayItem);
		//populate();
	}

	public void addOverlay(ArrayList<Marker> overlay) {
		for (int i = 0; i < overlay.size(); i++) {
			this.addOverlayMarker(overlay.get(i));
		}
		populate();
	}

	public void updateBestLocation(Location location) {
		mOverlays.remove(currentLocationMarker);
		currentLocationMarker = new Marker(new GeoPoint(
				(int) (location.getLatitude() * 1000000),
				(int) (location.getLongitude() * 1000000)), "You are here",
				"Description...", null, 0, 0, 0);
		Drawable icon = activity.getResources().getDrawable(R.drawable.location);
		icon.setBounds(-icon.getIntrinsicWidth() /2, -icon.getIntrinsicHeight(),
				icon.getIntrinsicWidth()/2, 0);
		currentLocationMarker.setMarker(icon);
		addOverlayItem(currentLocationMarker);
		populate();
	}

	public void initiateDragMarker(Location location) {
		uploadLocationMarker = new OverlayItem(new GeoPoint(
				(int) (location.getLatitude() * 1000000),
				(int) (location.getLongitude() * 1000000)), "", "");
		Drawable icon = activity.getResources().getDrawable(R.drawable.marker_white);
		icon.setBounds(-icon.getIntrinsicWidth() /2, -icon.getIntrinsicHeight(),
				icon.getIntrinsicWidth()/2, 0);
		uploadDrawable = icon;
		uploadLocationMarker.setMarker(icon);
		addOverlayItem(uploadLocationMarker);
		populate();
		isMarking = true;
	}

	public void stopDragMarker() {
		isMarking = false;
		mOverlays.remove(uploadLocationMarker);
		populate();
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

		OverlayItem item = mOverlays.get(id);
		TextView text = (TextView) layout.findViewById(R.id.textView1);
		text.setText(item.getTitle());
		ImageView image = (ImageView) layout.findViewById(R.id.imageView1);
		String pathToFile = "/mnt/sdcard/FloodMonitor/.cache/cute_cat.jpeg";
		image.setImageBitmap(BitmapFactory.decodeFile(pathToFile));
		TextView text2 = (TextView) layout.findViewById(R.id.textView2);
		text2.setText(item.getSnippet());

		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
	}

}
