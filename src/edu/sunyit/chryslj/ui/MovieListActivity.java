package edu.sunyit.chryslj.ui;

import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieManagementSystem;

public class MovieListActivity extends ListActivity implements
        OnItemClickListener, OnItemSelectedListener
{
    private static final String TAG = MovieListActivity.class.getSimpleName();
    private MovieManagementSystem movieManagementSystem;

    private List<Movie> movies = null;
    private MovieAdapter movieAdapter;
    private Spinner sortBySpinner;

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
                this, R.layout.category_list_item, movies);
        setListAdapter(movieAdapter);
        getListView().setOnItemClickListener(this);

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

        movieManagementSystem.open();
        movies = movieManagementSystem.getAllMovies();
        movieManagementSystem.close();

        // By doing this we ensure the newly added movies or deleted movies are
        // added to the list. This would be horrible for a large amount of
        // movies and should probably only have the one movie that was added
        // returned and added to the list.
        movieAdapter.clear();

        for (int index = 0; index < movies.size(); index++)
        {
            movieAdapter.add(movies.get(index));
        }

        movieAdapter.notifyDataSetChanged();

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
                startActivity(intent);
                break;
            case R.id.movie_add_camera:
                intent = new Intent();
                intent.setClass(view.getContext(), CameraPreviewActivity.class);
                startActivityForResult(intent, R.id.TAKE_PICTURE_REQUEST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Log.d(TAG, "Result returned to activity");
            switch (requestCode)
            {
                case R.id.TAKE_PICTURE_REQUEST:
                    startDecodeActivity(data);
                    break;
                case R.id.DECODE_PICTURE:
                    showDecodedMovieInfo(data);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        TextView movieTitle =
                (TextView) view.findViewById(R.id.movie_list_item_title);

        if (movieTitle != null)
        {
            String title =
                    movieTitle.getText().toString().replaceFirst("Title: ", "");
            Log.d(TAG, "Title: " + title);
            movieManagementSystem.open();
            Movie movie = movieManagementSystem.getMovie(title);
            movieManagementSystem.close();

            Intent intent = new Intent();
            intent.putExtra(getString(R.string.aquired_movie_info), movie);
            intent.setClass(getApplication(), MovieInfoActivity.class);
            startActivity(intent);
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
        // TODO Auto-generated method stub

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        // Do nothing.
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
            startActivity(movieInfoIntent);
        }
    }
}
