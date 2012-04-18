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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieCategory;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieCategoryInfoActivity extends Activity implements
        AdapterView.OnItemClickListener
{
    private static final String TAG = MovieCategoryInfoActivity.class
            .getSimpleName();

    private MovieManagementSystem movieManagementSystem;
    private MovieCategory movieCategory = null;

    private MovieAdapter moviesInCategory;
    private List<Movie> movies;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_info);

        movieManagementSystem = new MovieManagementSystem(
                getApplication());

        movies = new ArrayList<Movie>();
        moviesInCategory = new MovieAdapter(
                this, R.layout.movie_list_item, movies);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(moviesInCategory);
        listView.setOnItemClickListener(this);
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

                movieManagementSystem.open();
                movies =
                        movieManagementSystem
                                .getAllMoviesInCategory(movieCategory);
                movieManagementSystem.close();

                moviesInCategory.clear();

                for (int index = 0; index < movies.size(); index++)
                {
                    moviesInCategory.add(movies.get(index));
                }

                moviesInCategory.notifyDataSetChanged();
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
                finish();
                break;
            case R.id.category_info_remove:
                showDeleteConfirm();
                break;
            default:
                break;
        }
    }

    /**
     * Show the dialog on whether the category selected should be deleted or
     * not.
     */
    private void showDeleteConfirm()
    {
        final Movie movie = moviesInCategory.getSelectedMovie();

        if (movie != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    this);

            builder.setTitle("Remove " + movie.getTitle() + "?")
                    .setMessage(
                            "Are you sure you want to remove \"" +
                                    movie.getTitle() + "\"?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    MovieCategoryInfoActivity.this
                                            .removeSelectedMovie(movie);
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    dialog.cancel();
                                }
                            });

            builder.create().show();
        }
        else
        {
            Toast.makeText(this, "No movie selected.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    protected void removeSelectedMovie(Movie movie)
    {
        StringBuilder toastText = new StringBuilder();
        toastText.append(movie.getTitle() + " ");

        movieManagementSystem.open();
        if (movieManagementSystem.removeAssociation(movie, movieCategory))
        {
            toastText.append(" was removed from " + movieCategory.getTitle());
        }
        else
        {
            toastText.append(" was not removed from " +
                    movieCategory.getTitle());
        }
        movieManagementSystem.close();

        moviesInCategory.removeSelectedMovie();
        MovieCategoryInfoActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Updating the adapter from UI Thread");
                moviesInCategory.notifyDataSetChanged();
            }
        });

        Toast.makeText(this, toastText.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Show the dialog to add a Movie to this Category.
     */
    private void showMovieAddDialog()
    {
        movieManagementSystem.open();

        List<Movie> moviesInSystem = movieManagementSystem.getAllMovies();
        List<Movie> moviesNotInCategory = new ArrayList<Movie>();

        for (int movieIndex = 0; movieIndex < moviesInSystem.size(); movieIndex++)
        {
            Movie movie = moviesInSystem.get(movieIndex);
            if (!movieManagementSystem.isMovieInCategory(movie.getTitle(),
                    movieCategory.getTitle()))
            {
                moviesNotInCategory.add(movie);
            }
        }

        movieManagementSystem.close();

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

        movieManagementSystem.open();
        if (movieManagementSystem.addMovieToMovieCategory(movie, movieCategory))
        {
            // Add it to the adapter.
            moviesInCategory.add(movie);
            MovieCategoryInfoActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.d(TAG, "Updating the adapter from UI Thread");
                    moviesInCategory.notifyDataSetChanged();
                }
            });

            toastText.append(" has been added to " + movieCategory.getTitle());

            if (!"Unsorted".equals(movieCategory))
            {
                // Now that it has been added to a category check and remove the
                // association with the Unsorted category.
                MovieCategory unsortedCat =
                        movieManagementSystem.getCategory("Unsorted");

                // Do not care if it failed or not. A failure probably means it
                // wasn't a part of that category already.
                movieManagementSystem.removeAssociation(movie, unsortedCat);
            }
        }
        else
        {
            toastText.append(" has not been added to " +
                    movieCategory.getTitle());
        }
        movieManagementSystem.close();

        Toast.makeText(getApplication(), toastText.toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        moviesInCategory.setSelectedIndex(position);
        moviesInCategory.notifyDataSetChanged();
    }
}
