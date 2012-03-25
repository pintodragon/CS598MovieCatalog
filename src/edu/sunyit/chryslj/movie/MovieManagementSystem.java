package edu.sunyit.chryslj.movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

	/**
	 * 
	 * @param title
	 * @return
	 */
	private Movie aquireMovieInformation(String title)
	{
		// TODO
		return null;
	}

	/**
	 * 
	 * @param barcode
	 * @return
	 */
	private Movie aquireMovieInformation(int barcode)
	{
		// TODO
		return null;
	}

	/**
	 * 
	 * @param newMovie
	 * @return
	 */
	public boolean addMovie(Movie newMovie)
	{
		boolean movieAdded = true;

		ContentValues values = new ContentValues();
		values.put(MovieTable.COLUMN_TITLE, newMovie.getTitle());
		values.put(MovieTable.COLUMN_RATED, newMovie.getRated().getId());
		values.put(MovieTable.COLUMN_GENRE, newMovie.getGenre().getId());
		values.put(MovieTable.COLUMN_PERSONALRATING,
		        newMovie.getPersonalRaiting());
		values.put(MovieTable.COLUMN_FORMAT, newMovie.getFormat().ordinal());
		values.put(MovieTable.COLUMN_RUNTIME, newMovie.getRunTime());

		database.beginTransaction();
		try
		{
			long insertId = database.insertOrThrow(MovieTable.TABLE_MOVIES,
			        null, values);

			// In the event the insert doesn't throw like it is suppose to be.
			if (insertId == -1)
			{
				throw new SQLException();
			}

			Log.d(TAG, "InsertedId: " + insertId);
			database.setTransactionSuccessful();
		}
		catch (SQLException sqlException)
		{
			Log.e(TAG, "Unable to add \"" + newMovie + "\" to the database." +
			        sqlException.getMessage());
			movieAdded = false;
		}
		finally
		{
			database.endTransaction();
		}

		return movieAdded;
	}

	/**
	 * 
	 * @param movie
	 * @return
	 */
	public boolean removeMovie(Movie movie)
	{
		boolean movieRemoved = false;

		database.beginTransaction();
		try
		{
			database.delete(MovieTable.TABLE_MOVIES, MovieTable.COLUMN_ID +
			        " = " + movie.getId(), null);
			database.setTransactionSuccessful();
			movieRemoved = true;
		}
		finally
		{
			database.endTransaction();
		}

		if (!movieRemoved)
		{
			Log.e(TAG, "Unable to delete movie: " + movie);
		}

		return movieRemoved;
	}

	/**
	 * 
	 * @param movies
	 * @return
	 */
	public boolean removeMovie(List<Movie> movies)
	{
		boolean moviesRemoved = true;

		for (Movie movie : movies)
		{
			moviesRemoved = removeMovie(movie);
		}

		return moviesRemoved;
	}

	/**
	 * 
	 * @return
	 */
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

	/**
	 * 
	 * @param movieList
	 * @return
	 */
	public boolean addList(MovieList movieList)
	{
		boolean listAdded = true;

		ContentValues values = new ContentValues();
		values.put(ListTable.COLUMN_TITLE, movieList.getTitle());

		database.beginTransaction();
		try
		{
			long insertId = database.insertOrThrow(ListTable.TABLE_LISTS, null,
			        values);

			// In the event the insert doesn't throw like it is suppose to be.
			if (insertId == -1)
			{
				throw new SQLException();
			}

			Log.d(TAG, "InsertedId: " + insertId);
			database.setTransactionSuccessful();
		}
		catch (SQLException sqlException)
		{
			Log.e(TAG, "Unable to add \"" + movieList + "\" to the database." +
			        sqlException.getMessage());
			listAdded = false;
		}
		finally
		{
			database.endTransaction();
		}

		return listAdded;
	}

	/**
	 * 
	 * @param movieList
	 * @return
	 */
	public boolean removeList(MovieList movieList)
	{
		boolean ListRemoved = false;

		database.beginTransaction();
		try
		{
			database.delete(ListTable.TABLE_LISTS, ListTable.COLUMN_ID + " = " +
			        movieList.getId(), null);
			database.delete(
			        ListMovieAssociationTable.TABLE_ASSOCIATIONS,
			        ListMovieAssociationTable.COLUMN_LISTID + " = " +
			                movieList.getId(), null);
			database.setTransactionSuccessful();
			ListRemoved = true;
		}
		finally
		{
			database.endTransaction();
		}

		if (!ListRemoved)
		{
			Log.e(TAG, "Unable to delete list: " + movieList);
		}

		return ListRemoved;
	}

	/**
	 * 
	 * @param lists
	 * @return
	 */
	public boolean removeList(List<MovieList> lists)
	{
		boolean listsRemoved = true;

		for (MovieList list : lists)
		{
			listsRemoved = removeList(list);
		}

		return listsRemoved;
	}

	/**
	 * 
	 * @return
	 */
	public List<MovieList> getAllLists()
	{
		List<MovieList> movieLists = new ArrayList<MovieList>();
		Cursor cursor = database.query(ListTable.TABLE_LISTS,
		        listTable.getColumnNames(), null, null, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast())
		{
			MovieList movieList = cursorToMovieList(cursor);
			movieLists.add(movieList);
			cursor.moveToNext();
		}

		cursor.close();
		return movieLists;
	}

	/**
	 * 
	 * @param movie
	 * @param movieList
	 * @return
	 */
	public boolean addMovieToMovieList(Movie movie, MovieList movieList)
	{
		boolean movieAddedToList = true;

		ContentValues values = new ContentValues();
		values.put(ListMovieAssociationTable.COLUMN_LISTID, movieList.getId());
		values.put(ListMovieAssociationTable.COLUMN_MOVIEID, movie.getId());

		database.beginTransaction();

		try
		{
			long insertId = database.insertOrThrow(
			        ListMovieAssociationTable.TABLE_ASSOCIATIONS, null, values);

			if (insertId == -1)
			{
				throw new SQLException();
			}

			Log.d(TAG, "InsertedId: " + insertId);
			database.setTransactionSuccessful();
		}
		catch (SQLException sqlException)
		{
			Log.e(TAG, "Unable to add \"" + movie + "\" to \"" + movieList +
			        "\"." + sqlException.getMessage());
			movieAddedToList = false;
		}
		finally
		{
			database.endTransaction();
		}

		return movieAddedToList;
	}

	/**
	 * 
	 * @param currentLists
	 * @return
	 */
	public String promptForList(List<MovieList> currentLists)
	{
		// TODO create the dialog to prompt for a list or create one.
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public Collection<String> getChangesSinceLastSync()
	{
		// TODO
		return null;
	}

	/**
	 * 
	 * @param cursor
	 * @return
	 */
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

		Log.d(TAG, "Movie: " + movie.toString());

		return movie;
	}

	/**
	 * 
	 * @param cursor
	 * @return
	 */
	private MovieList cursorToMovieList(Cursor cursor)
	{
		// TODO Magic numbers!!!!
		MovieList movieList = new MovieList();
		movieList.setId(cursor.getInt(0));
		movieList.setTitle(cursor.getString(1));

		Log.d(TAG, "MovieList: " + movieList.toString());

		return movieList;
	}
}
