package flood.monitor.modules.kmlparser;

/**
 * @author Cesar
 *
 */
public class Boundary {
	/**
	 * 
	 */
	private int south;
	/**
	 * 
	 */
	private int east;
	/**
	 * 
	 */
	private int north;
	/**
	 * 
	 */
	private int west;
	/**
	 * 
	 */
	private int id;
	/**
	 * 
	 */
	private int regionId;
	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	public Boundary(){
		
	}
	
	/**
	 * @param id
	 * @param regionId
	 * @param name
	 * @param south
	 * @param north
	 * @param west
	 * @param east
	 */
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

	/**
	 * @return
	 */
	public int getSouth() {
		return south;
	}

	/**
	 * @param south
	 */
	public void setSouth(int south) {
		this.south = south;
	}

	/**
	 * @return
	 */
	public int getEast() {
		return east;
	}

	/**
	 * @param east
	 */
	public void setEast(int east) {
		this.east = east;
	}

	/**
	 * @return
	 */
	public int getNorth() {
		return north;
	}

	/**
	 * @param north
	 */
	public void setNorth(int north) {
		this.north = north;
	}

	/**
	 * @return
	 */
	public int getWest() {
		return west;
	}

	/**
	 * @param west
	 */
	public void setWest(int west) {
		this.west = west;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
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

}
