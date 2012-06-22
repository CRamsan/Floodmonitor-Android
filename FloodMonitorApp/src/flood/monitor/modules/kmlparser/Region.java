package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class Region {

	private String kml;
	private String name;
	private Event event;
	private int[] edges;
	private int regionId;
	private int latitude;
	private int longitud;
	private GeoPoint nw;
	private GeoPoint se;

	public Region(int regionId, String name, String kml, int latitude,
			int longitud) {
		this.setKml(kml);
		this.setName(name);
		this.setRegionId(regionId);
		this.setLatitude(latitude);
		this.setLongitud(longitud);
	}

	public Region(int regionId, String name, GeoPoint nw, GeoPoint se) {
		this.setName(name);
		this.setRegionId(regionId);
		this.nw = nw;
		this.se = se;
	}

	public String getKml() {
		return kml;
	}

	public void setKml(String kml) {
		this.kml = kml;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getEdges() {
		return edges;
	}

	public void setEdges(int[] edges) {
		this.edges = edges;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitud() {
		return longitud;
	}

	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
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