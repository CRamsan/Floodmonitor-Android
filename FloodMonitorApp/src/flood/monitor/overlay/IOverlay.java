package flood.monitor.overlay;

import flood.monitor.MapViewActivity;

/**
 * @author Cesar
 *
 */
public interface IOverlay {
	/**
	 * @param newActivity
	 */
	public void updateActivity(MapViewActivity newActivity);

	/**
	 * @param id
	 */
	public void showMarkerDialog(int id);

}
