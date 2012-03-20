package edu.sunyit.chryslj.database;

import java.util.Arrays;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public class ListMovieAssociationTable implements DatabaseTable
{
	public static final String TABLE_ASSOCIATIONS = "lists";
	public static final String TABLE_ASSOCIATIONS_BACKUP = TABLE_ASSOCIATIONS +
	        "_backup";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MOVIEID = "movie_id";
	public static final String COLUMN_LISTID = "list_id";

	private static final String TABLE_CREATE = "CREATE TABLE " +
	        TABLE_ASSOCIATIONS + "(" + COLUMN_ID +
	        " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MOVIEID +
	        " INTEGER NOT NULL, " + COLUMN_LISTID + " INTEGER NOT NULL," +
	        " FOREIGN KEY(" + COLUMN_MOVIEID + ") REFERENCES " +
	        MovieTable.TABLE_MOVIES + "(" + MovieTable.COLUMN_ID + ")" +
	        " FOREIGN KEY(" + COLUMN_LISTID + ") REFERENCES " +
	        ListTable.TABLE_LISTS + "(" + ListTable.COLUMN_ID + "));";

	// Used for upgrading the table.
	private static final String CREATE_BACKUP = "CREATE TEMPORARY TABLE " +
	        TABLE_ASSOCIATIONS_BACKUP + " AS SELECT * FROM " +
	        TABLE_ASSOCIATIONS + ";";

	private static final String DROP_TABLE = "DROP TABLE " +
	        TABLE_ASSOCIATIONS + ";";

	private static final String COPY_TABLE_BACK = "INSERT INTO " +
	        TABLE_ASSOCIATIONS + " SELECT * FROM " + TABLE_ASSOCIATIONS_BACKUP +
	        ";";

	@Override
	public List<String> getColumnNames()
	{
		return Arrays.asList(COLUMN_ID, COLUMN_MOVIEID, COLUMN_LISTID);
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
