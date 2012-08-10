package flood.monitor.modules.kmlparser;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MarkerDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SQLliteManager dbHelper;
	private String[] allColumns = { SQLliteManager.COLUMN_ID,
			SQLliteManager.COLUMN_SEVERITY, SQLliteManager.COLUMN_LATITUDE,
			SQLliteManager.COLUMN_LONGITUDE,
			SQLliteManager.COLUMN_OBSERVATION_TIME,
			SQLliteManager.COLUMN_UPLOAD_TIME, SQLliteManager.COLUMN_COMMENT,
			SQLliteManager.COLUMN_IMAGEURL };

	public MarkerDataSource(Context context) {
		dbHelper = new SQLliteManager(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Marker createMarker(int id, GeoPoint point, String observationTime,
			String uploadTime, String userComment, String image, int severity) {
		ContentValues values = new ContentValues();
		values.put(SQLliteManager.COLUMN_ID, id);
		values.put(SQLliteManager.COLUMN_LATITUDE, point.getLatitudeE6());
		values.put(SQLliteManager.COLUMN_LONGITUDE, point.getLongitudeE6());
		values.put(SQLliteManager.COLUMN_OBSERVATION_TIME, observationTime);
		values.put(SQLliteManager.COLUMN_UPLOAD_TIME, uploadTime);
		values.put(SQLliteManager.COLUMN_SEVERITY, severity);
		values.put(SQLliteManager.COLUMN_COMMENT, userComment);
		values.put(SQLliteManager.COLUMN_IMAGEURL, image);

		database.insert(SQLliteManager.TABLE_MARKERS_NAME,
				null, values);
		Cursor cursor = database.query(SQLliteManager.TABLE_MARKERS_NAME,
				allColumns, SQLliteManager.COLUMN_ID + " = " + id, null, null,
				null, null);
		cursor.moveToFirst();
		Marker newmarker = cursorToMarker(cursor);
		cursor.close();
		return newmarker;
	}

	public void deleteMarker(Marker marker) {
		int id = marker.getId();
		database.delete(SQLliteManager.TABLE_MARKERS_NAME, SQLliteManager.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Marker> getAllMarkers() {
		List<Marker> markers = new ArrayList<Marker>();

		Cursor cursor = database.query(SQLliteManager.TABLE_MARKERS_NAME,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Marker marker = cursorToMarker(cursor);
			markers.add(marker);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return markers;
	}

	private Marker cursorToMarker(Cursor cursor) {
		Marker marker = new Marker(cursor.getInt(0), new GeoPoint(
				(int) cursor.getLong(1), (int) cursor.getLong(2)),
				cursor.getString(3), cursor.getString(4), cursor.getString(5),
				cursor.getString(6), cursor.getInt(7));
		return marker;
	}
}
