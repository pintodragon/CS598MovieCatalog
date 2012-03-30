package edu.sunyit.chryslj.database;

import android.database.sqlite.SQLiteDatabase;

public class MovieTable implements DatabaseTable
{
    public static final String TABLE_MOVIES = "movies";
    public static final String TABLE_MOVIES_BACKUP = TABLE_MOVIES + "_backup";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RATED = "rated";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_PERSONALRATING = "personalRating";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_RUNTIME = "runTime";

    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_MOVIES +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " + COLUMN_RATED + " INTEGER, " +
            COLUMN_GENRE + " TEXT NOT NULL, " + COLUMN_PERSONALRATING +
            " INTEGER, " + COLUMN_FORMAT + " INTEGER, " + COLUMN_RUNTIME +
            " INTEGER," + " FOREIGN KEY(" + COLUMN_RATED + ") REFERENCES " +
            RatingTable.TABLE_RATINGS + "(" + RatingTable.COLUMN_ID + ")" +
            " FOREIGN KEY(" + COLUMN_FORMAT + ") REFERENCES " +
            MediaFormatTable.TABLE_FORMATS + "(" + MediaFormatTable.COLUMN_ID +
            "));";

    // Used for upgrading the table.
    private static final String CREATE_BACKUP = "CREATE TEMPORARY TABLE " +
            TABLE_MOVIES_BACKUP + " AS SELECT * FROM " + TABLE_MOVIES + ";";

    private static final String DROP_TABLE = "DROP TABLE " + TABLE_MOVIES + ";";

    private static final String COPY_TABLE_BACK = "INSERT INTO " +
            TABLE_MOVIES + " SELECT * FROM " + TABLE_MOVIES_BACKUP + ";";

    @Override
    public String[] getColumnNames()
    {
        return new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_RATED,
                COLUMN_GENRE, COLUMN_PERSONALRATING, COLUMN_FORMAT,
                COLUMN_RUNTIME };
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
