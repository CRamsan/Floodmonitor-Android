package flood.monitor.modules.kmlparser;

import java.util.ArrayList;


/**
 * @author Cesar
 *
 */
public class Event {

	private String beginDate;
	private String endDate;
	private String name;
	private int eventId = -1;
	private boolean active;
	private int regionId = -1;
	private ArrayList<Marker> markers;

	/**
	 * @param eventId
	 * @param name
	 * @param active
	 * @param beginDate
	 * @param endDate
	 */
	public Event(int eventId, String name, boolean active, String beginDate,
			String endDate) {
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.name = name;
		this.eventId = eventId;
		this.active = active;
	}

	/**
	 * @param eventId
	 * @param name
	 * @param active
	 * @param beginDate
	 * @param endDate
	 * @param regionId
	 */
	public Event(int eventId, String name, boolean active, String beginDate,
			String endDate, int regionId) {
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.name = name;
		this.eventId = eventId;
		this.active = active;
		this.regionId = regionId;
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
	public int getEventId() {
		return eventId;
	}

	/**
	 * @param eventId
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return
	 */
	public String getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate
	 */
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * @return
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @return
	 */
	public ArrayList<Marker> getMarkers() {
		return markers;
	}

	/**
	 * @param markers
	 */
	public void setMarkers(ArrayList<Marker> markers) {
		this.markers = markers;
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
	 * @param o
	 * @return
	 */
	public boolean equals(Event o){
		if(this.getEventId() == o.getEventId() && this.getRegionId() == o.getRegionId()){
			return true;
		}else{
			return false;
		}
	}
}
