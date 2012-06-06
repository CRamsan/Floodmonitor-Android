package flood.monitor.overlay;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData.Item;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import flood.monitor.MapViewActivity;
import flood.monitor.R;

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
	private int coverType;
	private int coverHeight;

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
	
	public Marker(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);

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
