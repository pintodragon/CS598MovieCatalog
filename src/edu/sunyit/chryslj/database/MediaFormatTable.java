package edu.sunyit.chryslj.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.sunyit.chryslj.movie.enums.MediaFormat;

public class MediaFormatTable implements DatabaseTable
{
	public static final String TABLE_FORMATS = "mediaFormats";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_NAME = "name";

	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_FORMATS +
	        "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_TITLE +
	        " TEXT NOT NULL, " + COLUMN_NAME + " TEXT NOT NULL);";

	private static final String DROP_TABLE = "DROP TABLE " + TABLE_FORMATS +
	        ";";

	@Override
	public String[] getColumnNames()
	{
		return new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_NAME };
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(TABLE_CREATE);

		insertMediaFormats(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
	        int newVersion)
	{
		// Drop the original
		database.execSQL(DROP_TABLE);

		// Create the new version
		database.execSQL(TABLE_CREATE);

		insertMediaFormats(database);
	}

	private void insertMediaFormats(SQLiteDatabase database)
	{
		try
		{

			for (MediaFormat mediaFormat : MediaFormat.values())
			{
				ContentValues values = new ContentValues();
				values.put(COLUMN_ID, mediaFormat.getId());
				values.put(COLUMN_TITLE, mediaFormat.getTitle());
				values.put(COLUMN_NAME, mediaFormat.getName());
				database.insertOrThrow(TABLE_FORMATS, null, values);
			}
		}
		catch (Exception e)
		{
			Log.e("Error in transaction", e.toString());
		}
	}
}
