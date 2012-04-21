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

/**
 * This is the main class that does all of the interaction with the database. It
 * stores, retrieves and updates information about the movies and categories.
 * 
 * @author Justin Chrysler
 * 
 */
public class MovieManagementSystem
{
    private static final String TAG = MovieManagementSystem.class
            .getSimpleName();

    private SQLiteDatabase database = null;
    private MovieDatabaseHelper dbHelper;

    private MediaFormatTable mediaFormatTable = new MediaFormatTable();
    private RatingTable ratingTable = new RatingTable();
    private GenreTable genreTable = new GenreTable();
    private MovieTable movieTable = new MovieTable();
    private MovieCategoryTable movieCategoryTable = new MovieCategoryTable();
    private CategoryMovieAssociationTable associationTable =
            new CategoryMovieAssociationTable();

    public MovieManagementSystem(Context context)
    {
        dbHelper =
                new MovieDatabaseHelper(
                        context, Arrays.asList(mediaFormatTable, ratingTable,
                                genreTable, movieTable, movieCategoryTable,
                                associationTable));
        Log.d(TAG, "Database helper created for context: " + context);
    }

    /**
     * Get a writable database connection. The reason we do not get a read only
     * version when doing queries is due to the underlying implementation of the
     * dbHelper. It returns the same database reference no matter which choice
     * you make unless there is no more room on the device.
     * 
     * @throws SQLException
     */
    public synchronized void open() throws SQLException
    {
        if (database == null || !database.isOpen())
        {
            database = dbHelper.getWritableDatabase();
        }
    }

    /**
     * Close the connection.
     */
    public synchronized void close()
    {
        if (database.isOpen())
        {
            database.close();
            dbHelper.close();
        }
    }

    /**
     * Add a new movie to the database.
     * 
     * @param newMovie
     *            the movie information to add.
     * @return the index of the newly added movie or -1 if the insert failed.
     */
    public synchronized long addMovie(Movie newMovie)
    {
        long movieId = -1;

        if (!"".equals(newMovie.getTitle()))
        {
            if (getMovie(newMovie.getId()) == null)
            {
                ContentValues values = new ContentValues();
                values.put(MovieTable.COLUMN_TITLE, newMovie.getTitle());
                values.put(MovieTable.COLUMN_RATED, newMovie.getRated().getId());
                values.put(MovieTable.COLUMN_GENRE, newMovie.getGenre().getId());
                values.put(MovieTable.COLUMN_PERSONALRATING,
                        newMovie.getPersonalRating());
                values.put(MovieTable.COLUMN_FORMAT, newMovie.getFormat()
                        .ordinal());
                values.put(MovieTable.COLUMN_RUNTIME, newMovie.getRunTime());

                database.beginTransaction();
                try
                {
                    movieId =
                            database.insertOrThrow(MovieTable.TABLE_MOVIES,
                                    null, values);

                    // In the event the insert doesn't throw like it is suppose
                    // to be.
                    if (movieId == -1)
                    {
                        throw new SQLException();
                    }

                    Log.d(TAG, "InsertedId: " + movieId);
                    database.setTransactionSuccessful();
                    newMovie.setId((int) movieId);
                }
                catch (SQLException sqlException)
                {
                    Log.e(TAG, "Unable to add \"" + newMovie +
                            "\" to the database." + sqlException.getMessage());
                }
                finally
                {
                    database.endTransaction();
                }

                MovieCategory category = getCategory("Unsorted");
                if (!addMovieToMovieCategory(newMovie, category))
                {
                    Log.e(TAG, "Could not add " + newMovie.getTitle() + " to " +
                            category.getTitle() + "!");
                }
            }
            else
            {
                movieId = updateMovie(newMovie);
            }
        }

        return movieId;
    }

