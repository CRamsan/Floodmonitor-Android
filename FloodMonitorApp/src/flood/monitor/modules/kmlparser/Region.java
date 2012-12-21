package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

/**
 * @author Cesar
 *
 */
public class Region {

	private ArrayList<Boundary> boundaries;
	private ArrayList<Event> events;
	private String name;
	private int regionId;

	private int selectedEvent = -1;

	/**
	 * @param regionId
	 * @param name
	 * @param boundaries
	 */
	public Region(int regionId, String name, ArrayList<Boundary> boundaries) {
		this.setName(name);
		this.setRegionId(regionId);
		this.setBoundaries(boundaries);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public int getRegionId() {
		return regionId;
	}

	/**
	 * @param regionId
	 */
	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	/**
	 * @return
	 */
	public ArrayList<Event> getEvents() {
		return events;
	}

	/**
	 * @param events
	 */
	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	/**
	 * @return
	 */
	public ArrayList<Boundary> getBoundaries() {
		return boundaries;
	}

	/**
	 * @param boundaries
	 */
	public void setBoundaries(ArrayList<Boundary> boundaries) {
		this.boundaries = boundaries;
	}
	
	/**
	 * @param o
	 * @return
	 */
	public boolean equals(Region o){
		if(this.getRegionId() == o.getRegionId() && this.getName() == o.getName())
			return true;
		else
			return false;
	}

	/**
	 * @return
	 */
	public int getSelectedEvent() {
		return selectedEvent;
	}

	/**
	 * @param selectedEvent
	 */
	public void setSelectedEvent(int selectedEvent) {
		this.selectedEvent = selectedEvent;
	}

	/**
	 * @return
	 */
	public GeoPoint getCenter(){
		GeoPoint center = null;
		if(boundaries.size() > 0){
			int latitude = (boundaries.get(0).getNorth() + boundaries.get(0).getSouth())/2;
			int longitude = (boundaries.get(0).getEast() + boundaries.get(0).getWest())/2;
			center = new GeoPoint(latitude, longitude);
		}
		return center;
	}
}