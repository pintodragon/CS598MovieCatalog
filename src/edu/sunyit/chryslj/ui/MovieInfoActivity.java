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

    private MovieManagementSystem movieMangementSystem;

    private Movie currentMovie = null;
    private boolean editable = true;

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

        movieMangementSystem = new MovieManagementSystem(getApplication());

        titleEditText = (EditText) findViewById(R.id.movie_info_title_text);
        titleEditText.setText("");

        ratedSpinner = (Spinner) findViewById(R.id.movie_info_rating);
        ratedSpinner.setAdapter(new ArrayAdapter<Rating>(this,
                android.R.layout.simple_spinner_item, Rating.values()));

        formatSpinner = (Spinner) findViewById(R.id.movie_info_format);
        formatSpinner.setAdapter(new ArrayAdapter<MediaFormat>(this,
                android.R.layout.simple_spinner_item, MediaFormat.values()));

        genreSpinner = (Spinner) findViewById(R.id.movie_info_genre);
        genreSpinner.setAdapter(new ArrayAdapter<Genre>(this,
                android.R.layout.simple_spinner_item, Genre.values()));

        ratingSeekBar = (SeekBar) findViewById(R.id.movie_info_seek_bar);
        ratingSeekBar.setOnSeekBarChangeListener(this);
        ratingSeekBar.setProgress(0);

        ratingProgressText =
                (TextView) findViewById(R.id.movie_info_rating_progress);
        ratingProgressText.setText("" + 0);

        runtimeText = (TextView) findViewById(R.id.movie_info_runtime_text);
        runtimeText.setText("" + 0);

        Log.d(TAG, "TextView: " + ratingProgressText.getId() + " SeekBar: " +
                ratingSeekBar.getId());

        editable = true;
        updateEditable();
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
            String movieTitle =
                    intent.getStringExtra(getResources().getString(
                            R.string.movie_info_intent_title));
            if (movieTitle != null)
            {
                movieMangementSystem.open();
                currentMovie = movieMangementSystem.getMovie(movieTitle);

                titleEditText.setText(currentMovie.getTitle());
                ratedSpinner.setSelection(currentMovie.getRated().getId());
                formatSpinner.setSelection(currentMovie.getFormat().getId());
                genreSpinner.setSelection(currentMovie.getGenre().getId());
                ratingSeekBar.setProgress(currentMovie.getPersonalRaiting());
                ratingProgressText.setText("" +
                        currentMovie.getPersonalRaiting());
                runtimeText.setText("" + currentMovie.getRunTime());

                editable = false;
                updateEditable();
            }
            else
            {
                editable = true;
                updateEditable();
            }
        }
        else
        {
            editable = true;
            updateEditable();
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
                finish();
                break;
            case R.id.movie_info_cancel:
                finish();
                break;
            case R.id.movie_info_edit:
                editable = true;
                updateEditable();
                break;
        }
    }

    private void updateEditable()
    {
        titleEditText.setEnabled(editable);
        ratedSpinner.setEnabled(editable);
        formatSpinner.setEnabled(editable);
        genreSpinner.setEnabled(editable);
        ratingSeekBar.setEnabled(editable);
        ratingProgressText.setEnabled(editable);
        runtimeText.setEnabled(editable);

        if (editable)
        {
            findViewById(R.id.movie_info_edit).setVisibility(View.INVISIBLE);
        }
        else
        {
            findViewById(R.id.movie_info_edit).setVisibility(View.INVISIBLE);
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

        movieMangementSystem.open();

        if (isUpdate)
        {
            if (movieMangementSystem.updateMovie(currentMovie))
            {
                Toast.makeText(getApplication(),
                        currentMovie.getTitle() + " has been updated!",
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplication(),
                        currentMovie.getTitle() + " was not updated.",
                        Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            if (movieMangementSystem.addMovie(currentMovie))
            {
                Toast.makeText(getApplication(),
                        currentMovie.getTitle() + " has been added!",
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplication(),
                        currentMovie.getTitle() + " was not added.",
                        Toast.LENGTH_LONG).show();
            }
        }
        movieMangementSystem.close();

    }
}
