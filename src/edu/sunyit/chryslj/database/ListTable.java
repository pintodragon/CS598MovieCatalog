package edu.sunyit.chryslj.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ListTable implements DatabaseTable
{
	public static final String TABLE_LISTS = "lists";
	public static final String TABLE_LISTS_BACKUP = TABLE_LISTS + "_backup";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";

	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_LISTS +
	        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	        COLUMN_TITLE + " TEXT NOT NULL);";

	// Used for upgrading the table.
	private static final String CREATE_BACKUP = "CREATE TEMPORARY TABLE " +
	        TABLE_LISTS_BACKUP + " AS SELECT * FROM " + TABLE_LISTS + ";";

	private static final String DROP_TABLE = "DROP TABLE " + TABLE_LISTS + ";";

	private static final String COPY_TABLE_BACK = "INSERT INTO " + TABLE_LISTS +
	        " SELECT * FROM " + TABLE_LISTS_BACKUP + ";";

	private static final String[][] defaultLists = new String[][] {
	        { "0", "Unsorted" }, { "1", "Wishlist" }, { "2", "Loaned" },
	        { "3", "Borrowing" } };

	public String[] getColumnNames()
	{
		return new String[] { COLUMN_ID, COLUMN_TITLE };
	}

	public void onCreate(SQLiteDatabase database)
	{
		// TODO Add default lists
		database.execSQL(TABLE_CREATE);

		insertDefaultLists(database);
	}

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

	private void insertDefaultLists(SQLiteDatabase database)
	{
		try
		{

			for (String[] listVals : defaultLists)
			{
				ContentValues values = new ContentValues();
				values.put(COLUMN_ID, Integer.parseInt(listVals[0]));
				values.put(COLUMN_TITLE, listVals[1]);
				database.insertOrThrow(TABLE_LISTS, null, values);
			}
		}
		catch (Exception e)
		{
			Log.e("Error in transaction", e.toString());
		}
	}
}