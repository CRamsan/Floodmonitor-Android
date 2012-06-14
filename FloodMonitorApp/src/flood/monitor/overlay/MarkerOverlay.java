package flood.monitor.overlay;

import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData.Item;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Environment;
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

import flood.monitor.MapViewActivity;
import flood.monitor.R;
import flood.monitor.abstracts.ModuleEventListener;
import flood.monitor.modules.kmlparser.MarkerManager;

public class MarkerOverlay extends ItemizedOverlay<OverlayItem> {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Marker currentLocationMarker;
	private OverlayItem uploadLocationMarker;

	private Drawable defaultDrawable;
	
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

	public MarkerOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.mOverlays = new ArrayList<OverlayItem>(0);
	}

	public MarkerOverlay(Drawable defaultMarker, MapViewActivity activity) {
		super(boundCenterBottom(defaultMarker));
		this.isMarking = false;
		this.defaultDrawable = activity.getResources().getDrawable(R.drawable.marker_yellow_large);
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

	public void setOverlay(ArrayList<OverlayItem> overlay) {
		mOverlays = overlay;
		
		for(OverlayItem marker : overlay){
			Drawable icon = null;
			int severity = ((Marker) marker).getSeverity();
			switch (severity) {
			case 4:
				icon = activity.getResources().getDrawable(
						R.drawable.marker_green_large);
				break;
			case 5:
				icon = activity.getResources().getDrawable(
						R.drawable.marker_green_yellow_large);
				break;
			case 6:
				icon = activity.getResources().getDrawable(
						R.drawable.marker_yellow_large);
				break;
			case 7:
				icon = activity.getResources().getDrawable(
						R.drawable.marker_orange_large);
				break;
			case 8:
				icon = activity.getResources().getDrawable(
						R.drawable.marker_red_large);
				break;
			default:
				break;
			}
			icon.setBounds(0, 0, icon.getIntrinsicWidth(),
					icon.getIntrinsicHeight());
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
		/*
		 * AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		 * dialog.setTitle(item.getTitle());
		 * dialog.setMessage(item.getSnippet()); dialog.show();
		 */
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		Context mContext = activity;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.markerdialog,
				(ViewGroup) (activity).findViewById(R.id.markerLayout));

		TextView text = (TextView) layout.findViewById(R.id.textView1);
		text.setText(item.getTitle());
		ImageView image = (ImageView) layout.findViewById(R.id.imageView1);
		String pathToFile = "/mnt/sdcard/FloodMonitor/.cache/cute_cat.jpg";
		image.setImageBitmap(BitmapFactory.decodeFile(pathToFile));
		TextView text2 = (TextView) layout.findViewById(R.id.textView2);
		text2.setText(item.getSnippet());

		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.show();
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		if(mOverlays.size() == 0){
			return false;			
		}
		if (isMarking) {
			final int action = event.getAction();
			final int x = (int) event.getX();
			final int y = (int) event.getY();

			if (action == MotionEvent.ACTION_DOWN) {
				Point p = new Point(0, 0);

				((MapView) activity.findViewById(R.id.mapview)).getProjection().toPixels(uploadLocationMarker.getPoint(),
						p);

				if (hitTest(uploadLocationMarker, defaultDrawable, x - p.x, y
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

				GeoPoint pt = ((MapView) activity.findViewById(R.layout.map)).getProjection().fromPixels(
						x - xDragTouchOffset, y - yDragTouchOffset);

				Marker toDrop = new Marker(pt,
						uploadLocationMarker.getTitle(),
						uploadLocationMarker.getSnippet(), null, 0, 0, 0);

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

	// ===========================================================
	// Methods from Interfaces
	// ===========================================================

	
	// ===========================================================
	// Methods
	// ===========================================================
	public void updateActivity(Activity newActivity) {
		this.activity = newActivity;
		this.defaultDrawable = activity.getResources().getDrawable(R.drawable.marker_yellow_large);
		dragImage = (ImageView) activity.findViewById(R.id.drag);
		xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
		yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();
	}
	
	public void addOverlayMarker(Marker overlayItem) {
		Drawable icon = null;
		switch (overlayItem.getSeverity()) {
		case 4:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_green_large);
			break;
		case 5:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_green_yellow_large);
			break;
		case 6:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_yellow_large);
			break;
		case 7:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_orange_large);
			break;
		case 8:
			icon = activity.getResources().getDrawable(
					R.drawable.marker_red_large);
			break;
		default:
			break;
		}
		icon.setBounds(0, 0, icon.getIntrinsicWidth(),
				icon.getIntrinsicHeight());
		overlayItem.setMarker(icon);
		mOverlays.add(overlayItem);
	}

	public void addOverlayItem(OverlayItem overlayItem) {
		mOverlays.add(overlayItem);
		populate();
	}
	
	public void addOverlay(ArrayList<Marker> overlay) {
		for(int i = 0; i < overlay.size(); i++){
			this.addOverlayMarker(overlay.get(i));	
		}
		populate();
	}
	
	public void updateBestLocation(Location location) {
		mOverlays.remove(currentLocationMarker);
		currentLocationMarker = new Marker(new GeoPoint(
				(int) (location.getLatitude() * 1000000),
				(int) (location.getLongitude() * 1000000)), "You are here",
				"Description...", null, 0,0,0);
		addOverlayItem(currentLocationMarker);
		populate();
	}

	public void initiateDragMarker(Location location) {
		uploadLocationMarker = new OverlayItem(new GeoPoint(
				(int) (location.getLatitude()  * 1000000),
				(int) (location.getLongitude() * 1000000)), "", "");
		addOverlayItem(uploadLocationMarker);
		populate();
		isMarking = true;
	}

	public void stopDragMarker() {
		isMarking = false;
		mOverlays.remove(uploadLocationMarker);
		populate();
	}

	
}
