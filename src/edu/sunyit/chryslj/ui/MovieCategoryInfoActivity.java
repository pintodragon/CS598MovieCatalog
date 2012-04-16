package edu.sunyit.chryslj.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieCategory;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieCategoryInfoActivity extends Activity
{
    private static final String TAG = MovieCategoryInfoActivity.class
            .getSimpleName();

    private MovieManagementSystem movieMangementSystem;
    private MovieCategory movieCategory = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_info);

        movieMangementSystem = new MovieManagementSystem(
                getApplication());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null)
        {
            movieCategory =
                    (MovieCategory) intent
                            .getSerializableExtra(getString(R.string.aquired_category_info));
            if (movieCategory != null)
            {
                String mainTitle =
                        getResources().getString(
                                R.string.category_info_main_title,
                                movieCategory.getTitle());
                ((TextView) findViewById(R.id.category_info_main_title))
                        .setText(mainTitle);
                Log.d(TAG, "Displaying category: " + movieCategory.getTitle());
            }
            else
            {
                Log.e(TAG, "There is no Category to display.");
                Toast.makeText(getApplication(),
                        "There is no Category to display.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     * This method is called when a button on this activity is pressed.
     * 
     * @param view
     */
    public void onButtonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.category_info_add:
                showMovieAddDialog();
                break;
            case R.id.category_info_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.category_info_delete:
                showConfirmDialog();
                break;
            default:
                break;
        }
    }

    /**
     * Delete the movie that is currently being displayed on this view.
     */
    private void deleteCategory()
    {
        movieMangementSystem.open();

        StringBuilder toastMessage = new StringBuilder();
        toastMessage.append(movieCategory.getTitle());

        if (movieMangementSystem.removeCategory(movieCategory))
        {
            toastMessage.append(" has been deleted!");
            Intent returnIntent = new Intent();
            returnIntent.putExtra(getString(R.string.deleted_category_info),
                    movieCategory);
            setResult(RESULT_OK, returnIntent);
        }
        else
        {
            toastMessage.append(" was not deleted!");
            setResult(RESULT_CANCELED);
        }

        Toast.makeText(getApplication(), toastMessage.toString(),
                Toast.LENGTH_LONG).show();

        movieMangementSystem.close();
    }

    /**
     * Show the dialog on whether the category selected should be deleted or
     * not.
     */
    private void showConfirmDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);

        builder.setTitle("Delete " + movieCategory.getTitle() + "?")
                .setMessage(
                        "Are you sure you want to delete \"" +
                                MovieCategoryInfoActivity.this.movieCategory
                                        .getTitle() + "\"?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MovieCategoryInfoActivity.this.deleteCategory();
                        MovieCategoryInfoActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }

    /**
     * Show the dialog to add a Movie to this Category.
     */
    private void showMovieAddDialog()
    {
        movieMangementSystem.open();

        List<Movie> moviesInSystem = movieMangementSystem.getAllMovies();
        List<Movie> moviesNotInCategory = new ArrayList<Movie>();

        for (int movieIndex = 0; movieIndex < moviesInSystem.size(); movieIndex++)
        {
            Movie movie = moviesInSystem.get(movieIndex);
            if (!movieMangementSystem.isMovieInCategory(movie.getTitle(),
                    movieCategory.getTitle()))
            {
                moviesNotInCategory.add(movie);
            }
        }

        movieMangementSystem.close();

        ArrayAdapter<Movie> movieSpinnerAdapter =
                new ArrayAdapter<Movie>(
                        this, android.R.layout.simple_spinner_item,
                        moviesNotInCategory);

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout =
                inflater.inflate(R.layout.category_add_movie_dialog, null);

        // Get the spinner and set some values of it.
        final Spinner movieSpinner =
                (Spinner) layout.findViewById(R.id.category_add_movie_spinner);
        movieSpinner.setAdapter(movieSpinnerAdapter);
        movieSpinner.setPrompt("Please select a Movie");
        TextView emptyView =
                (TextView) layout.findViewById(R.id.category_add_movie_empty);
        movieSpinner.setEmptyView(emptyView);
        Log.d(TAG, "After setup of spinner");

        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);
        builder.setTitle("Movie to add:")
                .setView(layout)
                .setCancelable(true)
                .setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Movie selectedMovie =
                                (Movie) movieSpinner.getSelectedItem();
                        if (selectedMovie != null)
                        {
                            MovieCategoryInfoActivity.this
                                    .addAssociation(selectedMovie);
                        }
                        else
                        {
                            Toast.makeText(
                                    getApplication(),
                                    "There are no movies in the system to select!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                dialog.cancel();
                            }
                        });

        Log.d(TAG, "Before create and show dialog");
        builder.create().show();
    }

    private void addAssociation(Movie movie)
    {
        Log.d(TAG, "Movie selected: " + movie.toString());

        StringBuilder toastText = new StringBuilder();
        toastText.append(movie.getTitle() + " ");

        movieMangementSystem.open();
        if (movieMangementSystem.addMovieToMovieCategory(movie, movieCategory))
        {
            toastText.append(" has been added to " + movieCategory.getTitle());
            // Now that it has been added to a category check and remove the
            // association with the Unsorted category.
            MovieCategory unsortedCat =
                    movieMangementSystem.getCategory("Unsorted");

            // Do not care if it failed or not. A failure probably means it
            // wasn't a part of that category already.
            movieMangementSystem.removeAssociation(movie, unsortedCat);
        }
        else
        {
            toastText.append(" has not been added to " +
                    movieCategory.getTitle());
        }
        movieMangementSystem.close();

        Toast.makeText(getApplication(), toastText.toString(),
                Toast.LENGTH_LONG).show();
    }
}
