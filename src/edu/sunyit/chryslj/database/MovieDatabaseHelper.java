package edu.sunyit.chryslj.database;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class is a helper class that handles the creation and upgrade of the
 * database tables.
 * 
 * @author Justin Chrysler
 * 
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper
{
    private static final String LOG_TAG = MovieDatabaseHelper.class.getName();
    private static final String ENABLE_FOREIGN_KEYS = "PRAGMA foreign_keys=ON;";
    private static final String DATABASE_NAME = "csMovieCatalog.db";
    private static final int DATABASE_VERSION = 1;

    List<DatabaseTable> tables;

    public MovieDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MovieDatabaseHelper(Context context, List<DatabaseTable> tables)
    {
        this(context);
        this.tables = tables;
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(ENABLE_FOREIGN_KEYS);
        for (DatabaseTable table : tables)
        {
            table.onCreate(database);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion)
    {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", data will be preserved.");
        for (DatabaseTable table : tables)
        {
            table.onUpgrade(database, oldVersion, newVersion);
        }
    }

}
