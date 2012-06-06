package flood.monitor.modules.kmlparser;

public class Region {
	private String name;
	private String date;
	private int[] edges;
	private int regionId;

	public Region(String name, String date, int id, int[] points) {
		this.setName(name);
		this.setRegionId(id);
		this.setDate(date);
		this.setEdges(points);
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int[] getEdges() {
		return edges;
	}

	public void setEdges(int[] edges) {
		this.edges = edges;
	}

}