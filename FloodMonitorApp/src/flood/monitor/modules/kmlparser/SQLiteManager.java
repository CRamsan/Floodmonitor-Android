package flood.monitor.modules.kmlparser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class will handle accessing and retrieving information from the local
 * database.
 * 
 * @author Cesar
 * 
 */
public class SQLiteManager extends SQLiteOpenHelper {

	public static final String TABLE_REGIONS_NAME = "regions";
	public static final String TABLE_BOUNDARIES_NAME = "boundaries";
	public static final String TABLE_EVENTS_NAME = "events";
	public static final String TABLE_MARKERS_NAME = "markers";
	public static final String TABLE_KML_FILE_NAME = "kmlfiles";
	public static final String TABLE_EXTRA_NAME = "extra";

	public static final String UNIQUE_COLUMN_ID = "_id";

	public static final String KML_FILE_COLUMN_ID = "id";
	public static final String KML_FILE_COLUMN_BOUNDARYID = "boundaryid";
	public static final String KML_FILE_COLUMN_REGIONID = "regionid";
	public static final String KML_FILE_COLUMN_EVENTID = "eventid";

	public static final String MARKERS_COLUMN_ID = "id";
	public static final String MARKERS_COLUMN_SEVERITY = "severity";
	public static final String MARKERS_COLUMN_LATITUDE = "latitude";
	public static final String MARKERS_COLUMN_LONGITUDE = "longitude";
	public static final String MARKERS_COLUMN_OBSERVATION_TIME = "observation";
	public static final String MARKERS_COLUMN_COMMENT = "comment";
	public static final String MARKERS_COLUMN_IMAGEURL = "image";
	public static final String MARKERS_COLUMN_BOUNDARYID = "boundaryid";
	public static final String MARKERS_COLUMN_EVENTID = "eventid";

	public static final String EVENTS_COLUMN_ID = "id";
	public static final String EVENTS_COLUMN_NAME = "name";
	public static final String EVENTS_COLUMN_BEGINDATE = "begindate";
	public static final String EVENTS_COLUMN_ENDDATE = "enddate";
	public static final String EVENTS_COLUMN_ACTIVE = "active";
	public static final String EVENTS_COLUMN_REGIONID = "regionid";

	public static final String REGIONS_COLUMN_ID = "id";
	public static final String REGIONS_COLUMN_NAME = "name";

	public static final String BOUNDARIES_COLUMN_ID = "id";
	public static final String BOUNDARIES_COLUMN_REGIONID = "regionid";
	public static final String BOUNDARIES_COLUMN_NAME = "name";
	public static final String BOUNDARIES_COLUMN_SOUTH = "south";
	public static final String BOUNDARIES_COLUMN_EAST = "east";
	public static final String BOUNDARIES_COLUMN_NORTH = "north";
	public static final String BOUNDARIES_COLUMN_WEST = "west";

	private static final String DATABASE_NAME = "floodmonitor.db";
	private static final int DATABASE_VERSION = 27;

	private static final String CREATE_MARKER_TABLE = "create table "
			+ TABLE_MARKERS_NAME + " ( " + UNIQUE_COLUMN_ID
			+ " integer primary key autoincrement, " + MARKERS_COLUMN_ID
			+ " int, " + MARKERS_COLUMN_LATITUDE + " text not null, "
			+ MARKERS_COLUMN_LONGITUDE + " text not null, "
			+ MARKERS_COLUMN_OBSERVATION_TIME + " text not null, "
			+ MARKERS_COLUMN_COMMENT + " text not null, "
			+ MARKERS_COLUMN_IMAGEURL + " text not null, "
			+ MARKERS_COLUMN_SEVERITY + " int, " + MARKERS_COLUMN_EVENTID
			+ " int, " + MARKERS_COLUMN_BOUNDARYID + " int);";

	private static final String CREATE_KML_FILE_TABLE = "create table "
			+ TABLE_KML_FILE_NAME + " ( " + UNIQUE_COLUMN_ID
			+ " integer primary key autoincrement, " + KML_FILE_COLUMN_ID
			+ " int, " + KML_FILE_COLUMN_EVENTID + " int, "
			+ KML_FILE_COLUMN_REGIONID + " int, " + KML_FILE_COLUMN_BOUNDARYID
			+ " int);";

	private static final String CREATE_EVENT_TABLE = "create table "
			+ TABLE_EVENTS_NAME + " (  " + UNIQUE_COLUMN_ID
			+ " integer primary key autoincrement, " + EVENTS_COLUMN_ID
			+ " int, " + EVENTS_COLUMN_NAME + " text not null, "
			+ EVENTS_COLUMN_ACTIVE + " int, " + EVENTS_COLUMN_BEGINDATE
			+ " text not null, " + EVENTS_COLUMN_ENDDATE + " text not null, "
			+ EVENTS_COLUMN_REGIONID + " int );";

	private static final String CREATE_BOUNDARY_TABLE = "create table "
			+ TABLE_BOUNDARIES_NAME + " (  " + UNIQUE_COLUMN_ID
			+ " integer primary key autoincrement, " + BOUNDARIES_COLUMN_ID
			+ " int, " + BOUNDARIES_COLUMN_REGIONID + " int, "
			+ BOUNDARIES_COLUMN_NAME + " text not null, "
			+ BOUNDARIES_COLUMN_SOUTH + " int, " + BOUNDARIES_COLUMN_NORTH
			+ " text not null, " + BOUNDARIES_COLUMN_WEST + " text not null , "
			+ BOUNDARIES_COLUMN_EAST + " text not null );";

	private static final String CREATE_REGION_TABLE = "create table "
			+ TABLE_REGIONS_NAME + " (  " + UNIQUE_COLUMN_ID
			+ " integer primary key autoincrement, " + REGIONS_COLUMN_ID
			+ " int, " + REGIONS_COLUMN_NAME + " text not null);";

	/**
	 * @param context
	 *            reference to the activity that is accesing the database.
	 */
	public SQLiteManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_KML_FILE_TABLE);
		db.execSQL(CREATE_MARKER_TABLE);
		db.execSQL(CREATE_REGION_TABLE);
		db.execSQL(CREATE_EVENT_TABLE);
		db.execSQL(CREATE_BOUNDARY_TABLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGIONS_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOUNDARIES_NAME);
		onCreate(db);
	}
}