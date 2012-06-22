package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class Event {

	private String beginDate;
	private String endDate;
	private String name;
	private int regionId;
	private boolean active;
	private GeoPoint nw;
	private GeoPoint se;

	private ArrayList<Region> regions;

	public Event(int regionId, String name, boolean active, String beginDate,
			String endDate, ArrayList<Region> regions) {
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.name = name;
		this.regionId = regionId;
		this.active = active;
		this.regions = regions;

		int north = 0;
		int east = 0;
		int south = 0;
		int west = 0;
		for (int i = 0; i < regions.size(); i++) {
			Region region = regions.get(i);
			if (i == 0) {
				north = region.getNw().getLatitudeE6();
				west = region.getNw().getLongitudeE6();
				south = region.getSe().getLatitudeE6();
				east = region.getSe().getLongitudeE6();
			} else {
				if (north < region.getNw().getLatitudeE6())
					north = region.getNw().getLatitudeE6();
				if (west > region.getNw().getLongitudeE6())
					west = region.getNw().getLongitudeE6();
				if (south > region.getSe().getLatitudeE6())
					south = region.getSe().getLatitudeE6();
				if (east < region.getSe().getLongitudeE6())
					east = region.getSe().getLongitudeE6();
			}
		}
		nw = new GeoPoint(north, west);
		se = new GeoPoint(south, east);
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
