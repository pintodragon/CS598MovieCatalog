package edu.sunyit.chryslj.movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.sunyit.chryslj.database.GenreTable;
import edu.sunyit.chryslj.database.ListMovieAssociationTable;
import edu.sunyit.chryslj.database.ListTable;
import edu.sunyit.chryslj.database.MediaFormatTable;
import edu.sunyit.chryslj.database.MovieDatabaseHelper;
import edu.sunyit.chryslj.database.MovieTable;
import edu.sunyit.chryslj.database.RatingTable;
import edu.sunyit.chryslj.movie.enums.Genre;
import edu.sunyit.chryslj.movie.enums.MediaFormat;
import edu.sunyit.chryslj.movie.enums.Rating;

public class MovieManagementSystem
{
	private static final String TAG = MovieManagementSystem.class
	        .getSimpleName();

	private SQLiteDatabase database;
	private MovieDatabaseHelper dbHelper;

	private MediaFormatTable mediaFormatTable = new MediaFormatTable();
	private RatingTable ratingTable = new RatingTable();
	private GenreTable genreTable = new GenreTable();
	private MovieTable movieTable = new MovieTable();
	private ListTable listTable = new ListTable();
	private ListMovieAssociationTable associationTable = new ListMovieAssociationTable();

	public MovieManagementSystem(Context context)
	{
		dbHelper = new MovieDatabaseHelper(context, Arrays.asList(
		        mediaFormatTable, ratingTable, genreTable, movieTable,
		        listTable, associationTable));
	}

	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}

	public void close()
	{
		dbHelper.close();
	}

	public Movie createMovie(Movie newMovie)
	{
		ContentValues values = new ContentValues();
		values.put(MovieTable.COLUMN_TITLE, newMovie.getTitle());
		values.put(MovieTable.COLUMN_RATED, newMovie.getRated().getId());
		values.put(MovieTable.COLUMN_GENRE, newMovie.getGenre().getId());
		values.put(MovieTable.COLUMN_PERSONALRATING,
		        newMovie.getPersonalRaiting());
		values.put(MovieTable.COLUMN_FORMAT, newMovie.getFormat().ordinal());
		values.put(MovieTable.COLUMN_RUNTIME, newMovie.getRunTime());

		Movie storedMovie = null;
		try
		{
			long insertId = database.insertOrThrow(MovieTable.TABLE_MOVIES,
			        null, values);

			Log.i(TAG, "InsertedId: " + insertId);
			// TODO For testing
			Cursor cursor = database.query(MovieTable.TABLE_MOVIES,
			        movieTable.getColumnNames(), MovieTable.COLUMN_ID + " = " +
			                insertId, null, null, null, null);
			cursor.moveToFirst();
			storedMovie = cursorToMovie(cursor);
			cursor.close();
		}
		catch (SQLException sqlException)
		{
			Log.e(TAG, "SqlException: " + sqlException.getMessage());
		}

		return storedMovie;
	}

	public List<Movie> getAllMovies()
	{
		List<Movie> movies = new ArrayList<Movie>();
		Cursor cursor = database.query(MovieTable.TABLE_MOVIES,
		        movieTable.getColumnNames(), null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast())
		{
			Movie movie = cursorToMovie(cursor);
			movies.add(movie);
			cursor.moveToNext();
		}

		cursor.close();
		return movies;
	}

	// private Movie getMovieInformation(String title)
	// {
	// return null;
	// }
	//
	// private Movie getMovieInformation(int barcode)
	// {
	// return null;
	// }

	private Movie cursorToMovie(Cursor cursor)
	{
		// TODO Magic numbers!!!!
		Movie movie = new Movie();
		movie.setId(cursor.getInt(0));
		movie.setTitle(cursor.getString(1));
		movie.setRated(Rating.values()[cursor.getInt(2)]);
		movie.setGenre(Genre.values()[cursor.getInt(3)]);
		movie.setPersonalRaiting(cursor.getInt(4));
		movie.setFormat(MediaFormat.values()[cursor.getInt(5)]);
		movie.setRunTime(cursor.getShort(6));

		Log.i(TAG, "Movie: " + movie.toString());

		return movie;
	}
}