    /**
     * Update a movie in the database.
     * 
     * @param movie
     *            the movie we wish to update along with the updated
     *            information.
     * @return the index of the updated movie or -1 if the update failed.
     */
    private synchronized int updateMovie(Movie movie)
    {
        int updateId = -1;

        // Because this movie was retrieved from the movie list we do not know
        // it's ID in the database. Retrieve it so we can use it as the where
        // clause in the update query.
        // movie.setId(getMovie(movie.getTitle()).getId());

        ContentValues values = new ContentValues();
        values.put(MovieTable.COLUMN_ID, movie.getId());
        values.put(MovieTable.COLUMN_TITLE, movie.getTitle());
        values.put(MovieTable.COLUMN_RATED, movie.getRated().getId());
        values.put(MovieTable.COLUMN_GENRE, movie.getGenre().getId());
        values.put(MovieTable.COLUMN_PERSONALRATING, movie.getPersonalRating());
        values.put(MovieTable.COLUMN_FORMAT, movie.getFormat().ordinal());
        values.put(MovieTable.COLUMN_RUNTIME, movie.getRunTime());

        database.beginTransaction();
        try
        {
            String[] selectionArgs = { String.valueOf(movie.getId()) };
            updateId =
                    database.update(MovieTable.TABLE_MOVIES, values,
                            MovieTable.COLUMN_ID + "=?", selectionArgs);

            // In the event the insert doesn't throw like it is suppose
            // to be.
            if (updateId == -1)
            {
                throw new SQLException();
            }

            Log.d(TAG, "InsertedId: " + updateId);
            database.setTransactionSuccessful();
        }
        catch (SQLException sqlException)
        {
            Log.e(TAG,
                    "Unable to update \"" + movie + "\"." +
                            sqlException.getMessage());
        }
        finally
        {
            database.endTransaction();
        }

        return updateId;
    }

    /**
     * Remove a movie from the database.
     * 
     * @param movie
     *            the movie we wish to remove.
     * @return whether or not the movie was removed.
     */
    public synchronized boolean removeMovie(Movie movie)
    {
        boolean movieRemoved = false;

        if (movie.getId() != -1 && !"".equals(movie.getTitle()))
        {
            removeAllAssociation(movie);

            database.beginTransaction();
            try
            {
                Log.d(TAG,
                        "Delete from " + MovieTable.TABLE_MOVIES + " where " +
                                MovieTable.COLUMN_ID + " = " + movie.getId());
                database.delete(MovieTable.TABLE_MOVIES, MovieTable.COLUMN_ID +
                        "=?", new String[] { String.valueOf(movie.getId()) });
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
        }

        return movieRemoved;
    }

    /**
     * Remove all of the associations between the categories in the system and a
     * particular movie.
     * 
     * @param movie
     *            the move to remove the associations of.
     * @return whether or not the associations were removed.
     */
    private synchronized boolean removeAllAssociation(Movie movie)
    {
        boolean associationRemoved = false;

        if (movie.getId() != -1)
        {
            Log.d(TAG, "Removing associations for: " + movie.getId());
            database.beginTransaction();
            try
            {
                String[] selectionArgs =
                        new String[] { String.valueOf(movie.getId()) };
                Log.d(TAG, "Delete from " +
                        CategoryMovieAssociationTable.TABLE_ASSOCIATIONS +
                        " where " +
                        CategoryMovieAssociationTable.COLUMN_MOVIEID + " = " +
                        movie.getId());
                database.delete(
                        CategoryMovieAssociationTable.TABLE_ASSOCIATIONS,
                        CategoryMovieAssociationTable.COLUMN_MOVIEID + "=?",
                        selectionArgs);
                database.setTransactionSuccessful();
                Log.d(TAG, "Removing associations successful");
                associationRemoved = true;
            }
            finally
            {
                Log.d(TAG, "Removing associations finished");
                database.endTransaction();
            }

            if (!associationRemoved)
            {
                Log.e(TAG, "Unable to delete association for " + movie);
            }
        }

        return associationRemoved;
    }

    /**
     * Get a list of all the movies in the system.
     * 
     * @return an ArrayList of all the movies in the system or an empty list if
     *         there aren't any movies.
     */
    public synchronized List<Movie> getAllMovies()
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
     * Get a list of all the movies that are in a category.
     * 
     * @param movieCategory
     *            the category which we want to know the movies associated.
     * @return an ArrayList of all the movies in the category or an empty list
     *         if there aren't any.
     */
    public List<Movie> getAllMoviesInCategory(MovieCategory movieCategory)
    {
        List<Movie> moviesInCategory = new ArrayList<Movie>();

        String movieAlias = "movies";
        String associationAlias = "associations";

        // Inner join to get movie information based on association to Category.
        String rawSql =
                "SELECT " + movieAlias + "." + MovieTable.COLUMN_ID + ", " +
                        movieAlias + "." + MovieTable.COLUMN_TITLE + ", " +
                        movieAlias + "." + MovieTable.COLUMN_RATED + ", " +
                        movieAlias + "." + MovieTable.COLUMN_GENRE + ", " +
                        movieAlias + "." + MovieTable.COLUMN_PERSONALRATING +
                        ", " + movieAlias + "." + MovieTable.COLUMN_FORMAT +
                        ", " + movieAlias + "." + MovieTable.COLUMN_RUNTIME +
                        " FROM " + MovieTable.TABLE_MOVIES + " " + movieAlias +
                        " INNER JOIN " +
                        CategoryMovieAssociationTable.TABLE_ASSOCIATIONS + " " +
                        associationAlias + " ON " + MovieTable.TABLE_MOVIES +
                        "." + MovieTable.COLUMN_ID + "=" + associationAlias +
                        "." + CategoryMovieAssociationTable.COLUMN_MOVIEID +
                        " WHERE " + associationAlias + "." +
                        CategoryMovieAssociationTable.COLUMN_CATEGORYID + "=?";
        String[] selectionArgs = { String.valueOf(movieCategory.getId()) };

        Cursor cursor = database.rawQuery(rawSql, selectionArgs);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Movie movie = cursorToMovie(cursor);
            if (movie != null)
            {
                moviesInCategory.add(movie);
            }

            cursor.moveToNext();
        }

        cursor.close();

        return moviesInCategory;
    }

