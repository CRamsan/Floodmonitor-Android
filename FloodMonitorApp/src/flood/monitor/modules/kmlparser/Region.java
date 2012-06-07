package flood.monitor.modules.kmlparser;

public class Region {

	private String originURL;
	private String beginDate;
	private String endDate;
	private String name;
	private int[] edges;
	private int regionId;
	private int latitude;
	private int longitud;
	private boolean active;

	public Region(int regionId, String name, String originURL, boolean active, String beginDate, String endDate, int latitude, int longitud) {
		this.originURL = originURL;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.name = name;
		this.regionId = regionId;
		this.latitude = latitude;
		this.longitud = longitud;
		this.active = active;
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

	public int[] getEdges() {
		return edges;
	}

	public void setEdges(int[] edges) {
		this.edges = edges;
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

	public String getOriginURL() {
		return originURL;
	}

	public void setOriginURL(String originURL) {
		this.originURL = originURL;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}