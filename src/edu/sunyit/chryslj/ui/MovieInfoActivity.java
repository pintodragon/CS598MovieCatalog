package edu.sunyit.chryslj.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.MovieManagementSystem;
import edu.sunyit.chryslj.movie.enums.Genre;
import edu.sunyit.chryslj.movie.enums.MediaFormat;
import edu.sunyit.chryslj.movie.enums.Rating;

public class MovieInfoActivity extends Activity implements
        SeekBar.OnSeekBarChangeListener
{
    private static final String TAG = MovieInfoActivity.class.getSimpleName();

    private Movie currentMovie = null;

    private SeekBar ratingSeekBar;
    private Spinner formatSpinner;
    private Spinner genreSpinner;
    private Spinner ratedSpinner;
    private TextView ratingProgressText;
    private TextView runtimeText;
    private EditText titleEditText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_info);

        titleEditText = (EditText) findViewById(R.id.movie_info_title_text);

        ratingProgressText =
                (TextView) findViewById(R.id.movie_info_rating_progress);
        ratingProgressText.setText("" + 0);

        runtimeText = (TextView) findViewById(R.id.movie_info_runtime_text);

        ratingSeekBar = (SeekBar) findViewById(R.id.movie_info_seek_bar);
        ratingSeekBar.setOnSeekBarChangeListener(this);

        formatSpinner = (Spinner) findViewById(R.id.movie_info_format);
        formatSpinner.setAdapter(new ArrayAdapter<MediaFormat>(this,
                android.R.layout.simple_spinner_item, MediaFormat.values()));

        genreSpinner = (Spinner) findViewById(R.id.movie_info_genre);
        genreSpinner.setAdapter(new ArrayAdapter<Genre>(this,
                android.R.layout.simple_spinner_item, Genre.values()));

        ratedSpinner = (Spinner) findViewById(R.id.movie_info_rating);
        ratedSpinner.setAdapter(new ArrayAdapter<Rating>(this,
                android.R.layout.simple_spinner_item, Rating.values()));

        Log.d(TAG, "TextView: " + ratingProgressText.getId() + " SeekBar: " +
                ratingSeekBar.getId());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch)
    {
        ratingProgressText.setText("" + progress);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null)
        {
            // TODO fill in with stuff
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub
    }

    public void onButtonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.movie_info_commit:
                addOrUpdateMovie();
                break;
            case R.id.movie_info_cancel:
                break;
        }
    }

    private void addOrUpdateMovie()
    {
        boolean isUpdate = true;

        if (currentMovie == null)
        {
            currentMovie = new Movie();
            isUpdate = false;
        }

        currentMovie.setTitle(titleEditText.getText().toString());

        Object format = formatSpinner.getSelectedItem();
        if (format instanceof MediaFormat)
        {
            currentMovie.setFormat((MediaFormat) format);
        }

        Object genre = genreSpinner.getSelectedItem();
        if (genre instanceof Genre)
        {
            currentMovie.setGenre((Genre) genre);
        }

        Object rated = ratedSpinner.getSelectedItem();
        if (rated instanceof Rating)
        {
            currentMovie.setRated((Rating) rated);
        }

        currentMovie.setPersonalRaiting(Integer.parseInt(ratingProgressText
                .getText().toString()));

        currentMovie.setRunTime(Short.parseShort(runtimeText.getText()
                .toString()));

        MovieManagementSystem movieMangementSystem =
                new MovieManagementSystem(getApplication());
        movieMangementSystem.open();

        if (isUpdate)
        {
            if (movieMangementSystem.updateMovie(currentMovie))
            {

            }
            else
            {

            }
        }
        else
        {
            if (movieMangementSystem.addMovie(currentMovie))
            {
                Toast.makeText(this,
                        currentMovie.getTitle() + " has been added!",
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this,
                        currentMovie.getTitle() + " was not added.",
                        Toast.LENGTH_LONG).show();
            }
        }
        movieMangementSystem.close();

    }
}
