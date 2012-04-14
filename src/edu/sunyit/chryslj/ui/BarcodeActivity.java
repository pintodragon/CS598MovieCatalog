package edu.sunyit.chryslj.ui;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.barcode.BarcodeProcessor;
import edu.sunyit.chryslj.exceptions.InvalidImageException;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.ws.MovieLookup;
import edu.sunyit.chryslj.ws.RottenTomatoesMovieLookup;
import edu.sunyit.chryslj.ws.UPCDatabaseMovieLookup;

public class BarcodeActivity extends Activity
{
    private static final String TAG = BarcodeActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_test);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        Intent returnIntent = new Intent();
        int resultCode = RESULT_CANCELED;

        if (intent != null)
        {
            // imageData is the YCrCB data acquired from the preview.
            byte[] imageData =
                    intent.getByteArrayExtra(getString(R.string.ycrcb_image_data));
            int width =
                    intent.getIntExtra(getString(R.string.ycrcb_image_width), 0);
            int height =
                    intent.getIntExtra(getString(R.string.ycrcb_image_height),
                            0);

            String barcode;
            try
            {
                barcode =
                        BarcodeProcessor.decodeImage(width, height, imageData);
                Toast.makeText(getApplication(), barcode, Toast.LENGTH_LONG)
                        .show();

                if (!barcode.equals(""))
                {
                    // TODO Also might be a good candidate for a separate thread
                    // with a handler.
                    MovieLookup movieLookup =
                            new UPCDatabaseMovieLookup(getResources());
                    Movie movie = movieLookup.lookupMovieByBarcode(barcode);

                    if (movie != null)
                    {
                        MovieLookup movieInfoLookup =
                                new RottenTomatoesMovieLookup(getResources());
                        movie = movieInfoLookup.gatherMoreInformation(movie);

                        Log.d(TAG, "Movie: " + movie.getTitle() + " Rated: " + 
                                movie.getRated() + " Runtime: " + 
                                movie.getRunTime() + " Genre: " +
                                movie.getGenre() + " Format: " +
                                movie.getFormat());

                        // TODO Need to send each peice of information about the
                        // movie seperately.
                        returnIntent.putExtra(
                                getString(R.string.aquired_movie_info), movie);
                        resultCode = RESULT_OK;
                    }
                    else
                    {
                        Log.d(TAG, "Unable to lookup info for the movie.");
                    }
                }
                else
                {
                    Toast.makeText(getApplication(), "The barcode is invalid.",
                            Toast.LENGTH_LONG);
                }
            }
            catch (InvalidImageException e)
            {
                Toast.makeText(
                        getApplication(),
                        "Encountered an NoSuchAlgorithmException. "
                                + "The Amazon API Key being used is"
                                + " not valid.", Toast.LENGTH_LONG).show();
            }
            catch (IOException e)
            {
                Toast.makeText(
                        getApplication(),
                        "Encountered an NoSuchAlgorithmException. "
                                + "The Amazon API Key being used is"
                                + " not valid.", Toast.LENGTH_LONG).show();
            }
        }

        setResult(resultCode, returnIntent);
        finish();
    }
}
