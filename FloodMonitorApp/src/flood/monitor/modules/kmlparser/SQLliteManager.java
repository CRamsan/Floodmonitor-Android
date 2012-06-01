package flood.monitor.modules.kmlparser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLliteManager extends SQLiteOpenHelper {

	public static final String PK_ATTR = "INTEGER PRIMARY KEY";
	public static final String INTEGER_ATTR = "INTEGER";
	public static final String TEXT_ATTR = "TEXT";

	public static final String TABLE_MARKER_LOCATION = "markerlocation";
	public static final String LOCATION_PK = "locationID";
	public static final String LOCATION_PK_ATTR = PK_ATTR;
	public static final String LOCATION_LAT = "lat";
	public static final String LOCATION_LAT_ATTR = INTEGER_ATTR;
	public static final String LOCATION_LONG = "long";
	public static final String LOCATION_LONG_ATTR = INTEGER_ATTR;

	public static final String TABLE_EVENT_TYPE = "eventtype";
	public static final String EVENT_TYPE_PK = "eventTypeID";
	public static final String EVENT_TYPE_ATTR = PK_ATTR;

	public static final String TABLE_ADDITIONAL_INFO = "additionalinfo";
	public static final String ADDITIONAL_INFO_PK = "infoID";
	public static final String ADDITIONAL_INFO_PK_ATTR = PK_ATTR;
	public static final String ADDITIONAL_INFO_FK = EVENT_TYPE_PK;
	public static final String ADDITIONAL_INFO_FK_ATTR = "FOREIGN KEY("
			+ ADDITIONAL_INFO_FK + ") REFERENCES " + TABLE_EVENT_TYPE + "("
			+ EVENT_TYPE_PK + ")";

	public static final String TABLE_SEVERITY = "severity";
	public static final String SEVERITY_PK = "severityID";
	public static final String SEVERITY_PK_ATTR = PK_ATTR;
	public static final String SEVERITY_FK = EVENT_TYPE_PK;
	public static final String SEVERITY_FK_ATTR = "FOREIGN KEY(" + SEVERITY_FK
			+ ") REFERENCES " + TABLE_EVENT_TYPE + "(" + EVENT_TYPE_PK + ")";;

	public static final String TABLE_EVENT_REGION = "eventregion";
	public static final String EVENT_REGION_PK = "regionID";
	public static final String EVENT_REGION_PK_ATTR = PK_ATTR;

	public static final String TABLE_EVENT = "event";
	public static final String EVENT_PK = "eventID";
	public static final String EVENT_PK_ATTR = PK_ATTR;
	public static final String EVENT_FK1 = EVENT_REGION_PK;
	public static final String EVENT_FK1_ATTR = "FOREIGN KEY(" + EVENT_FK1
			+ ") REFERENCES " + TABLE_EVENT_REGION + "(" + EVENT_REGION_PK
			+ ")";
	public static final String EVENT_FK2 = EVENT_TYPE_PK;
	public static final String EVENT_FK2_ATTR = "FOREIGN KEY(" + EVENT_FK2
			+ ") REFERENCES " + TABLE_EVENT_TYPE + "(" + EVENT_TYPE_PK + ")";

	public static final String TABLE_MARKER = "marker";
	public static final String MARKER_PK = "markerID";
	public static final String MARKER_PK_ATTR = PK_ATTR;
	public static final String MARKER_FK1 = LOCATION_PK;
	public static final String MARKER_FK1_ATTR = "FOREIGN KEY(" + MARKER_FK1
			+ ") REFERENCES " + TABLE_MARKER_LOCATION + "(" + LOCATION_PK + ")";
	public static final String MARKER_FK2 = EVENT_PK;
	public static final String MARKER_FK2_ATTR = "FOREIGN KEY(" + MARKER_FK2
			+ ") REFERENCES " + TABLE_EVENT + "(" + EVENT_PK + ")";
	public static final String MARKER_FK3 = SEVERITY_PK;
	public static final String MARKER_FK3_ATTR = "FOREIGN KEY(" + MARKER_FK3
			+ ") REFERENCES " + TABLE_SEVERITY + "(" + SEVERITY_PK + ")";

	public static final String TABLE_MARKER_INFO = "markerinfo";
	public static final String MARKER_INFO_PK1 = MARKER_PK;
	public static final String MARKER_INFO_PK1_ATTR = PK_ATTR;
	public static final String MARKER_INFO_FK1_ATTR = "FOREIGN KEY("
			+ MARKER_INFO_PK1 + ") REFERENCES " + TABLE_MARKER + "("
			+ MARKER_PK + ")";

	public static final String MARKER_INFO_PK2 = ADDITIONAL_INFO_PK;
	public static final String MARKER_INFO_PK2_ATTR = PK_ATTR;
	public static final String MARKER_INFO_FK2_ATTR = "FOREIGN KEY("
			+ MARKER_INFO_PK2 + ") REFERENCES " + TABLE_ADDITIONAL_INFO + "("
			+ ADDITIONAL_INFO_PK + ")";

	public static final String TABLE_BOUNDARY = "boundary";
	public static final String BOUNDARY_PK = "boundaryID";
	public static final String BOUNDARY_PK_ATTR = PK_ATTR;
	public static final String BOUNDARY_FK = EVENT_REGION_PK;
	public static final String BOUNDARY_FK_ATTR = "FOREIGN KEY(" + BOUNDARY_FK
			+ ") REFERENCES " + TABLE_EVENT_REGION + "(" + EVENT_PK + ")";

	public static final String DATABASE_NAME = "floodmonitor.db";
	public static final int DATABASE_VERSION = 1;

	public SQLliteManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		init(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	private void init(SQLiteDatabase db) {
		String[] CREATE_TABLE_MARKER_LOCATION = { TABLE_MARKER_LOCATION,
				LOCATION_PK, LOCATION_PK_ATTR, LOCATION_LAT, LOCATION_LAT_ATTR,
				LOCATION_LONG, LOCATION_LONG_ATTR };
		createTable(CREATE_TABLE_MARKER_LOCATION, db);
		String[] CREATE_TABLE_EVENT_TYPE = { TABLE_EVENT_TYPE, EVENT_TYPE_PK,
				EVENT_TYPE_ATTR, TABLE_ADDITIONAL_INFO, ADDITIONAL_INFO_PK,
				ADDITIONAL_INFO_PK_ATTR, ADDITIONAL_INFO_FK,
				ADDITIONAL_INFO_FK_ATTR };
		createTable(CREATE_TABLE_EVENT_TYPE, db);
		String[] CREATE_TABLE_TABLE_SEVERITY = { TABLE_SEVERITY, SEVERITY_PK,
				SEVERITY_PK_ATTR, SEVERITY_FK, SEVERITY_FK_ATTR };
		createTable(CREATE_TABLE_TABLE_SEVERITY, db);
		String[] CREATE_TABLE_EVENT_REGION = { TABLE_EVENT_REGION,
				EVENT_REGION_PK, EVENT_REGION_PK_ATTR };
		createTable(CREATE_TABLE_EVENT_REGION, db);
		String[] CREATE_TABLE_EVENT = { TABLE_EVENT, EVENT_PK, EVENT_PK_ATTR,
				EVENT_FK1, EVENT_FK1_ATTR, EVENT_FK2, EVENT_FK2_ATTR };
		createTable(CREATE_TABLE_EVENT, db);
		String[] CREATE_TABLE_MARKER = { TABLE_MARKER, MARKER_PK,
				MARKER_PK_ATTR, MARKER_FK1, MARKER_FK1_ATTR, MARKER_FK2,
				MARKER_FK2_ATTR, MARKER_FK3, MARKER_FK3_ATTR };
		createTable(CREATE_TABLE_MARKER, db);
		String[] CREATE_TABLE_MARKER_INFO = { TABLE_MARKER_INFO,
				MARKER_INFO_PK1, MARKER_INFO_PK1_ATTR, MARKER_INFO_FK1_ATTR,
				MARKER_INFO_PK2, MARKER_INFO_PK2_ATTR, MARKER_INFO_FK2_ATTR,
				TABLE_BOUNDARY, BOUNDARY_PK, BOUNDARY_PK_ATTR, BOUNDARY_FK,
				BOUNDARY_FK_ATTR };
		createTable(CREATE_TABLE_MARKER_INFO, db);
	}

	private void createTable(String[] args, SQLiteDatabase db) {
		String create_table = "create table " + args[0] + "( ";
		for (String argument : args) {
			create_table += (argument + " ");
		}
		create_table += ");";
		// db.execSQL(create_table);

	}
}