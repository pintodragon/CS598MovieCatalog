package edu.sunyit.chryslj.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.sunyit.chryslj.movie.enums.Genre;

public class GenreTable implements DatabaseTable
{
	public static final String TABLE_GENRES = "genres";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";

	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_GENRES +
	        "(" + COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, " + COLUMN_TITLE +
	        " TEXT NOT NULL);";

	private static final String DROP_TABLE = "DROP TABLE " + TABLE_GENRES + ";";

	@Override
	public String[] getColumnNames()
	{
		return new String[] { COLUMN_ID, COLUMN_TITLE };
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(TABLE_CREATE);

		insertGenres(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
	        int newVersion)
	{
		// Drop the original
		database.execSQL(DROP_TABLE);

		// Create the new version
		database.execSQL(TABLE_CREATE);

		insertGenres(database);
	}

	private void insertGenres(SQLiteDatabase database)
	{
		try
		{

			for (Genre genre : Genre.values())
			{
				ContentValues values = new ContentValues();
				values.put(COLUMN_ID, genre.getId());
				values.put(COLUMN_TITLE, genre.getTitle());
				database.insertOrThrow(TABLE_GENRES, null, values);
			}
		}
		catch (Exception e)
		{
			Log.e("Error in transaction", e.toString());
		}
	}
}