    /**
     * Get a particular movie based on title.
     * 
     * @param movieTitle
     *            the movies title.
     * @return the movie or null if it doesn't exist.
     */
    public Movie getMovie(String movieTitle)
    {
        String[] selectionArgs = { movieTitle };
        Cursor cursor =
                database.query(MovieTable.TABLE_MOVIES,
                        movieTable.getColumnNames(), MovieTable.COLUMN_TITLE +
                                "=?", selectionArgs, null, null, null);
        cursor.moveToFirst();

        Movie movie = null;

        if (cursor.getCount() == 1)
        {
            movie = cursorToMovie(cursor);
        }

        return movie;
    }

    /**
     * Get a movie based on its primary id key.
     * 
     * @param id
     *            the primary id of the movie.
     * @return the movie or null if it doesn't exist.
     */
    private Movie getMovie(int id)
    {
        String[] selectionArgs = { String.valueOf(id) };
        Cursor cursor =
                database.query(MovieTable.TABLE_MOVIES,
                        movieTable.getColumnNames(), MovieTable.COLUMN_ID +
                                "=?", selectionArgs, null, null, null);
        cursor.moveToFirst();

        Movie movie = null;

        if (cursor.getCount() == 1)
        {
            movie = cursorToMovie(cursor);
        }

        return movie;
    }

    /**
     * Get a category by its title.
     * 
     * @param categoryTitle
     *            the title of the category.
     * @return the Category or null if it doesn't exist.
     */
    public MovieCategory getCategory(String categoryTitle)
    {
        String[] selectionArgs = { categoryTitle };
        Cursor cursor =
                database.query(MovieCategoryTable.TABLE_CATEGORY,
                        movieCategoryTable.getColumnNames(),
                        MovieCategoryTable.COLUMN_TITLE + "=?", selectionArgs,
                        null, null, null);
        cursor.moveToFirst();

        MovieCategory category = null;

        if (cursor.getCount() == 1)
        {
            category = cursorToMovieCategory(cursor);
        }

        return category;
    }

    /**
     * Add a Category to the system.
     * 
     * @param movieCategory
     *            the category to add.
     * @return the categories insert id or -1 if it failed to insert.
     */
    public synchronized long addCategory(MovieCategory movieCategory)
    {
        long insertId = -1;

        if (!"".equals(movieCategory.getTitle()))
        {
            ContentValues values = new ContentValues();
            values.put(MovieCategoryTable.COLUMN_TITLE,
                    movieCategory.getTitle());

            database.beginTransaction();
            try
            {
                insertId =
                        database.insertOrThrow(
                                MovieCategoryTable.TABLE_CATEGORY, null, values);

                // In the event the insert doesn't throw like it is suppose to
                // be.
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
            }
            finally
            {
                database.endTransaction();
            }
        }

        return insertId;
    }

