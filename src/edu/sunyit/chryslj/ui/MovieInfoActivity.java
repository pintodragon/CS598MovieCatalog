package edu.sunyit.chryslj.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
    public static final int TAKE_PICTURE_REQUEST = 0;

    private MovieManagementSystem movieMangementSystem;

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

        movieMangementSystem = new MovieManagementSystem(
                getApplication());

        titleEditText = (EditText) findViewById(R.id.movie_info_title_text);
        titleEditText.setText("");

        ratedSpinner = (Spinner) findViewById(R.id.movie_info_rating);
        ratedSpinner.setAdapter(new ArrayAdapter<Rating>(
                this, android.R.layout.simple_spinner_item, Rating.values()));

        formatSpinner = (Spinner) findViewById(R.id.movie_info_format);
        formatSpinner.setAdapter(new ArrayAdapter<MediaFormat>(
                this, android.R.layout.simple_spinner_item, MediaFormat
                        .values()));

        genreSpinner = (Spinner) findViewById(R.id.movie_info_genre);
        genreSpinner.setAdapter(new ArrayAdapter<Genre>(
                this, android.R.layout.simple_spinner_item, Genre.values()));

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
            currentMovie =
                    (Movie) intent
                            .getSerializableExtra(getString(R.string.aquired_movie_info));
            if (currentMovie != null)
            {
                Log.d(TAG,
                        "Movie Info: " + currentMovie.getTitle() + " Rated: " +
                                currentMovie.getRated() + " Format: " +
                                currentMovie.getFormat() + " Genre: " +
                                currentMovie.getGenre() + " Personal: " +
                                currentMovie.getPersonalRating() +
                                " RunTime: " + currentMovie.getRunTime());

                titleEditText.setText(currentMovie.getTitle());
                ratedSpinner.setSelection(currentMovie.getRated().getId());
                formatSpinner.setSelection(currentMovie.getFormat().getId());
                genreSpinner.setSelection(currentMovie.getGenre().getId());
                ratingSeekBar.setProgress(currentMovie.getPersonalRating());
                ratingProgressText.setText("" +
                        currentMovie.getPersonalRating());
                runtimeText.setText("" + currentMovie.getRunTime());
            }
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
        // Do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        // Do nothing
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
            case R.id.movie_info_commit:
                addOrUpdateMovie();
                finish();
                break;
            case R.id.movie_info_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.movie_info_delete:
                showConfirmDialog();
                break;
        }
    }

    private void addOrUpdateMovie()
    {
        if (currentMovie == null)
        {
            currentMovie = new Movie();
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

        currentMovie.setPersonalRating(Integer.parseInt(ratingProgressText
                .getText().toString()));

        try
        {
            currentMovie.setRunTime(Short.parseShort(runtimeText.getText()
                    .toString()));
        }
        catch (NumberFormatException nfe)
        {
            Log.e(TAG, "Invalid number entered for runtime.");
            currentMovie.setRunTime((short) 0);
        }

        movieMangementSystem.open();
        StringBuilder toastMessage = new StringBuilder();
        toastMessage.append(currentMovie.getTitle());

        long insertId = movieMangementSystem.addMovie(currentMovie);
        if (insertId != -1)
        {
            currentMovie.setId((int) insertId);
            toastMessage.append(" has been added or updated!");
            Intent returnIntent = new Intent();
            returnIntent.putExtra(getString(R.string.added_movie_info),
                    currentMovie);
            setResult(RESULT_OK, returnIntent);
        }
        else
        {
            toastMessage.append(" was not added or updated!");
            setResult(RESULT_CANCELED);
        }
        movieMangementSystem.close();

        Toast.makeText(getApplication(), toastMessage.toString(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Delete the movie that is currently being displayed on this view.
     */
    private void deleteMovie()
    {
        if (currentMovie != null)
        {
            movieMangementSystem.open();

            StringBuilder toastMessage = new StringBuilder();
            toastMessage.append(currentMovie.getTitle());

            if (movieMangementSystem.removeMovie(currentMovie))
            {
                toastMessage.append(" has been deleted!");
                Intent returnIntent = new Intent();
                returnIntent.putExtra(getString(R.string.deleted_movie_info),
                        currentMovie);
                setResult(RESULT_OK, returnIntent);
            }
            else
            {
                toastMessage.append(" was not deleted!");
                setResult(RESULT_CANCELED);
            }

            Toast.makeText(getApplication(), toastMessage.toString(),
                    Toast.LENGTH_SHORT).show();

            movieMangementSystem.close();
        }
    }

    /**
     * Show the confirm dialog on whether the movie selected should be deleted
     * or not.
     */
    private void showConfirmDialog()
    {
        if (currentMovie != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    this);

            builder.setTitle("Delete " + currentMovie.getTitle() + "?")
                    .setMessage(
                            "Are you sure you want to delete \"" +
                                    MovieInfoActivity.this.currentMovie
                                            .getTitle() + "\"?")
                    .setCancelable(true)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    MovieInfoActivity.this.deleteMovie();
                                    MovieInfoActivity.this.finish();
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
            Toast.makeText(getApplication(),
                    "You can not delete that which does not exist!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
