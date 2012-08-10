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
	private String uploadTime;
	private String image;
	private int severity;
	private int id;
	private int boundaryId;
	private int eventId;

	// ===========================================================
	// Constructors
	// ===========================================================
	public Marker(int id, GeoPoint point, String observationTime, String uploadTime, String userComment, String image, int severity) {
		super(point, observationTime, userComment);
		this.id = id;
		this.image = image;
		this.severity = severity;
		this.uploadTime = uploadTime;
	}
	
	public Marker(int id, GeoPoint point, String observationTime, String uploadTime, String userComment, String image, int severity, int evetId, int boundaryId) {
		super(point, observationTime, userComment);
		this.id = id;
		this.image = image;
		this.severity = severity;
		this.uploadTime = uploadTime;
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

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
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
	
	public boolean equals(Marker o){
		if(this.getId() == o.getId() && this.getBoundaryId() == o.getBoundaryId() && this.getEventId() == o.getEventId()){
			return true;
		}else{
			return false;
		}
	}
}
