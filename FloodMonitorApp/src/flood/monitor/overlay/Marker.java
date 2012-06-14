package flood.monitor.overlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import flood.monitor.modules.kmlparser.Region;

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
	private String id;
	private int coverType;
	private int coverHeight;
	private Region region;

	// ===========================================================
	// Constructors
	// ===========================================================
	public Marker(GeoPoint point, String observationTime, String userComment, String image, int severity, int coverType, int coverHeight) {
		super(point, observationTime, userComment);
		this.severity = severity;
		this.image = image;
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

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// ===========================================================
	// Methods from Parent
	// ===========================================================

	// ===========================================================
	// Methods from Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

}
