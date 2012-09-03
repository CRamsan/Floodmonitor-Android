package flood.monitor.modules.kmlparser;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Marker extends OverlayItem {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private String image;
	private int severity;
	private int id;
	private int boundaryId;
	private int eventId;
	private int regionId;

	// ===========================================================
	// Constructors
	// ===========================================================
	public Marker(int id, GeoPoint point, String observationTime,
			String userComment, String image, int severity) {
		super(point, observationTime, userComment);
		this.id = id;
		this.image = image;
		this.severity = severity;
	}

	public Marker(int id, GeoPoint point, String observationTime,
			String userComment, String image, int severity, int evetId,
			int boundaryId) {
		super(point, observationTime, userComment);
		this.id = id;
		this.image = image;
		this.severity = severity;
		this.eventId = evetId;
		this.boundaryId = boundaryId;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public String getObservationTime() {
		return this.getTitle();
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUserComment() {
		return this.getSnippet();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBoundaryId() {
		return boundaryId;
	}

	public void setBoundaryId(int boundaryId) {
		this.boundaryId = boundaryId;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public int getRegionId() {
		return regionId;
	}

	public int getLatitudeE6() {
		return getPoint().getLatitudeE6();
	}

	public int getLongitudeE6() {
		return getPoint().getLongitudeE6();
	}

	public double getLatitude() {
		return getPoint().getLatitudeE6() / 1000000d;
	}

	public double getLongitude() {
		return getPoint().getLongitudeE6() / 1000000d;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public boolean equals(Marker o) {
		if (this.getId() == o.getId()
				&& this.getBoundaryId() == o.getBoundaryId()
				&& this.getEventId() == o.getEventId()) {
			return true;
		} else {
			return false;
		}
	}
}