    /**
     * Remove a Category from the system.
     * 
     * @param movieCategory
     *            the Category to remove.
     * @return whether or not the Category was removed.
     */
    public synchronized boolean removeCategory(MovieCategory movieCategory)
    {
        boolean categoryRemoved = false;

        if (movieCategory != null && !"".equals(movieCategory.getTitle()))
        {
            if (!MovieCategoryTable.isDefaultCategory(movieCategory.getTitle()))
            {
                List<Movie> moviesInCategory =
                        getAllMoviesInCategory(movieCategory);
                database.beginTransaction();
                try
                {
                    database.delete(
                            MovieCategoryTable.TABLE_CATEGORY,
                            MovieCategoryTable.COLUMN_ID + "=?",
                            new String[] { String.valueOf(movieCategory.getId()) });
                    database.delete(
                            CategoryMovieAssociationTable.TABLE_ASSOCIATIONS,
                            CategoryMovieAssociationTable.COLUMN_CATEGORYID +
                                    "=?", new String[] { String
                                    .valueOf(movieCategory.getId()) });
                    database.setTransactionSuccessful();
                    categoryRemoved = true;
                }
                finally
                {
                    database.endTransaction();
                }

                if (categoryRemoved)
                {
                    addToUnsortedIfOrphaned(moviesInCategory);
                }
                else
                {
                    Log.e(TAG, "Unable to delete list: " + movieCategory);
                }
            }
            else
            {
                Log.e(TAG, "Tried to delete a default category!");
            }
        }

        return categoryRemoved;
    }

    /**
     * Add the movies from the list to the unsorted category if they are
     * orphaned. AKA if they do not belong to any categories.
     * 
     * @param moviesToCheck
     *            the list of movies to check and update.
     */
    private void addToUnsortedIfOrphaned(List<Movie> moviesToCheck)
    {
        for (int index = 0; index < moviesToCheck.size(); index++)
        {
            addToUnsortedIfOrphaned(moviesToCheck.get(index));
        }
    }

    /**
     * Add a movie to the unsorted category if it is an orphaned. AKA if it does
     * not belong to any categories.
     * 
     * @param moviesToCheck
     *            the movie to check and update.
     */
    private void addToUnsortedIfOrphaned(Movie movieToCheck)
    {
        MovieCategory unsortedCategory = getCategory("Unsorted");

        int count = getNumCategoriesForMovie(movieToCheck.getId());
        Log.d(TAG, "Movie " + movieToCheck.getTitle() + " is in " + count +
                " categories.");

        if (count == 0)
        {
            if (!addMovieToMovieCategory(movieToCheck, unsortedCategory))
            {
                Log.e(TAG, "Could not add " + movieToCheck.getTitle() + " to " +
                        unsortedCategory.getTitle() + "!");
            }
        }
    }

