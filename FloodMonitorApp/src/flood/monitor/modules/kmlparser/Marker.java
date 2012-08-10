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
	private String observationTime;
	private String uploadTime;
	private String userComment;
	private String image;
	private int severity;
	private int id;

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
		return observationTime;
	}

	public void setObservationTime(String observationTime) {
		this.observationTime = observationTime;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
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
}
