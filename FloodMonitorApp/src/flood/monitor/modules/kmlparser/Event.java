package flood.monitor.modules.kmlparser;

import java.util.ArrayList;


public class Event {

	private String beginDate;
	private String endDate;
	private String name;
	private int eventId;
	private boolean active;
	private int regionId;
	private ArrayList<Marker> markers;

	public Event(int eventId, String name, boolean active, String beginDate,
			String endDate) {
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.name = name;
		this.eventId = eventId;
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public ArrayList<Marker> getMarkers() {
		return markers;
	}

	public void setMarkers(ArrayList<Marker> markers) {
		this.markers = markers;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public boolean equals(Event o){
		if(this.getEventId() == o.getEventId() && this.getRegionId() == o.getRegionId()){
			return true;
		}else{
			return false;
		}
	}
}
