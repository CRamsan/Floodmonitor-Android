package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

/**
 * @author Cesar
 * 
 */
public class KMLFile {

	private int fileId = -1;
	private int fileVersion = -1;
	private int regionId = -1;
	private int boundaryId = -1;	
	private int eventId = -1;
	private int diffId = -1;
	private int diffVersion = -1;
	private String fileURL;

	/**
	 * @param fileId
	 * @param fileVersion
	 * @param fileURL
	 */
	public KMLFile(int fileId, int fileVersion, String fileURL) {
		this.fileId = fileId;
		this.fileVersion = fileVersion;
		this.fileURL = fileURL;
	}

	/**
	 * @param fileId
	 * @param fileVersion
	 * @param fileURL
	 * @param regionId
	 * @param boudaryId
	 * @param eventId
	 */
	public KMLFile(int fileId, int fileVersion, String fileURL, int regionId, int boudaryId, int eventId) {
		this.fileId = fileId;
		this.fileVersion = fileVersion;
		this.regionId = regionId;
		this.boundaryId = boudaryId;
		this.eventId = eventId;
		this.fileURL = fileURL;
	}
	
	/**
	 * @return
	 */
	public int getFileId() {
		return fileId;
	}

	/**
	 * @param fileId
	 */
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	/**
	 * @return
	 */
	public int getFileVersion() {
		return fileVersion;
	}

	/**
	 * @param fileVersion
	 */
	public void setFileVersion(int fileVersion) {
		this.fileVersion = fileVersion;
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

	/**
	 * @return
	 */
	public int getEventId() {
		return eventId;
	}

	/**
	 * @param regionId
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
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
	public String getFileURL() {
		return fileURL;
	}

	/**
	 * @param fileURL
	 */
	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	/**
	 * @return
	 */
	public int getDiffId() {
		return diffId;
	}

	/**
	 * @param diffId
	 */
	public void setDiffId(int diffId) {
		this.diffId = diffId;
	}

	/**
	 * @return
	 */
	public int getDiffVersion() {
		return diffVersion;
	}

	/**
	 * @param diffVersion
	 */
	public void setDiffVersion(int diffVersion) {
		this.diffVersion = diffVersion;
	}
}
