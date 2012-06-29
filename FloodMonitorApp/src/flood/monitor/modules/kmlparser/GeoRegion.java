package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class GeoRegion {

	private String name;
	private ArrayList<Event> events;
	private int geoRegionId;
	private GeoPoint nw;
	private GeoPoint se;

	public GeoRegion(int regionId, String name, GeoPoint nw, GeoPoint se) {
		this.setName(name);
		this.nw = nw;
		this.se = se;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGeoRegionId() {
		return geoRegionId;
	}

	public void setGeoRegionId(int geoRegionId) {
		this.geoRegionId = geoRegionId;
	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	public GeoPoint getNw() {
		return nw;
	}

	public void setNw(GeoPoint nw) {
		this.nw = nw;
	}

	public GeoPoint getSe() {
		return se;
	}

	public void setSe(GeoPoint se) {
		this.se = se;
	}

}