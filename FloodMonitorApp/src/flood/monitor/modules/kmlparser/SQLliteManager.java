package flood.monitor.modules.kmlparser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLliteManager extends SQLiteOpenHelper {

	public static final String TABLE_MARKERS = "markers";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_SEVERITY = "severity";
	public static final String COLUMN_COORDINATES = "coordinates";
	public static final String COLUMN_OBSERVATION_TIME = "observation";
	public static final String COLUMN_COMMENT = "comment";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_COVER_HEIGHT = "height";

	private static final String DATABASE_NAME = "floodmonitor.db";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_DATABASE = "create table "
			+ TABLE_MARKERS + "( " + COLUMN_ID + " integer primary key, "
			+ COLUMN_COORDINATES + " text not null, " + COLUMN_SEVERITY + " int, "
			+ COLUMN_OBSERVATION_TIME + " text not null, " + COLUMN_COMMENT
			+ " text not null, " + COLUMN_IMAGE + " text not null, "
			+ COLUMN_COVER_HEIGHT + " int);";

	public SQLliteManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);
		onCreate(db);
	}
}