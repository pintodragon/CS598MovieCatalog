package edu.sunyit.chryslj.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDatabaseHelper extends SQLiteOpenHelper
{
	private static final String LOG_TAG = MovieDatabaseHelper.class.getName();
	private static final String ENABLE_FOREIGN_KEYS = "PRAGMA foreign_keys=ON;";
	private static final String DATABASE_NAME = "csMovieCatalog.db";
	private static final int DATABASE_VERSION = 1;

	public MovieDatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(ENABLE_FOREIGN_KEYS);
		MediaFormatTable.onCreate(database);
		RatingTable.onCreate(database);
		MovieTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
	        int newVersion)
	{
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion +
		        " to " + newVersion + ", data will be preserved.");
		MediaFormatTable.onUpgrade(database, oldVersion, newVersion);
		RatingTable.onUpgrade(database, oldVersion, newVersion);
		MovieTable.onUpgrade(database, oldVersion, newVersion);
	}

}
