package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.maps.GeoPoint;

public class ObjectDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SQLiteManager dbHelper;
	private String[] allColumnsMarkers = { 
			SQLiteManager.MARKERS_COLUMN_ID,
			SQLiteManager.MARKERS_COLUMN_LATITUDE,
			SQLiteManager.MARKERS_COLUMN_LONGITUDE,
			SQLiteManager.MARKERS_COLUMN_OBSERVATION_TIME,
			SQLiteManager.MARKERS_COLUMN_COMMENT,
			SQLiteManager.MARKERS_COLUMN_IMAGEURL, 
			SQLiteManager.MARKERS_COLUMN_SEVERITY,
			SQLiteManager.MARKERS_COLUMN_EVENTID, 
			SQLiteManager.MARKERS_COLUMN_BOUNDARYID};

	private String[] allColumnsRegions = { SQLiteManager.REGIONS_COLUMN_ID,
			SQLiteManager.REGIONS_COLUMN_NAME };

	private String[] allColumnsBoundaries = {
			SQLiteManager.BOUNDARIES_COLUMN_ID,
			SQLiteManager.BOUNDARIES_COLUMN_REGIONID,
			SQLiteManager.BOUNDARIES_COLUMN_NAME,
			SQLiteManager.BOUNDARIES_COLUMN_EAST,
			SQLiteManager.BOUNDARIES_COLUMN_NORTH,
			SQLiteManager.BOUNDARIES_COLUMN_SOUTH,
			SQLiteManager.BOUNDARIES_COLUMN_WEST };

	private String[] allColumnsEvents = { SQLiteManager.EVENTS_COLUMN_ID,
			SQLiteManager.EVENTS_COLUMN_NAME,
			SQLiteManager.EVENTS_COLUMN_ACTIVE,
			SQLiteManager.EVENTS_COLUMN_BEGINDATE,
			SQLiteManager.EVENTS_COLUMN_ENDDATE,
			SQLiteManager.EVENTS_COLUMN_REGIONID};

	public ObjectDataSource(Context context) {
		dbHelper = new SQLiteManager(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/*
	 * public Marker createMarker(int id, GeoPoint point, String
	 * observationTime, String uploadTime, String userComment, String image, int
	 * severity, int regionId, int eventId) { ContentValues values = new
	 * ContentValues(); values.put(SQLiteManager.MARKERS_COLUMN_ID, id);
	 * values.put(SQLiteManager.MARKERS_COLUMN_LATITUDE, point.getLatitudeE6());
	 * values.put(SQLiteManager.MARKERS_COLUMN_LONGITUDE,
	 * point.getLongitudeE6());
	 * values.put(SQLiteManager.MARKERS_COLUMN_OBSERVATION_TIME,
	 * observationTime); values.put(SQLiteManager.MARKERS_COLUMN_UPLOAD_TIME,
	 * uploadTime); values.put(SQLiteManager.MARKERS_COLUMN_SEVERITY, severity);
	 * values.put(SQLiteManager.MARKERS_COLUMN_COMMENT, userComment);
	 * values.put(SQLiteManager.MARKERS_COLUMN_IMAGEURL, image);
	 * values.put(SQLiteManager.MARKERS_COLUMN_EVENTID, eventId);
	 * values.put(SQLiteManager.MARKERS_COLUMN_REGIONID, regionId);
	 * 
	 * long insertId =
	 * dcursor.getString(1)atabase.insert(SQLiteManager.TABLE_MARKERS_NAME,
	 * null, values); Cursor cursor =
	 * database.query(SQLiteManager.TABLE_MARKERS_NAME, allColumnsMarkers,
	 * SQLiteManager.UNIQUE_COLUMN_ID + " = " + insertId, null, null, null,
	 * null); cursor.moveToFirst(); Marker newmarker = cursorToMarker(cursor);
	 * cursor.close(); return newmarker; }
	 */

	public boolean insertMarker(Marker marker) {
		ContentValues values = new ContentValues();
		values.put(SQLiteManager.MARKERS_COLUMN_ID, marker.getId());
		values.put(SQLiteManager.MARKERS_COLUMN_LATITUDE, marker.getPoint()
				.getLatitudeE6());
		values.put(SQLiteManager.MARKERS_COLUMN_LONGITUDE, marker.getPoint()
				.getLongitudeE6());
		values.put(SQLiteManager.MARKERS_COLUMN_OBSERVATION_TIME,
				marker.getObservationTime());
		values.put(SQLiteManager.MARKERS_COLUMN_COMMENT,
				marker.getUserComment());
		values.put(SQLiteManager.MARKERS_COLUMN_IMAGEURL, marker.getImage());
		values.put(SQLiteManager.MARKERS_COLUMN_SEVERITY, marker.getSeverity());
		values.put(SQLiteManager.MARKERS_COLUMN_EVENTID, marker.getEventId());
		values.put(SQLiteManager.MARKERS_COLUMN_BOUNDARYID, marker.getBoundaryId());

		long insertId = database.insert(SQLiteManager.TABLE_MARKERS_NAME, null,
				values);
		return (insertId != -1);
	}

	public void deleteMarker(Marker marker) {
		int id = marker.getId();
		int regionId = marker.getBoundaryId();
		int eventId = marker.getEventId();
		database.delete(SQLiteManager.TABLE_MARKERS_NAME,
				SQLiteManager.MARKERS_COLUMN_ID + " = " + id + " AND "
						+ SQLiteManager.MARKERS_COLUMN_EVENTID + " = "
						+ eventId + " AND "
						+ SQLiteManager.MARKERS_COLUMN_BOUNDARYID + " = "
						+ regionId, null);
	}

	public ArrayList<Marker> getAllMarkers(int boundaryId, int eventId) {
		ArrayList<Marker> markers = new ArrayList<Marker>();

		Cursor cursor = database.query(SQLiteManager.TABLE_MARKERS_NAME,
				allColumnsMarkers, SQLiteManager.MARKERS_COLUMN_EVENTID + " = "
						+ eventId + " AND "
						+ SQLiteManager.MARKERS_COLUMN_BOUNDARYID + " = "
						+ boundaryId, null, null, null, null);

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
		int severity = cursor.getInt(6);
		Marker marker = new Marker(
				cursor.getInt(0), new GeoPoint(
				(int) cursor.getLong(1), 
				(int) cursor.getLong(2)),
				cursor.getString(3), 
				cursor.getString(4), 
				cursor.getString(5), 
				severity, 
				cursor.getInt(7),
				cursor.getInt(8));
		return marker;
	}

	public boolean insertEvent(Event event) {
		ContentValues values = new ContentValues();
		values.put(SQLiteManager.EVENTS_COLUMN_ID, event.getEventId());
		values.put(SQLiteManager.EVENTS_COLUMN_NAME, event.getName());
		values.put(SQLiteManager.EVENTS_COLUMN_ACTIVE, event.isActive());
		values.put(SQLiteManager.EVENTS_COLUMN_BEGINDATE, event.getBeginDate());
		values.put(SQLiteManager.EVENTS_COLUMN_ENDDATE, event.getEndDate());
		values.put(SQLiteManager.EVENTS_COLUMN_REGIONID, event.getRegionId());

		long insertId = database.insert(SQLiteManager.TABLE_EVENTS_NAME, null,
				values);
		return (insertId != -1);
	}

	public void deleteEvent(Event event) {
		int id = event.getEventId();
		int regionId = event.getRegionId();
		database.delete(SQLiteManager.TABLE_EVENTS_NAME,
				SQLiteManager.EVENTS_COLUMN_ID + " = " + id + " AND "
						+ SQLiteManager.EVENTS_COLUMN_REGIONID + " = "
						+ regionId, null);
	}

	public ArrayList<Event> getAllEvents(int regionId) {
		ArrayList<Event> events = new ArrayList<Event>(0);

		Cursor cursor = database.query(SQLiteManager.TABLE_EVENTS_NAME,
				allColumnsEvents, SQLiteManager.EVENTS_COLUMN_REGIONID + " = "
						+ regionId, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Event event = cursorToEvent(cursor);
			events.add(event);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return events;
	}

	private Event cursorToEvent(Cursor cursor) {
		Event event = new Event(cursor.getInt(0), cursor.getString(1), true, cursor.getString(3), cursor.getString(4), cursor.getInt(5));
		return event;
	}

	public boolean insertRegion(Region region) {
		ContentValues values = new ContentValues();
		values.put(SQLiteManager.REGIONS_COLUMN_ID, region.getRegionId());
		values.put(SQLiteManager.REGIONS_COLUMN_NAME, region.getName());

		for (Boundary boundary : region.getBoundaries()) {
			insertBoundary(boundary);
		}

		long insertId = database.insert(SQLiteManager.TABLE_REGIONS_NAME, null,
				values);
		return (insertId != -1);
	}

	public void deleteRegion(Region region) {
		int id = region.getRegionId();
		database.delete(SQLiteManager.TABLE_REGIONS_NAME,
				SQLiteManager.REGIONS_COLUMN_ID + " = " + id, null);
		for (Boundary boundary : region.getBoundaries()) {
			deleteBoundary(boundary);
		}
	}

	public ArrayList<Region> getAllRegions() {
		ArrayList<Region> regions = new ArrayList<Region>(0);

		Cursor cursor = database.query(SQLiteManager.TABLE_REGIONS_NAME,
				allColumnsRegions, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Region region = cursorToRegion(cursor);
			regions.add(region);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return regions;
	}

	private Region cursorToRegion(Cursor cursor) {
		Region region = new Region(cursor.getInt(0), cursor.getString(1), null);
		region.setBoundaries(this.getAllBoundaries(region.getRegionId()));
		return region;
	}

	public boolean insertBoundary(Boundary boundary) {
		ContentValues values = new ContentValues();
		values.put(SQLiteManager.BOUNDARIES_COLUMN_ID, boundary.getId());
		values.put(SQLiteManager.BOUNDARIES_COLUMN_REGIONID,
				boundary.getRegionId());
		values.put(SQLiteManager.BOUNDARIES_COLUMN_NAME, boundary.getName());
		values.put(SQLiteManager.BOUNDARIES_COLUMN_EAST, boundary.getEast());
		values.put(SQLiteManager.BOUNDARIES_COLUMN_NORTH, boundary.getNorth());
		values.put(SQLiteManager.BOUNDARIES_COLUMN_SOUTH, boundary.getSouth());
		values.put(SQLiteManager.BOUNDARIES_COLUMN_WEST, boundary.getWest());

		long insertId = database.insert(SQLiteManager.TABLE_BOUNDARIES_NAME,
				null, values);
		return (insertId != -1);
	}

	public void deleteBoundary(Boundary boundary) {
		int id = boundary.getId();
		int regionid = boundary.getRegionId();
		database.delete(SQLiteManager.TABLE_BOUNDARIES_NAME,
				SQLiteManager.BOUNDARIES_COLUMN_ID + " = " + id + " AND "
						+ SQLiteManager.BOUNDARIES_COLUMN_REGIONID + " = "
						+ regionid, null);
	}

	public ArrayList<Boundary> getAllBoundaries(int regionId) {
		ArrayList<Boundary> boundaries = new ArrayList<Boundary>(0);

		Cursor cursor = database.query(SQLiteManager.TABLE_BOUNDARIES_NAME,
				allColumnsBoundaries, SQLiteManager.BOUNDARIES_COLUMN_REGIONID
						+ " = " + regionId, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Boundary boundary = cursorToBoundary(cursor);
			boundaries.add(boundary);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return boundaries;
	}

	private Boundary cursorToBoundary(Cursor cursor) {
		int south = cursor.getInt(5);
		int north = cursor.getInt(4);
		int west = cursor.getInt(6);
		int east = cursor.getInt(3);
		Boundary boundary = new Boundary(cursor.getInt(0), cursor.getInt(1),
				cursor.getString(2), south, north, west, east);
		return boundary;
	}

	public void applyRegionDifferences(ArrayList<Region> cachedRegions,
			ArrayList<Region> newRegions) {
		for (Region oldRegion : cachedRegions) {
			for (Region newRegion : newRegions) {
				if (oldRegion.equals(newRegion)) {
					break;
				}
			}
			deleteRegion(oldRegion);
		}

		for (Region newRegion : newRegions) {
			for (Region oldRegion : cachedRegions) {
				if (oldRegion.equals(newRegion)) {
					break;
				}
			}
			insertRegion(newRegion);
		}
	}

	public void applyEventDifferences(ArrayList<Event> cachedEvents,
			ArrayList<Event> newEvents) {
		for (Event oldEvent : cachedEvents) {
			for (Event newEvent : newEvents) {
				if (oldEvent.equals(newEvent)) {
					break;
				}
			}
			deleteEvent(oldEvent);
		}

		for (Event newEvent : newEvents) {
			for (Event oldEvent : cachedEvents) {
				if (oldEvent.equals(newEvent)) {
					break;
				}
			}
			insertEvent(newEvent);
		}
	}

	public void applyMarkerDifferences(ArrayList<Marker> cachedMarkers,
			ArrayList<Marker> newMarkers) {
		for (Marker oldMarker : cachedMarkers) {
			for (Marker newMarker : newMarkers) {
				if (oldMarker.equals(newMarker)) {
					break;
				}
			}
			deleteMarker(oldMarker);
		}

		for (Marker newMarker : newMarkers) {
			for (Marker oldMarker : cachedMarkers) {
				if (oldMarker.equals(newMarker)) {
					break;
				}
			}
			insertMarker(newMarker);
		}
	}

	public void applyBoundaryDifferences(ArrayList<Boundary> cachedBoundaries,
			ArrayList<Boundary> newBoundaries) {
		for (Boundary oldBoundary : cachedBoundaries) {
			for (Boundary newBoundary : newBoundaries) {
				if (oldBoundary.equals(newBoundary)) {
					break;
				}
			}
			deleteBoundary(oldBoundary);
		}

		for (Boundary newBoundary : newBoundaries) {
			for (Boundary oldBoundary : cachedBoundaries) {
				if (oldBoundary.equals(newBoundary)) {
					break;
				}
			}
			insertBoundary(newBoundary);
		}
	}
}
