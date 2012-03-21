package edu.sunyit.chryslj.database;

import android.database.sqlite.SQLiteDatabase;

public interface DatabaseTable
{
	public String[] getColumnNames();

	public void onCreate(SQLiteDatabase database);

	public void onUpgrade(SQLiteDatabase database, int oldVersion,
	        int newVersion);
}
