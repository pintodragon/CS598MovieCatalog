package edu.sunyit.chryslj.database;

public class MovieTable
{
	public static final String TABLE_MOVIES = "movies";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_RATED = "rated";
	public static final String COLUMN_GENRE = "genre";
	public static final String COLUMN_PERSONALRATING = "personalRating";
	public static final String COLUMN_FORMAT = "format";
	public static final String COLUMN_RUNTIME = "runTime";

	private static final String DATABASE_CREATE = "CREATE TABLE "
	        + TABLE_MOVIES + "(" + COLUMN_ID
	        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE
	        + " TEXT NOT NULL, " + COLUMN_RATED + " INTEGER, " + COLUMN_GENRE
	        + " TEXT NOT NULL, " + COLUMN_PERSONALRATING + " INTEGER, "
	        + COLUMN_FORMAT + " INTEGER, " + COLUMN_RUNTIME + " INTEGER" + ");";
}
