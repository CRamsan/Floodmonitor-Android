package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

public class Region {

	private ArrayList<Boundary> boundaries;
	private ArrayList<Event> events;
	private String name;
	private int regionId;

	public Region(int regionId, String name, ArrayList<Boundary> boundaries) {
		this.setName(name);
		this.setRegionId(regionId);
		this.setBoundaries(boundaries);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
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