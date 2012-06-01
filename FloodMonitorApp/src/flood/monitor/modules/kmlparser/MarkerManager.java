package flood.monitor.modules.kmlparser;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MarkerManager {

	// Database fields
	private SQLiteDatabase database;
	private SQLliteManager dbHelper;
	private String[] allColumns = { SQLliteManager.COLUMN_ID,
			SQLliteManager.COLUMN_SEVERITY, SQLliteManager.COLUMN_COORDINATES,
			SQLliteManager.COLUMN_OBSERVATION_TIME,
			SQLliteManager.COLUMN_COMMENT, SQLliteManager.COLUMN_IMAGE,
			SQLliteManager.COLUMN_COVER_HEIGHT };

	public MarkerManager(Context context) {
		dbHelper = new SQLliteManager(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Marker createMarker(String Marker) {
		ContentValues values = new ContentValues();
		values.put(SQLliteManager.COLUMN_SEVERITY, Marker);
		values.put(SQLliteManager.COLUMN_COORDINATES, Marker);
		values.put(SQLliteManager.COLUMN_OBSERVATION_TIME, Marker);
		values.put(SQLliteManager.COLUMN_COMMENT, Marker);
		values.put(SQLliteManager.COLUMN_IMAGE, Marker);
		values.put(SQLliteManager.COLUMN_COVER_HEIGHT, Marker);

		long insertId = database.insert(SQLliteManager.TABLE_MARKERS, null,
				values);
		Cursor cursor = database.query(SQLliteManager.TABLE_MARKERS,
				allColumns, SQLliteManager.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Marker newComment = cursorToMarker(cursor);
		cursor.close();
		return newComment;
	}

	public void deleteMarker(Marker Marker) {
		long id = Marker.id;
		System.out.println("Comment deleted with id: " + id);
		database.delete(SQLliteManager.TABLE_MARKERS, SQLliteManager.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Marker> getAllMarkers() {
		List<Marker> comments = new ArrayList<Marker>();

		Cursor cursor = database.query(SQLliteManager.TABLE_MARKERS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Marker comment = cursorToMarker(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return comments;
	}

	private Marker cursorToMarker(Cursor cursor) {
		Marker comment = new Marker();
		comment.id = cursor.getInt(0);
		comment.comment = cursor.getString(4);
		return comment;
	}

	private class Marker {
		public int id;
		public int severity;
		public int latitude;
		public int longitude;
		public String observation;
		public String comment;
		public String image;
		public String time;
	}

}