package flood.monitor.overlay;

import flood.monitor.MapViewActivity;

/**
 * Interface that extends the capabilities of an overlay by allowing it to swap
 * between activities. This is usefull then the activity needs to recreate
 * itself.
 * 
 * @author Cesar
 * 
 */
public interface IOverlay {
	/**
	 * Define the activity that will display the information of this overlay.
	 * 
	 * @param newActivity
	 *            activity that will handle this overlay.
	 */
	public void updateActivity(MapViewActivity newActivity);

	/**
	 * Method that will activate the markerDialog.
	 * 
	 * @param id
	 *            unique identifier of the marker.
	 */
	public void showMarkerDialog(int id);

}
