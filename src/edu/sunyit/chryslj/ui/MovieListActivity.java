package edu.sunyit.chryslj.ui;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieComparator;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

/**
 * This Activity displays a list of Movies to the user. This list contains all
 * the movies currently in the system. It does offer a spinner at the top for a
 * user to select how they want the movies sorted. A user can also add a new
 * Movie manually or via the camera.
 * 
 * @author Justin Chrysler
 * 
 */
public class MovieListActivity extends ListActivity implements
        OnItemClickListener, OnItemSelectedListener
{
    private static final String TAG = MovieListActivity.class.getSimpleName();
    private MovieManagementSystem movieManagementSystem;

    private List<Movie> movies = null;
    private MovieAdapter movieAdapter;
    private Spinner sortBySpinner;

    private MovieComparator movieComparator = new MovieComparator();
    private String[] spinnerValues = { "None", "Title", "Rated",
            "Personal Rating", "Genre", "Format", "Runtime" };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list);

        // Connect to the movie management system.
        movieManagementSystem = new MovieManagementSystem(
                getApplication());

        movieManagementSystem.open();
        movies = movieManagementSystem.getAllMovies();
        movieManagementSystem.close();

        movieAdapter = new MovieAdapter(
                this, R.layout.movie_list_item, movies);
        movieAdapter.setColorHightlighted(false);
        setListAdapter(movieAdapter);
        getListView().setOnItemClickListener(this);

        updateView();

        sortBySpinner = (Spinner) findViewById(R.id.movie_list_sort_spinner);
        ArrayAdapter<String> movieSpinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerValues);

        sortBySpinner.setAdapter(movieSpinnerAdapter);
        sortBySpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        updateView();
    }

    /**
     * Our button handler. When a user clicks on the manual or camera add
     * buttons this method is called.
     * 
     * @param view
     */
    public void onButtonClick(View view)
    {
        Log.d(TAG, "View: " + view.getId());
        switch (view.getId())
        {
            case R.id.movie_add_manual:
                Intent intent = new Intent();
                intent.setClass(view.getContext(), MovieInfoActivity.class);
                startActivityForResult(intent, R.id.MOVIE_INFO);
                break;
            case R.id.movie_add_camera:
                if (isNetworkAvailable())
                {
                    intent = new Intent();
                    intent.setClass(view.getContext(),
                            CameraPreviewActivity.class);
                    startActivityForResult(intent, R.id.TAKE_PICTURE_REQUEST);
                }
                else
                {
                    Toast.makeText(
                            getApplication(),
                            "No network connection. Unable to look up movie"
                                    + " information.", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case R.id.TAKE_PICTURE_REQUEST:
                    startDecodeActivity(data);
                    break;
                case R.id.DECODE_PICTURE:
                    showDecodedMovieInfo(data);
                    break;
                case R.id.MOVIE_INFO:
                    Movie addedMovie =
                            (Movie) data
                                    .getSerializableExtra(getString(R.string.added_movie_info));
                    Movie deletedMovie =
                            (Movie) data
                                    .getSerializableExtra(getString(R.string.deleted_movie_info));

                    if (deletedMovie != null && addedMovie == null)
                    {
                        movieAdapter.removeSelectedMovie();
                    }

                    if (addedMovie != null && deletedMovie == null)
                    {
                        if (!movieAdapter.hasMovie(addedMovie))
                        {
                            movieAdapter.add(addedMovie);
                        }
                        else
                        {
                            movieAdapter.updateMovie(addedMovie);
                        }
                    }
                default:
                    break;
            }

            // Update the view. No movies display no movies otherwise display
            // list.
            updateView();

            // Notify the system that the data may have changed. If it hasn't
            // this does nothing.
            MovieListActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    movieAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        movieAdapter.setSelectedIndex(position);
        TextView movieTitle =
                (TextView) view.findViewById(R.id.movie_list_item_title_val);

        if (movieTitle != null)
        {
            String title = movieTitle.getText().toString();
            Log.d(TAG, "Title: " + title);
            movieManagementSystem.open();
            Movie movie = movieManagementSystem.getMovie(title);
            movieManagementSystem.close();

            Intent intent = new Intent();
            intent.putExtra(getString(R.string.aquired_movie_info), movie);
            intent.setClass(getApplication(), MovieInfoActivity.class);
            startActivityForResult(intent, R.id.MOVIE_INFO);
        }
    }

    private void updateView()
    {
        Log.d(TAG, "Update called");
        if (movies.size() > 0)
        {
            findViewById(R.id.movie_list_container).setVisibility(View.VISIBLE);
            findViewById(R.id.movie_list_empty_textview).setVisibility(
                    View.INVISIBLE);
        }
        else
        {
            findViewById(R.id.movie_list_container).setVisibility(
                    View.INVISIBLE);
            findViewById(R.id.movie_list_empty_textview).setVisibility(
                    View.VISIBLE);
        }
    }

    private void startDecodeActivity(Intent data)
    {
        // imageData is the YCrCB data acquired from the preview.
        byte[] imageData =
                data.getByteArrayExtra(getString(R.string.ycrcb_image_data));
        int width = data.getIntExtra(getString(R.string.ycrcb_image_width), 0);
        int height =
                data.getIntExtra(getString(R.string.ycrcb_image_height), 0);

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setClass(getApplication(), BarcodeActivity.class);
        intent.putExtra(getString(R.string.ycrcb_image_data), imageData);
        intent.putExtra(getString(R.string.ycrcb_image_width), width);
        intent.putExtra(getString(R.string.ycrcb_image_height), height);
        startActivityForResult(intent, R.id.DECODE_PICTURE);
    }

    private void showDecodedMovieInfo(Intent data)
    {
        Movie aquiredMovie =
                (Movie) data
                        .getSerializableExtra(getString(R.string.aquired_movie_info));
        if (aquiredMovie != null)
        {
            Intent movieInfoIntent = new Intent();
            movieInfoIntent.putExtra(getString(R.string.aquired_movie_info),
                    aquiredMovie);
            movieInfoIntent.setClass(getApplication(), MovieInfoActivity.class);
            startActivityForResult(movieInfoIntent, R.id.MOVIE_INFO);
        }
    }

    // //////////////////////
    // Used by the Spinner //
    // //////////////////////

    /**
     * 
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id)
    {
        TextView spinnerText = (TextView) view;
        String sortedBy = spinnerText.getText().toString();

        movieAdapter.setSortedBy(sortedBy);

        if (!"None".equals(sortedBy))
        {
            movieComparator.setCompareKey(sortedBy);

            Collections.sort(movies, movieComparator);
        }

        MovieListActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                movieAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        // Do nothing.
    }

    /**
     * Check to see if we have a network connection.
     * 
     * @return true if there is a network connection.
     */
    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =
                connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
