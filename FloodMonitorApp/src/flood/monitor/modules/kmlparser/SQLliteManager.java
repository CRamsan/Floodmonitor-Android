package flood.monitor.modules.kmlparser;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class SQLliteManager extends SQLiteOpenHelper {

	private SQLiteDatabase db;

	public static final String TABLE_MARKERS_NAME = "markers";
	public static final String TABLE_EXTRA_NAME = "extra";
	public static final int REGION_ID = -1;
	public static final int EVENT_ID = -1;

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_SEVERITY = "severity";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_OBSERVATION_TIME = "observation";
	public static final String COLUMN_UPLOAD_TIME = "upload";
	public static final String COLUMN_COMMENT = "comment";
	public static final String COLUMN_IMAGEURL = "image";

	private static final String DATABASE_NAME = "floodmonitor.db";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_DATABASE = "create table "
			+ TABLE_MARKERS_NAME + "-" + REGION_ID + "-" + EVENT_ID + " ( "
			+ COLUMN_ID + " integer primary key, " + COLUMN_LATITUDE
			+ " text not null, " + COLUMN_LONGITUDE + " text not null, "
			+ COLUMN_SEVERITY + " int, " + COLUMN_OBSERVATION_TIME
			+ " text not null, " + COLUMN_COMMENT + " text not null, "
			+ COLUMN_IMAGEURL + " text not null);";

	public SQLliteManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS_NAME);
		onCreate(db);
	}
}