package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

public class Event {

	private String beginDate;
	private String endDate;
	private String name;
	private int regionId;
	private boolean active;

	private ArrayList<Region> regions;
	
	public Event(int regionId, String name, boolean active, String beginDate, String endDate, ArrayList<Region> regions) {
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.name = name;
		this.regionId = regionId;
		this.active = active;
		this.regions = regions;
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

	public ArrayList<Region> getRegions() {
		return regions;
	}
	
	public Region getRegion(int index) {
		return regions.get(index);
	}

	public void setRegions(ArrayList<Region> regions) {
		this.regions = regions;
	}

	public void addRegion(Region region) {
		this.regions.add(region);
	}
}