package edu.sunyit.chryslj.database;

import java.util.Arrays;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public class MediaFormatTable implements DatabaseTable
{
	public static final String TABLE_FORMATS = "mediaFormats";
	public static final String TABLE_FORMATS_BACKUP = TABLE_FORMATS + "_backup";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_NAME = "name";

	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_FORMATS +
	        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	        COLUMN_TITLE + " TEXT NOT NULL, " + COLUMN_NAME +
	        " TEXT NOT NULL);";

	// Used for upgrading the table.
	private static final String CREATE_BACKUP = "CREATE TEMPORARY TABLE " +
	        TABLE_FORMATS_BACKUP + " AS SELECT * FROM " + TABLE_FORMATS + ";";

	private static final String DROP_TABLE = "DROP TABLE " + TABLE_FORMATS +
	        ";";

	private static final String COPY_TABLE_BACK = "INSERT INTO " +
	        TABLE_FORMATS + " SELECT * FROM " + TABLE_FORMATS_BACKUP + ";";

	@Override
	public List<String> getColumnNames()
	{
		return Arrays.asList(COLUMN_ID, COLUMN_TITLE, COLUMN_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
	        int newVersion)
	{
		// Create a backup of the previous table.
		database.execSQL(CREATE_BACKUP);

		// Drop the original
		database.execSQL(DROP_TABLE);

		// Create the new version
		database.execSQL(TABLE_CREATE);

		// Copy the data back
		database.execSQL(COPY_TABLE_BACK);

	}
}
