package flood.monitor.overlay;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import flood.monitor.MapViewActivity;
import flood.monitor.MarkerDialogActivity;
import flood.monitor.R;
import flood.monitor.modules.kmlparser.Marker;

/**
 * Overlay that will display the markers on the map. Each marker have
 * information regarding the state of that location based on severity. The
 * number of markers can be really high so they can be also showed in pages.
 * 
 * @author Cesar
 * 
 */
public class MarkersOverlay extends ItemizedOverlay<OverlayItem> implements
		IOverlay {

	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private ArrayList<Marker> markersinPage = new ArrayList<Marker>();
	private MapViewActivity activity;
	private int page;
	private int maxPage;
	private int markersPerPage;
	private boolean pageing;

	/**
	 * This constructor require the default drawable used for all markers.
	 * 
	 * @param defaultMarker
	 *            Drawable resource used.
	 */
	public MarkersOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.markers = new ArrayList<Marker>(0);
		this.pageing = false;
		this.setPage(0);
	}

	/**
	 * Returns the complete list of markers that this overlay contains.
	 * 
	 * @return an arraylist with all the markers.
	 */
	public ArrayList<Marker> getmarkers() {
		return markers;
	}

	/**
	 * Set the given overlay as the new set of markers.
	 * 
	 * @param overlay
	 *            arraylist containing all the markers.
	 */
	public void setOverlay(ArrayList<Marker> overlay) {
		this.markers = new ArrayList<Marker>(0);
		for (Marker marker : overlay) {
			addOverlayMarker(marker);
		}
		populate();
	}

	/**
	 * Enable visualizing the markers in sets containing a given number of
	 * markers.
	 * 
	 * @param perPage
	 *            number of markers per page. If the parameter is bigger than
	 *            the number of markers or less than 1, then all markers will be
	 *            displayed.
	 */
	public void enablePageing(int perPage) {
		this.setPageing(true);
		setMarkersPerPage(perPage);
		if (perPage > markers.size()) {
			perPage = markers.size();
		} else if (perPage < 1) {
			perPage = markers.size();
		}

		setMaxPage(markers.size(), perPage);
		/*
		 * if (page >= maxPage) { page = maxPage - 1; }
		 */
		this.setPage(0);
		this.markersinPage.clear();
		this.markersinPage.addAll(markers.subList(0, perPage));

		populate();
	}

	/**
	 * Will disable the paging mode and update the markers in the map.
	 */
	public void disablePageing() {
		this.setPageing(false);
		populate();
	}

	/**
	 * If available, the next page in the set of pages will be displayed.
	 */
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
		Log.i("Sample", Integer.toString(markersinPage.size()));
	}

	/**
	 * If available, the previous page in the set of pages will be displayed.
	 */
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
		Log.i("Sample", Integer.toString(markersinPage.size()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int i) {
		if (pageing) {
			return markersinPage.get(i);
		} else {
			return markers.get(i);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		if (pageing) {
			return markersinPage.size();
		} else {
			return markers.size();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected boolean onTap(int index) {
		showMarkerDialog(index);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.android.maps.ItemizedOverlay#onTouchEvent(android.view.MotionEvent
	 * , com.google.android.maps.MapView)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		return super.onTouchEvent(event, mapView);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * flood.monitor.overlay.IOverlay#updateActivity(flood.monitor.MapViewActivity
	 * )
	 */
	@Override
	public void updateActivity(MapViewActivity newActivity) {
		this.activity = newActivity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see flood.monitor.overlay.IOverlay#showMarkerDialog(int)
	 */
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

	/**
	 * Add a marker to the list of markers.
	 * 
	 * @param overlayItem
	 *            marker to be added.
	 */
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

	/**
	 * Add a set of markers to the list of markers.
	 * 
	 * @param overlay
	 *            list of marker to add.
	 */
	public void addOverlay(ArrayList<Marker> overlay) {
		for (int i = 0; i < overlay.size(); i++) {
			this.addOverlayMarker(overlay.get(i));
		}
		populate();
	}

	/**
	 * @return
	 */
	public boolean isPageing() {
		return pageing;
	}

	/**
	 * @param pageing
	 */
	public void setPageing(boolean pageing) {
		this.pageing = pageing;
	}

	/**
	 * @return
	 */
	public int getMarkersPerPage() {
		return markersPerPage;
	}

	/**
	 * @param markersPerPage
	 */
	public void setMarkersPerPage(int markersPerPage) {
		this.markersPerPage = markersPerPage;
	}

	/**
	 * @return
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @param page
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * @return
	 */
	public int getMaxPage() {
		return maxPage;
	}

	/**
	 * @param markers
	 * @param markersPerPage
	 */
	public void setMaxPage(int markers, int markersPerPage) {
		this.maxPage = markers / markersPerPage;
		if (markers % markersPerPage > 0) {
			this.maxPage++;
		}
	}

}
