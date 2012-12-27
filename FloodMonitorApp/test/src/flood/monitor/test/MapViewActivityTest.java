package flood.monitor.test;

import android.test.ActivityInstrumentationTestCase2;

import com.google.android.maps.MapActivity;

public class MapViewActivityTest extends
		ActivityInstrumentationTestCase2<MapActivity> {
	private MapActivity mActivity;

	public MapViewActivityTest() {
		super(MapActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(true);

		mActivity = getActivity();
	}
}
