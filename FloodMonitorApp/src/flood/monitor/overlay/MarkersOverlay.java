package flood.monitor.overlay;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import flood.monitor.MapViewActivity;
import flood.monitor.MarkerDialogActivity;
import flood.monitor.R;
import flood.monitor.modules.kmlparser.Marker;

public class MarkersOverlay extends ItemizedOverlay<OverlayItem> implements
		IOverlay {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private ArrayList<Marker> markersinPage = new ArrayList<Marker>();
	private MapViewActivity activity;
	private int page;
	private int maxPage;
	private int markersPerPage;
	private boolean pageing;

	// ===========================================================
	// Constructors
	// ===========================================================

	public MarkersOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.markers = new ArrayList<Marker>(0);
		this.pageing = false;
		this.setPage(0);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Marker> getmarkers() {
		return markers;
	}

	public void setOverlay(ArrayList<Marker> overlay) {
		this.markers = new ArrayList<Marker>(0);
		for (Marker marker : overlay) {
			addOverlayMarker(marker);
		}
		populate();
	}

	public void enablePageing(int perPage) {
		this.setPageing(true);
		setMarkersPerPage(perPage);
		if (perPage > markers.size()) {
			perPage = markers.size();
		}

		setMaxPage(markers.size(), perPage);
		if (page >= maxPage) {
			page = maxPage - 1;
		}
		this.markersinPage.clear();
		this.markersinPage.addAll(markers.subList(0, perPage));

		populate();
	}

	public void disablePageing() {
		this.setPageing(false);

		populate();
	}

	public void nextPage() {
		int perPage = markersPerPage;
		if (page + 1 >= maxPage) {
			return;
		}
		this.markersinPage.clear();
		page++;

		if ((page + 1) * markersPerPage >= markers.size()) {
			perPage = markers.size() % perPage;
		}

		this.markersinPage.addAll(markers.subList(page * markersPerPage, page
				* markersPerPage + perPage));
		populate();
	}

	public void previousPage() {
		int perPage = markersPerPage;
		if (page - 1 < 0) {
			return;
		}
		this.markersinPage.clear();

		page--;

		this.markersinPage.addAll(markers.subList(page * markersPerPage, page
				* markersPerPage + perPage));
		populate();
	}

	// ===========================================================
	// Methods from Parent
	// ===========================================================
	@Override
	protected OverlayItem createItem(int i) {
		if (pageing) {
			return markersinPage.get(i);
		} else {
			return markers.get(i);
		}
	}

	@Override
	public int size() {
		if (pageing) {
			return markersinPage.size();
		} else {
			return markers.size();
		}
	}

	@Override
	protected boolean onTap(int index) {
		showMarkerDialog(index);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		return super.onTouchEvent(event, mapView);
	}

	// ===========================================================
	// Methods from Interfaces
	// ===========================================================
	@Override
	public void updateActivity(MapViewActivity newActivity) {
		this.activity = newActivity;
	}

	@Override
	public void showMarkerDialog(int id) {
		Marker dialogMarker = (Marker) createItem(id);
		Intent intent = new Intent(activity, MarkerDialogActivity.class);
		intent.putExtra("id", dialogMarker.getId());
		intent.putExtra("title", dialogMarker.getTitle());
		intent.putExtra("desc", dialogMarker.getSnippet());
		intent.putExtra("severity", dialogMarker.getSeverity());
		intent.putExtra("latitude", dialogMarker.getLatitude());
		intent.putExtra("longitude", dialogMarker.getLongitude());
		intent.putExtra("image", dialogMarker.getImage());
		intent.putExtra("regionId", dialogMarker.getRegionId());
		intent.putExtra("eventId", dialogMarker.getEventId());
		intent.putExtra("markerId", dialogMarker.getId());
		intent.putExtra("mode", MapViewActivity.MARKER_UPLOAD);
		boolean uploadButton = true;
		intent.putExtra("upload", uploadButton);
		activity.startActivityForResult(intent, MapViewActivity.DIALOG_INTENT);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void addOverlayMarker(Marker overlayItem) {
		Drawable icon = null;
		switch (overlayItem.getSeverity()) {
		case 1:
			icon = activity.getResources().getDrawable(R.drawable.marker_blue);
			break;
		case 2:
			icon = activity.getResources().getDrawable(R.drawable.marker_green);
			break;
		case 3:
			icon = activity.getResources()
					.getDrawable(R.drawable.marker_yellow);
			break;
		case 4:
			icon = activity.getResources()
					.getDrawable(R.drawable.marker_orange);
			break;
		case 5:
			icon = activity.getResources().getDrawable(R.drawable.marker_red);
			break;
		default:
			icon = activity.getResources().getDrawable(R.drawable.marker_blue);
			break;
		}
		icon.setBounds(-icon.getIntrinsicWidth() / 2,
				-icon.getIntrinsicHeight(), icon.getIntrinsicWidth() / 2, 0);
		overlayItem.setMarker(icon);
		markers.add(overlayItem);
	}

	public void addOverlay(ArrayList<Marker> overlay) {
		for (int i = 0; i < overlay.size(); i++) {
			this.addOverlayMarker(overlay.get(i));
		}
		populate();
	}

	public boolean isPageing() {
		return pageing;
	}

	public void setPageing(boolean pageing) {
		this.pageing = pageing;
	}

	public int getMarkersPerPage() {
		return markersPerPage;
	}

	public void setMarkersPerPage(int markersPerPage) {
		this.markersPerPage = markersPerPage;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int markers, int markersPerPage) {
		this.maxPage = markers / markersPerPage;
		if (markers % markersPerPage > 0) {
			this.maxPage++;
		}
	}

}
