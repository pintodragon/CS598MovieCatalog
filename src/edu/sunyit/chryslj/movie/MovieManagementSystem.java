package edu.sunyit.chryslj.movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import edu.sunyit.chryslj.database.CategoryMovieAssociationTable;
import edu.sunyit.chryslj.database.GenreTable;
import edu.sunyit.chryslj.database.MediaFormatTable;
import edu.sunyit.chryslj.database.MovieCategoryTable;
import edu.sunyit.chryslj.database.MovieDatabaseHelper;
import edu.sunyit.chryslj.database.MovieTable;
import edu.sunyit.chryslj.database.RatingTable;
import edu.sunyit.chryslj.movie.enums.Genre;
import edu.sunyit.chryslj.movie.enums.MediaFormat;
import edu.sunyit.chryslj.movie.enums.Rating;

public class MovieManagementSystem extends ContentProvider
{
    private static final String TAG = MovieManagementSystem.class
            .getSimpleName();

    private SQLiteDatabase database;
    private MovieDatabaseHelper dbHelper;

    private MediaFormatTable mediaFormatTable = new MediaFormatTable();
    private RatingTable ratingTable = new RatingTable();
    private GenreTable genreTable = new GenreTable();
    private MovieTable movieTable = new MovieTable();
    private MovieCategoryTable movieCategoryTable = new MovieCategoryTable();
    private CategoryMovieAssociationTable associationTable =
            new CategoryMovieAssociationTable();

    @Override
    public boolean onCreate()
    {
        dbHelper =
                new MovieDatabaseHelper(getContext(), Arrays.asList(
                        mediaFormatTable, ratingTable, genreTable, movieTable,
                        movieCategoryTable, associationTable));
        return false;
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
            long insertId =
                    database.insertOrThrow(MovieTable.TABLE_MOVIES, null,
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
        Cursor cursor =
                database.query(MovieTable.TABLE_MOVIES,
                        movieTable.getColumnNames(), null, null, null, null,
                        null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Movie movie = cursorToMovie(cursor);
            if (movie != null)
            {
                movies.add(movie);
            }

            cursor.moveToNext();
        }

        cursor.close();
        return movies;
    }

    /**
     * 
     * @param movieCategory
     * @return
     */
    public boolean addList(MovieCategory movieCategory)
    {
        boolean listAdded = true;

        ContentValues values = new ContentValues();
        values.put(MovieCategoryTable.COLUMN_TITLE, movieCategory.getTitle());

        database.beginTransaction();
        try
        {
            long insertId =
                    database.insertOrThrow(MovieCategoryTable.TABLE_CATEGORY,
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
            Log.e(TAG, "Unable to add \"" + movieCategory +
                    "\" to the database." + sqlException.getMessage());
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
     * @param movieCategory
     * @return
     */
    public boolean removeList(MovieCategory movieCategory)
    {
        boolean ListRemoved = false;

        database.beginTransaction();
        try
        {
            database.delete(
                    edu.sunyit.chryslj.database.MovieCategoryTable.TABLE_CATEGORY,
                    edu.sunyit.chryslj.database.MovieCategoryTable.COLUMN_ID +
                            " = " + movieCategory.getId(), null);
            database.delete(CategoryMovieAssociationTable.TABLE_ASSOCIATIONS,
                    CategoryMovieAssociationTable.COLUMN_CATEGORYID + " = " +
                            movieCategory.getId(), null);
            database.setTransactionSuccessful();
            ListRemoved = true;
        }
        finally
        {
            database.endTransaction();
        }

        if (!ListRemoved)
        {
            Log.e(TAG, "Unable to delete list: " + movieCategory);
        }

        return ListRemoved;
    }

    /**
     * 
     * @param lists
     * @return
     */
    public boolean removeList(List<MovieCategory> lists)
    {
        boolean listsRemoved = true;

        for (MovieCategory list : lists)
        {
            listsRemoved = removeList(list);
        }

        return listsRemoved;
    }

    /**
     * 
     * @return
     */
    public List<MovieCategory> getAllLists()
    {
        List<MovieCategory> movieCategorys = new ArrayList<MovieCategory>();
        Cursor cursor =
                database.query(
                        edu.sunyit.chryslj.database.MovieCategoryTable.TABLE_CATEGORY,
                        movieCategoryTable.getColumnNames(), null, null, null,
                        null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            MovieCategory movieCategory = cursorTomovieCategory(cursor);
            if (movieCategory != null)
            {
                movieCategorys.add(movieCategory);
            }
            cursor.moveToNext();
        }

        cursor.close();
        return movieCategorys;
    }

    /**
     * 
     * @param movie
     * @param movieCategory
     * @return
     */
    public boolean addMovieTomovieCategory(Movie movie,
            MovieCategory movieCategory)
    {
        boolean movieAddedToList = true;

        ContentValues values = new ContentValues();
        values.put(CategoryMovieAssociationTable.COLUMN_CATEGORYID,
                movieCategory.getId());
        values.put(CategoryMovieAssociationTable.COLUMN_MOVIEID, movie.getId());

        database.beginTransaction();

        try
        {
            long insertId =
                    database.insertOrThrow(
                            CategoryMovieAssociationTable.TABLE_ASSOCIATIONS,
                            null, values);

            if (insertId == -1)
            {
                throw new SQLException();
            }

            Log.d(TAG, "InsertedId: " + insertId);
            database.setTransactionSuccessful();
        }
        catch (SQLException sqlException)
        {
            Log.e(TAG, "Unable to add \"" + movie + "\" to \"" + movieCategory +
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
    public String promptForList(List<MovieCategory> currentLists)
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
        Movie movie = null;
        if (cursor.getCount() != 0)
        {
            movie = new Movie();
            movie.setId(cursor.getInt(0));
            movie.setTitle(cursor.getString(1));
            movie.setRated(Rating.values()[cursor.getInt(2)]);
            movie.setGenre(Genre.values()[cursor.getInt(3)]);
            movie.setPersonalRaiting(cursor.getInt(4));
            movie.setFormat(MediaFormat.values()[cursor.getInt(5)]);
            movie.setRunTime(cursor.getShort(6));
            Log.d(TAG, "Movie: " + movie.toString());
        }

        return movie;
    }

    /**
     * 
     * @param cursor
     * @return
     */
    private MovieCategory cursorTomovieCategory(Cursor cursor)
    {
        MovieCategory movieCategory = null;
        if (cursor.getCount() != 0)
        {
            movieCategory = new MovieCategory();
            movieCategory.setId(cursor.getInt(0));
            movieCategory.setTitle(cursor.getString(1));
            Log.d(TAG, "movieCategory: " + movieCategory.toString());
        }

        return movieCategory;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs)
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
