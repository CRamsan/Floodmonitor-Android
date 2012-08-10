package flood.monitor.modules.kmlparser;

public class Boundary {
	private int south;
	private int east;
	private int north;
	private int west;
	private int id;
	private int regionId;
	private String name;

	public Boundary(){
		
	}
	
	public Boundary(int id, int regionId, String name, int south, int north,
			int west, int east) {
		this.id = id;
		this.regionId = regionId;
		this.name = name;
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
	}

	public int getSouth() {
		return south;
	}

	public void setSouth(int south) {
		this.south = south;
	}

	public int getEast() {
		return east;
	}

	public void setEast(int east) {
		this.east = east;
	}

	public int getNorth() {
		return north;
	}

	public void setNorth(int north) {
		this.north = north;
	}

	public int getWest() {
		return west;
	}

	public void setWest(int west) {
		this.west = west;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

}
