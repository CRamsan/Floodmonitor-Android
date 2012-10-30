package flood.monitor.overlay;

import flood.monitor.MapViewActivity;

public interface IOverlay {
	public void updateActivity(MapViewActivity newActivity);

	public void showMarkerDialog(int id);

}
