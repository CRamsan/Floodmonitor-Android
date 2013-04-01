package flood.monitor.modules.kmlparser;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.maps.GeoPoint;

/**
 * Class that retrieves information from the SQLiteManager and convert it into
 * objects that can be used by other classes.
 * 
 * @author Cesar
 * 
 */
public class ObjectDataSource {

	private SQLiteDatabase database;
	private SQLiteManager dbHelper;
	private String[] allColumnsMarkers = { SQLiteManager.MARKERS_COLUMN_ID,
			SQLiteManager.MARKERS_COLUMN_LATITUDE,
			SQLiteManager.MARKERS_COLUMN_LONGITUDE,
			SQLiteManager.MARKERS_COLUMN_OBSERVATION_TIME,
			SQLiteManager.MARKERS_COLUMN_COMMENT,
			SQLiteManager.MARKERS_COLUMN_IMAGEURL,
			SQLiteManager.MARKERS_COLUMN_SEVERITY,
			SQLiteManager.MARKERS_COLUMN_EVENTID,
			SQLiteManager.MARKERS_COLUMN_BOUNDARYID };

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
			SQLiteManager.EVENTS_COLUMN_REGIONID };

	/**
	 * Constructor that requires a reference to the current context.
	 * 
	 * @param context
	 *            reference to the calling activity.
	 */
	public ObjectDataSource(Context context) {
		dbHelper = new SQLiteManager(context);
	}

	/**
	 * Open the database and get it ready to retrieve information.
	 * 
	 * @throws SQLException
	 *             if there is an error while opening the database.
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Close the database.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Add a marker to the local database.
	 * 
	 * @param marker
	 *            to be stored in the database.
	 * @return return true if the SQL query run correctly, return flase
	 *         otherwise.
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
		values.put(SQLiteManager.MARKERS_COLUMN_BOUNDARYID,
				marker.getBoundaryId());

		long insertId = database.insert(SQLiteManager.TABLE_MARKERS_NAME, null,
				values);
		return (insertId != -1);
	}

	/**
	 * Delete a marker from the local database.
	 * 
	 * @param marker
	 *            object to be removed.
	 */
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

	/**
	 * Query the database for all the markers of the given event.
	 * 
	 * @param boundaryId
	 *            identifier of the boundary where the event is located.
	 * @param eventId
	 *            identifier of the event.
	 * @return a list of all the markers in the given event.
	 */
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

	/**
	 * Convert a cursor in the database to a marker object.
	 * 
	 * @param cursor
	 *            with a pointer to a location in the database.
	 * @return a marker from the location of the cursor.
	 */
	private Marker cursorToMarker(Cursor cursor) {
		int severity = cursor.getInt(6);
		Marker marker = new Marker(cursor.getInt(0), new GeoPoint(
				(int) cursor.getLong(1), (int) cursor.getLong(2)),
				cursor.getString(3), cursor.getString(4), cursor.getString(5),
				severity, cursor.getInt(7), cursor.getInt(8));
		return marker;
	}

	/**
	 * Add an event to the local database.
	 * 
	 * @param event
	 *            to get inserted into the database.
	 * @return true if the SQL query run correctly, return false otherwise.
	 */
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

	/**
	 * Remove the given event from the local database.
	 * 
	 * @param event
	 *            object to get removed.
	 */
	public void deleteEvent(Event event) {
		int id = event.getEventId();
		int regionId = event.getRegionId();
		database.delete(SQLiteManager.TABLE_EVENTS_NAME,
				SQLiteManager.EVENTS_COLUMN_ID + " = " + id + " AND "
						+ SQLiteManager.EVENTS_COLUMN_REGIONID + " = "
						+ regionId, null);
	}

	/**
	 * Retrieve all the events from the local database.
	 * 
	 * @param regionId
	 *            identifier of the region.
	 * @return the list of events in the given region.
	 */
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

	/**
	 * Convert a cursor in the database to a marker object.
	 * 
	 * @param cursor
	 *            with a pointer to a location in the database.
	 * @return an event from the location of the cursor.
	 */
	private Event cursorToEvent(Cursor cursor) {
		Event event = new Event(cursor.getInt(0), cursor.getString(1), true,
				cursor.getString(3), cursor.getString(4), cursor.getInt(5));
		return event;
	}

	/**
	 * Add a region to the local database.
	 * 
	 * @param region
	 *            to get inserted into the database.
	 * @return true if the SQL query run correctly, return false otherwise.
	 */
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

	/**
	 * Remove the given region from the local database.
	 * 
	 * @param region
	 *            object to get removed.
	 */
	public void deleteRegion(Region region) {
		int id = region.getRegionId();
		database.delete(SQLiteManager.TABLE_REGIONS_NAME,
				SQLiteManager.REGIONS_COLUMN_ID + " = " + id, null);
		for (Boundary boundary : region.getBoundaries()) {
			deleteBoundary(boundary);
		}
	}

	/**
	 * Retrieve all the events from the local database.
	 * 
	 * @return the list of regions in the database.
	 */
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

	/**
	 * Convert a cursor in the database to a region object.
	 * 
	 * @param cursor
	 *            with a pointer to a location in the database.
	 * @return a region from the location of the cursor.
	 */
	private Region cursorToRegion(Cursor cursor) {
		Region region = new Region(cursor.getInt(0), cursor.getString(1), null);
		region.setBoundaries(this.getAllBoundaries(region.getRegionId()));
		return region;
	}

	/**
	 * Add a boundary to the local database.
	 * 
	 * @param boundary
	 *            to get inserted into the databse.
	 * @return true if the SQL query run correctly, return false otherwise.
	 */
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

	/**
	 * Remove the given boundary from the local database.
	 * 
	 * @param boundary
	 *            object to get removed.
	 */
	public void deleteBoundary(Boundary boundary) {
		int id = boundary.getId();
		int regionid = boundary.getRegionId();
		database.delete(SQLiteManager.TABLE_BOUNDARIES_NAME,
				SQLiteManager.BOUNDARIES_COLUMN_ID + " = " + id + " AND "
						+ SQLiteManager.BOUNDARIES_COLUMN_REGIONID + " = "
						+ regionid, null);
	}

	/**
	 * Retrieve all the boundaries from the local database.
	 * 
	 * @param regionId
	 *            identifier of the boundary.
	 * @return the list of the boundaries in the given region.
	 */
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

	/**
	 * Convert a cursor in the database to a marker object.
	 * 
	 * @param cursor
	 *            with a pointer to a location in the database.
	 * @return a boundary from the location of the cursor.
	 */
	private Boundary cursorToBoundary(Cursor cursor) {
		int south = cursor.getInt(5);
		int north = cursor.getInt(4);
		int west = cursor.getInt(6);
		int east = cursor.getInt(3);
		Boundary boundary = new Boundary(cursor.getInt(0), cursor.getInt(1),
				cursor.getString(2), south, north, west, east);
		return boundary;
	}

	/**
	 * Compare two sets of regions, the elements that are not common between the
	 * two will be added to the database.
	 * 
	 * @param cachedRegions
	 *            list of regions that represent the information stored in the
	 *            database.
	 * @param newRegions
	 *            list of regions that contain new elements.
	 */
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

	/**
	 * Compare two sets of events, the elements that are not common between the
	 * two will be added to the database.
	 * 
	 * @param cachedEvents
	 *            list of events that represent the information stored in the
	 *            database.
	 * @param newEvents
	 *            list of events that contain new elements.
	 */
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

	/**
	 * @deprecated in favor of applyMarkerIncrement
	 * 
	 *             Compare two sets of markers, the elements that are not common
	 *             between the two will be added to the database.
	 * 
	 * @param cachedMarkers
	 *            list of markers that represent the information stored in the
	 *            database.
	 * @param newMarkers
	 *            list of markers that contain new elements.
	 */
	public void applyMarkerDifferences(ArrayList<Marker> cachedMarkers,
			ArrayList<Marker> newMarkers) {
		for (Marker newMarker : newMarkers) {
			for (Marker oldMarker : cachedMarkers) {
				if (oldMarker.equals(newMarker)) {
					break;
				}
			}
			insertMarker(newMarker);
		}
	}

	/**
	 * Add a new set of markers to the database. It is assumed that the new set
	 * of markers is an increment of the existing set. Some collisions may
	 * happen if a marker is uploaded by the user and then it is received in the
	 * new set. If this happens, it is assumed that both markers should have the
	 * same ID(primary key) and therefore no duplicates will happen.
	 * 
	 * @param cachedMarkers
	 *            list of markers that represent the information stored in the
	 *            database.
	 * @param newMarkers
	 *            list of markers that contain new elements.
	 */
	public void applyMarkerIncrement(ArrayList<Marker> cachedMarkers,
			ArrayList<Marker> newMarkers) {
		for (Marker newMarker : newMarkers) {
			insertMarker(newMarker);
		}
	}

	/**
	 * Compare two sets of boundaries, the elements that are not common between
	 * the two will be added to the database.
	 * 
	 * @param cachedBoundaries
	 *            list of boundaries that represent the information stored in
	 *            the database.
	 * @param newBoundaries
	 *            list of boundaries that contain new elements.
	 */
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
