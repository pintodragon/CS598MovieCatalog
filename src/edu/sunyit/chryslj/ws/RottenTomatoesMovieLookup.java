package edu.sunyit.chryslj.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.res.Resources;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;

public class RottenTomatoesMovieLookup implements MovieLookup
{
    private String api_key = "";

    public RottenTomatoesMovieLookup(Resources resources) throws IOException
    {
        InputStream rawPropertiesFile =
                resources.openRawResource(R.raw.upcdatabase);
        Properties properties = new Properties();
        properties.load(rawPropertiesFile);
        api_key = properties.getProperty("API_KEY", "");
    }

    @Override
    public Movie lookupMovieByBarcode(String barcode)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Movie lookupMovieByTitle(String title)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Movie gatherMoreInformation(Movie movie)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
