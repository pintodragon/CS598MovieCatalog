package edu.sunyit.chryslj.database;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public interface DatabaseTable
{
	public List<String> getColumnNames();

	public void onCreate(SQLiteDatabase database);

	public void onUpgrade(SQLiteDatabase database, int oldVersion,
	        int newVersion);
}
