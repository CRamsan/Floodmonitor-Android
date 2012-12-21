package flood.monitor.modules.kmlparser;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * @author Cesar
 *
 */
public class Marker extends OverlayItem implements Comparable<Marker> {

	private String image;
	private int severity;
	private int id;
	private int boundaryId;
	private int eventId;
	private int regionId;

	/**
	 * @param id
	 * @param point
	 * @param observationTime
	 * @param userComment
	 * @param image
	 * @param severity
	 */
	public Marker(int id, GeoPoint point, String observationTime,
			String userComment, String image, int severity) {
		super(point, observationTime, userComment);
		this.id = id;
		this.image = image;
		this.severity = severity;
	}

	/**
	 * @param id
	 * @param point
	 * @param observationTime
	 * @param userComment
	 * @param image
	 * @param severity
	 * @param evetId
	 * @param boundaryId
	 */
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

	/**
	 * @return
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * @param severity
	 */
	public void setSeverity(int severity) {
		this.severity = severity;
	}

	/**
	 * @return
	 */
	public String getObservationTime() {
		return this.getTitle();
	}

	/**
	 * @return
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return
	 */
	public String getUserComment() {
		return this.getSnippet();
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
	public int getBoundaryId() {
		return boundaryId;
	}

	/**
	 * @param boundaryId
	 */
	public void setBoundaryId(int boundaryId) {
		this.boundaryId = boundaryId;
	}

	/**
	 * @return
	 */
	public int getEventId() {
		return eventId;
	}

	/**
	 * @param eventId
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return
	 */
	public int getRegionId() {
		return regionId;
	}

	/**
	 * @return
	 */
	public int getLatitudeE6() {
		return getPoint().getLatitudeE6();
	}

	/**
	 * @return
	 */
	public int getLongitudeE6() {
		return getPoint().getLongitudeE6();
	}

	/**
	 * @return
	 */
	public double getLatitude() {
		return getPoint().getLatitudeE6() / 1000000d;
	}

	/**
	 * @return
	 */
	public double getLongitude() {
		return getPoint().getLongitudeE6() / 1000000d;
	}

	/**
	 * @param regionId
	 */
	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean equals(Marker o) {
		if (this.getId() == o.getId()
				&& this.getBoundaryId() == o.getBoundaryId()
				&& this.getEventId() == o.getEventId()) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Marker another) {
		return this.getObservationTime().compareToIgnoreCase(
				another.getObservationTime());
	}
}
