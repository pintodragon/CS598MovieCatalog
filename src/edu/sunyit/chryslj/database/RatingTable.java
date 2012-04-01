package edu.sunyit.chryslj.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.sunyit.chryslj.movie.enums.Rating;

public class RatingTable implements DatabaseTable
{
    public static final String TABLE_RATINGS = "ratings";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_RATINGS +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, " + COLUMN_TITLE +
            " TEXT NOT NULL, " + COLUMN_DESCRIPTION + " TEXT NOT NULL);";

    private static final String DROP_TABLE = "DROP TABLE " + TABLE_RATINGS +
            ";";

    public String[] getColumnNames()
    {
        return new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION };
    }

    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(TABLE_CREATE);

        insertRatings(database);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion)
    {
        // Drop the original
        database.execSQL(DROP_TABLE);

        // Create the new version
        database.execSQL(TABLE_CREATE);

        insertRatings(database);
    }

    private void insertRatings(SQLiteDatabase database)
    {
        try
        {

            for (Rating rating : Rating.values())
            {
                ContentValues values = new ContentValues();
                values.put(COLUMN_ID, rating.getId());
                values.put(COLUMN_TITLE, rating.getTitle());
                values.put(COLUMN_DESCRIPTION, rating.getDescription());
                database.insertOrThrow(TABLE_RATINGS, null, values);
            }
        }
        catch (Exception e)
        {
            Log.e("Error in transaction", e.toString());
        }
    }
}
