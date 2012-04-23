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

/**
 * This activity runs the UPCADecorder to obtain the bar code from the image
 * data.
 * 
 * @author Justin Chrysler
 * 
 */
public class BarcodeActivity extends Activity
{
    // This class would make a good candidate for an additional thread.

    private static final String TAG = BarcodeActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
                Toast.makeText(getApplication(), "Decoded Value: " + barcode,
                        Toast.LENGTH_SHORT).show();

                if (!barcode.equals(""))
                {
                    MovieLookup movieLookup = new UPCDatabaseMovieLookup(
                            getResources());
                    Movie movie = movieLookup.lookupMovieByBarcode(barcode);

                    if (movie != null)
                    {
                        MovieLookup movieInfoLookup =
                                new RottenTomatoesMovieLookup(
                                        getResources());
                        movie = movieInfoLookup.gatherMoreInformation(movie);

                        Log.d(TAG,
                                "Movie: " + movie.getTitle() + " Rated: " +
                                        movie.getRated() + " Runtime: " +
                                        movie.getRunTime() + " Genre: " +
                                        movie.getGenre() + " Format: " +
                                        movie.getFormat());

                        // Send our movie to the movie info intent.
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
                            Toast.LENGTH_SHORT);
                }
            }
            catch (InvalidImageException e)
            {
                Log.e(TAG,
                        "Encountered an InvalidImageException. " +
                                e.getMessage());
                Toast.makeText(getApplication(), "Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Encountered an IOException. " + e.getMessage());
                Toast.makeText(getApplication(), "Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }

        setResult(resultCode, returnIntent);
        finish();
    }
}