    /**
     * Get all the Categories in the system.
     * 
     * @return an ArrayList of all the Categories in the system or an empty list
     *         if there aren't any.
     */
    public synchronized List<MovieCategory> getAllCategories()
    {
        List<MovieCategory> movieCategorys = new ArrayList<MovieCategory>();
        Cursor cursor =
                database.query(MovieCategoryTable.TABLE_CATEGORY,
                        movieCategoryTable.getColumnNames(), null, null, null,
                        null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            MovieCategory movieCategory = cursorToMovieCategory(cursor);
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
     * Add a movie to a category.
     * 
     * @param movie
     *            the movie to add.
     * @param movieCategory
     *            the Category to add it to.
     * @return whether it was added to the category or not.
     */
    public synchronized boolean addMovieToMovieCategory(Movie movie,
            MovieCategory movieCategory)
    {
        boolean movieAddedToCategory = true;

        if (!isMovieInCategory(movie.getTitle(), movieCategory.getTitle()))
        {
            ContentValues values = new ContentValues();
            values.put(CategoryMovieAssociationTable.COLUMN_CATEGORYID,
                    movieCategory.getId());
            values.put(CategoryMovieAssociationTable.COLUMN_MOVIEID,
                    movie.getId());

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
                Log.e(TAG, "Unable to add \"" + movie + "\" to \"" +
                        movieCategory + "\"." + sqlException.getMessage());
                movieAddedToCategory = false;
            }
            finally
            {
                database.endTransaction();
            }
        }
        else
        {
            Log.e(TAG, "Movie already in the category.");
            movieAddedToCategory = false;
        }

        return movieAddedToCategory;
    }

    /**
     * Remove the association between a movie and a category.
     * 
     * @param movie
     *            the movie.
     * @param movieCategory
     *            the category.
     * @return whether the association was removed or not.
     */
    public synchronized boolean removeAssociation(Movie movie,
            MovieCategory movieCategory)
    {
        boolean associationRemoved = false;

        int count = getNumCategoriesForMovie(movie.getId());

        if ("Unsorted".equals(movieCategory.getTitle()) && count == 1 &&
                isMovieInCategory(movie.getTitle(), movieCategory.getTitle()))
        {
            Log.e(TAG, "Can not remove movie from unsorted if it is the only"
                    + " category the movie belongs too.");
        }
        else if (movie.getId() != -1 && movieCategory.getId() != -1)
        {
            Log.d(TAG, "Removing associations for: " + movie.getTitle() +
                    " and " + movieCategory.getTitle());
            database.beginTransaction();
            try
            {
                String[] selectionArgs =
                        new String[] { String.valueOf(movie.getId()),
                                String.valueOf(movieCategory.getId()) };
                database.delete(
                        CategoryMovieAssociationTable.TABLE_ASSOCIATIONS,
                        CategoryMovieAssociationTable.COLUMN_MOVIEID +
                                "=? AND " +
                                CategoryMovieAssociationTable.COLUMN_CATEGORYID +
                                "=?", selectionArgs);
                database.setTransactionSuccessful();
                Log.d(TAG, "Removing associations successful");
                associationRemoved = true;
            }
            finally
            {
                Log.d(TAG, "Removing associations finished");
                database.endTransaction();
            }

            if (associationRemoved)
            {
                addToUnsortedIfOrphaned(movie);
            }
            else
            {
                Log.e(TAG, "Unable to delete association for " + movie);
            }
        }

        return associationRemoved;
    }

    /**
     * Get the number of movies that belong to a particular Category.
     * 
     * @param categoryId
     *            the ID of the Category.
     * @return the number of movies in the category.
     */
    public synchronized int getNumMoviesInCategory(int categoryId)
    {
        int count = 0;

        Cursor cursorCount =
                database.rawQuery("SELECT COUNT(" +
                        CategoryMovieAssociationTable.COLUMN_CATEGORYID +
                        ") FROM " +
                        CategoryMovieAssociationTable.TABLE_ASSOCIATIONS +
                        " WHERE " +
                        CategoryMovieAssociationTable.COLUMN_CATEGORYID +
                        " = ?", new String[] { String.valueOf(categoryId) });

        cursorCount.moveToFirst();
        count = cursorCount.getInt(0);

        return count;
    }

    /**
     * Get the number of Categories that a Movie belongs to.
     * 
     * @param movieId
     *            the ID of the Movie.
     * @return the number of Categories that a Movie belongs to.
     */
    public synchronized int getNumCategoriesForMovie(int movieId)
    {
        int count = 0;

        Cursor cursorCount =
                database.rawQuery("SELECT COUNT(" +
                        CategoryMovieAssociationTable.COLUMN_MOVIEID +
                        ") FROM " +
                        CategoryMovieAssociationTable.TABLE_ASSOCIATIONS +
                        " WHERE " +
                        CategoryMovieAssociationTable.COLUMN_MOVIEID + " = ?",
                        new String[] { String.valueOf(movieId) });

        cursorCount.moveToFirst();
        count = cursorCount.getInt(0);

        return count;
    }

    /**
     * This method checks the database to see if the movie is already associated
     * with the given category. It uses Strings rather than Objects. This is
     * done to reduce the information needed on the callers side when doing the
     * check.
     * 
     * @param movieTitle
     * @param categoryTitle
     * @return
     */
    public synchronized boolean isMovieInCategory(String movieTitle,
            String categoryTitle)
    {
        boolean isInCategory = false;

        Movie movie = getMovie(movieTitle);
        MovieCategory movieCategory = getCategory(categoryTitle);

        if (movie != null && movieCategory != null)
        {
            String whereString =
                    CategoryMovieAssociationTable.COLUMN_MOVIEID + "=? AND " +
                            CategoryMovieAssociationTable.COLUMN_CATEGORYID +
                            "=?";

            String[] selectionArgs =
                    { String.valueOf(movie.getId()),
                            String.valueOf(movieCategory.getId()) };

            Cursor cursor =
                    database.query(
                            CategoryMovieAssociationTable.TABLE_ASSOCIATIONS,
                            associationTable.getColumnNames(), whereString,
                            selectionArgs, null, null, null);

            // Should be able to test for = 1 but this is safer. A Movie should
            // not be in a category twice.
            isInCategory = cursor.getCount() > 0;
        }

        return isInCategory;
    }

    /**
     * Convert a cursor object to a movie.
     * 
     * @param cursor
     *            cursor result from a database query.
     * @return the Movie that the cursor represents.
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
            movie.setPersonalRating(cursor.getInt(4));
            movie.setFormat(MediaFormat.values()[cursor.getInt(5)]);
            movie.setRunTime(cursor.getShort(6));
            Log.d(TAG, "Movie: " + movie.toString());
        }

        return movie;
    }

    /**
     * Convert a cursor object to a Category.
     * 
     * @param cursor
     *            cursor result from a database query.
     * @return the Category that the cursor represents.
     */
    private MovieCategory cursorToMovieCategory(Cursor cursor)
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
}
