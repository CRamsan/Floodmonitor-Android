package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class Region {

	private String kml;
	private String name;
	private ArrayList<Event> events;
	private ArrayList<Boundary> boundaries;
	private int[] edges;
	private int regionId;
	private int latitude;
	private int longitud;

	public Region(int regionId, String name, ArrayList<Boundary> boundaries) {
		this.setName(name);
		this.setRegionId(regionId);
		this.setBoundaries(boundaries);
	}

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

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	public ArrayList<Boundary> getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(ArrayList<Boundary> boundaries) {
		this.boundaries = boundaries;
	}

}