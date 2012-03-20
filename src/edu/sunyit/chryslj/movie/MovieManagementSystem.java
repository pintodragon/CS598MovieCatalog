package edu.sunyit.chryslj.movie;

import java.util.Arrays;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import edu.sunyit.chryslj.database.ListMovieAssociationTable;
import edu.sunyit.chryslj.database.ListTable;
import edu.sunyit.chryslj.database.MediaFormatTable;
import edu.sunyit.chryslj.database.MovieDatabaseHelper;
import edu.sunyit.chryslj.database.MovieTable;
import edu.sunyit.chryslj.database.RatingTable;

public class MovieManagementSystem
{
	private SQLiteDatabase database;
	private MovieDatabaseHelper dbHelper;

	private MediaFormatTable mediaFormatTable = new MediaFormatTable();
	private RatingTable ratingTable = new RatingTable();
	private MovieTable movieTable = new MovieTable();
	private ListTable listTable = new ListTable();
	private ListMovieAssociationTable associationTable = new ListMovieAssociationTable();

	private MovieManagementSystem(Context context)
	{
		dbHelper = new MovieDatabaseHelper(context, Arrays.asList(
		        mediaFormatTable, ratingTable, movieTable, listTable,
		        associationTable));
	}

	private Movie getMovieInformation(String title)
	{
		return null;
	}

	private Movie getMovieInformation(int barcode)
	{
		return null;
	}
}
