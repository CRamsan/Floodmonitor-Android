package flood.monitor.overlay;

import android.app.Activity;

public interface IOverlay {
	public void updateActivity(Activity newActivity);
	public void showMarkerDialog(int id);
	
}
