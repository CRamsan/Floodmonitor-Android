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
	private boolean isBase = false;
	private int baseId = -1;
	private int diffId = -1;
	private String fileURL;

	/**
	 * @param fileId
	 * @param fileVersion
	 * @param fileURL
	 */
	public KMLFile(int fileId, int fileVersion, String fileURL, boolean isBase) {
		this.fileId = fileId;
		this.fileVersion = fileVersion;
		this.fileURL = fileURL;
		this.isBase = isBase;
	}

	/**
	 * @param fileId
	 * @param fileVersion
	 * @param fileURL
	 * @param regionId
	 * @param boudaryId
	 * @param eventId
	 */
	public KMLFile(int fileId, int fileVersion, String fileURL, boolean isBase,
			int regionId, int boudaryId, int eventId, int diffId) {
		this.fileId = fileId;
		this.fileVersion = fileVersion;
		this.isBase = isBase;
		this.regionId = regionId;
		this.boundaryId = boudaryId;
		this.eventId = eventId;
		this.fileURL = fileURL;
		this.diffId = diffId;
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
	public boolean isBase() {
		return isBase;
	}

	/**
	 * @param isBase
	 */
	public void setBase(boolean isBase) {
		this.isBase = isBase;
	}

	public int getBaseId() {
		return baseId;
	}

	public void setBaseId(int baseId) {
		this.baseId = baseId;
	}

	public int getDiffId() {
		return diffId;
	}

	public void setDiffId(int diffId) {
		this.diffId = diffId;
	}

	public static KMLFile getBaseKML(ArrayList<KMLFile> kmlfiles) {
		int index = -1;
		int baseId = -1;
		KMLFile file = null;
		for (int i = 0; i < kmlfiles.size(); i++){
			file = kmlfiles.get(i);
			if (file.isBase()) {
				if (file.getBaseId() > baseId) {
					baseId = file.getBaseId();
					index = i;
				}
			}
		}
		return kmlfiles.get(index);
	}
	
	public static KMLFile getLatestDiffKML(ArrayList<KMLFile> kmlfiles) {
		int index = -1;
		int versionId = -1;
		KMLFile file = null;
		for (int i = 0; i < kmlfiles.size(); i++){
			file = kmlfiles.get(i);
			if (!file.isBase()) {
				if (file.getFileVersion() > versionId) {
					versionId = file.getFileVersion();
					index = i;
				}
			}
		}
		return kmlfiles.get(index);
	}
}
