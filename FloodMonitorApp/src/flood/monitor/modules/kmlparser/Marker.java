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
	private String userComment;
	private String image;
	private int severity;
	private int id;
	private int coverType;
	private int coverHeight;

	// ===========================================================
	// Constructors
	// ===========================================================
	public Marker(GeoPoint point, String observationTime, String userComment, String image, int severity, int coverType, int coverHeight) {
		super(point, observationTime, userComment);
		this.image = image;
		this.severity = severity;
		this.coverType = coverType;
		this.coverHeight =coverHeight;
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

	public int getCoverType() {
		return coverType;
	}

	public void setCoverType(int coverType) {
		this.coverType = coverType;
	}

	public int getCoverHeight() {
		return coverHeight;
	}

	public void setCoverHeight(int coverHeight) {
		this.coverHeight = coverHeight;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
