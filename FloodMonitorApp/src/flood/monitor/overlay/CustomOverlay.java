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

public class CustomOverlay extends ItemizedOverlay<OverlayItem> {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private ArrayList<CustomOverlayItem> mOverlays = new ArrayList<CustomOverlayItem>();
	private CustomOverlayItem currentLocationMarker;
	private Drawable defaultMarker;
	private MarkerManager manager;

	private Activity activity;
	
	private CustomOverlayItem pictureLocationMarker;
	private ImageView dragImage = null;
	private int xDragImageOffset = 0;
	private int yDragImageOffset = 0;
	private int xDragTouchOffset = 0;
	private int yDragTouchOffset = 0;

	private boolean isMarking;
	private boolean isTouching;

	// ===========================================================
	// Constructors
	// ===========================================================

	public CustomOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.mOverlays = new ArrayList<CustomOverlayItem>(0);
	}

	public CustomOverlay(Drawable defaultMarker, MapViewActivity activity) {
		super(boundCenterBottom(defaultMarker));
		this.isMarking = false;this.defaultMarker = defaultMarker;

		dragImage = (ImageView) activity.findViewById(R.id.drag);
		xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
		yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Location getMarkerLocation() {
		Location temp = new Location("Picture Marker");
		temp.setLatitude(pictureLocationMarker.getPoint().getLatitudeE6());
		temp.setLongitude(pictureLocationMarker.getPoint().getLongitudeE6());
		return temp;
	}

	public void setOverlay(ArrayList<CustomOverlayItem> overlay) {
		mOverlays = overlay;
		if (currentLocationMarker != null) {
			addOverlay(currentLocationMarker);
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
				.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
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

				((MapView) activity.findViewById(R.layout.map)).getProjection().toPixels(pictureLocationMarker.getPoint(),
						p);

				if (hitTest(pictureLocationMarker, defaultMarker, x - p.x, y
						- p.y)) {
					isTouching = true;
					mOverlays.remove(pictureLocationMarker);
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
					&& pictureLocationMarker != null && isTouching) {
				setDragImagePosition(x, y);
				return true;
			} else if (action == MotionEvent.ACTION_UP
					&& pictureLocationMarker != null && isTouching) {
				dragImage.setVisibility(View.GONE);

				GeoPoint pt = ((MapView) activity.findViewById(R.layout.map)).getProjection().fromPixels(
						x - xDragTouchOffset, y - yDragTouchOffset);

				CustomOverlayItem toDrop = new CustomOverlayItem(pt,
						pictureLocationMarker.getTitle(),
						pictureLocationMarker.getSnippet());

				mOverlays.remove(pictureLocationMarker);
				mOverlays.add(toDrop);
				populate();

				pictureLocationMarker = toDrop;
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
	}
	
	public void addOverlay(CustomOverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	public void updateBestLocation(Location location) {
		mOverlays.remove(currentLocationMarker);
		currentLocationMarker = new CustomOverlayItem(new GeoPoint(
				(int) (location.getLatitude() * 1000000),
				(int) (location.getLongitude() * 1000000)), "You are here",
				"Description...");
		addOverlay(currentLocationMarker);
		populate();
	}

	public void initiateDragMarker(Location location) {
		pictureLocationMarker = new CustomOverlayItem(new GeoPoint(
				(int) (location.getLatitude()  * 1000000),
				(int) (location.getLongitude() * 1000000)), "", "");
		mOverlays.add(pictureLocationMarker);
		populate();
		isMarking = true;
	}

	public void stopDragMarker() {
		isMarking = false;
		mOverlays.remove(pictureLocationMarker);
		populate();
	}

	
}
