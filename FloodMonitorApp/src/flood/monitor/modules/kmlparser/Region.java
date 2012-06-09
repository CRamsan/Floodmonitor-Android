package flood.monitor.modules.kmlparser;

public class Region {

	private String originURL;
	private String name;
	private int[] edges;
	private int regionId;
	private int latitude;
	private int longitud;
	private int nw;
	private int se;

	public Region(int regionId, String name, String originURL, int latitude, int longitud) {
		this.setOriginURL(originURL);
		this.setName(name);
		this.setRegionId(regionId);
		this.setLatitude(latitude);
		this.setLongitud(longitud);
	}

	public Region(int regionId, String name, int nw, int se) {
		this.setName(name);
		this.setRegionId(regionId);
		this.nw = nw;
		this.se = se;
	}
	
	public String getOriginURL() {
		return originURL;
	}

	public void setOriginURL(String originURL) {
		this.originURL = originURL;
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

	
}