package edu.sunyit.chryslj.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * This interface defines methods that are expected for use by our database
 * management system.
 * 
 * @author Justin Chrysler
 * 
 */
public interface DatabaseTable
{
    public String[] getColumnNames();

    public void onCreate(SQLiteDatabase database);

    public void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion);
}
