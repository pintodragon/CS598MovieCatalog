package edu.sunyit.chryslj.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.util.Log;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.enums.Genre;
import edu.sunyit.chryslj.movie.enums.Rating;

public class RottenTomatoesMovieLookup implements MovieLookup
{
    private static final String TAG = RottenTomatoesMovieLookup.class
            .getSimpleName();
    private String api_key = "&apikey=";
    private String rotten_title_url = "";
    private String rotten_movie_url = "";
    private String page_limit = "page_limit=1";

    public RottenTomatoesMovieLookup(Resources resources) throws IOException
    {
        InputStream rawPropertiesFile = resources.openRawResource(R.raw.rotten);
        Properties properties = new Properties();
        properties.load(rawPropertiesFile);
        api_key = api_key + properties.getProperty("API_KEY", "");
        rotten_title_url = properties.getProperty("MOVIE_TITLE_SEARCH_URL", "");
        rotten_movie_url = properties.getProperty("MOVIE_SEARCH_URL", "");
    }

    @Override
    public Movie lookupMovieByBarcode(String barcode)
    {
        // Not supported by Rotten Tomatoes.
        return null;
    }

    @Override
    public Movie lookupMovieByTitle(String title)
    {
        Movie movie = new Movie();
        movie.setTitle(title);
        String queryParam = "q=" + URLEncoder.encode(title);

        String rottenMovieId = performTitleQuery(queryParam);

        movie = performMovieQuery(movie, rottenMovieId);

        return movie;
    }

    @Override
    public Movie gatherMoreInformation(Movie movie)
    {
        String queryParam = "q=" + URLEncoder.encode(movie.getTitle());

        String rottenMovieId = performTitleQuery(queryParam);

        movie = performMovieQuery(movie, rottenMovieId);

        return movie;
    }

    /**
     * 
     * @param queryParam
     * @return
     */
    private String performTitleQuery(String queryParam)
    {
        String rottenMovieId = null;

        String queryString =
                rotten_title_url + "?" + queryParam + "&" + page_limit + "&" +
                        api_key;
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(
                queryString);

        try
        {
            HttpResponse response = client.execute(httpGet);
            Log.d(TAG, "StatusCode: " +
                    response.getStatusLine().getStatusCode());
            // Check if the request is valid (StatusCode of 200)
            if (response.getStatusLine().getStatusCode() == 200)
            {
                rottenMovieId = parseTitleJSONResponse(response.getEntity());
            }
            else
            {
                Log.e(TAG, "Failed to get file from host: " + rotten_title_url);
            }
        }
        catch (IOException ioe)
        {
            Log.e(TAG, ioe.toString());
        }
        catch (IllegalStateException ise)
        {
            Log.e(TAG, ise.toString());
        }
        catch (JSONException jsone)
        {
            Log.e(TAG, jsone.toString());
        }

        return rottenMovieId;
    }

    /**
     * 
     * @param entity
     * @return
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    private String parseTitleJSONResponse(HttpEntity entity)
            throws IllegalStateException, IOException, JSONException
    {
        String jsonString = convertInputStreamToString(entity.getContent());
        JSONObject replyObject = new JSONObject(
                jsonString);

        if (replyObject.getInt("total") == 0)
        {
            throw new IllegalStateException(
                    "Movie not found.");
        }
        // Because we put a page limit of 1 we should only get one result back.
        // This result tends to be the closest to what we are looking for.
        JSONObject movie = replyObject.getJSONArray("movies").getJSONObject(0);

        return movie.getString("id");
    }

    /**
     * 
     * @param movie
     * @param rottenId
     * @return
     */
    private Movie performMovieQuery(Movie movie, String rottenId)
    {
        String queryString = rotten_movie_url + rottenId + ".json?" + api_key;
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(
                queryString);

        try
        {
            HttpResponse response = client.execute(httpGet);
            Log.d(TAG, "StatusCode: " +
                    response.getStatusLine().getStatusCode());
            // Check if the request is valid (StatusCode of 200)
            if (response.getStatusLine().getStatusCode() == 200)
            {
                movie = parseJSONResponse(movie, response.getEntity());
            }
            else
            {
                Log.e(TAG, "Failed to get file from host: " + rotten_title_url);
            }
        }
        catch (IOException ioe)
        {
            Log.e(TAG, ioe.toString());
        }
        catch (IllegalStateException ise)
        {
            Log.e(TAG, ise.toString());
        }
        catch (JSONException jsone)
        {
            Log.e(TAG, jsone.toString());
        }

        return movie;
    }

    /**
     * 
     * @param movie
     * @param entity
     * @return
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    private Movie parseJSONResponse(Movie movie, HttpEntity entity)
            throws IllegalStateException, IOException, JSONException
    {
        String jsonString = convertInputStreamToString(entity.getContent());
        JSONObject replyObject = new JSONObject(
                jsonString);

        // Check if we have a valid movie or not.
        if (replyObject.has("error"))
        {
            throw new IllegalStateException(
                    "Movie not found.");
        }

        movie = gatherInfoAboutMovie(movie, replyObject);

        return movie;
    }

    private String convertInputStreamToString(InputStream inputStream)
    {
        StringBuilder outputString = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        inputStream));

        try
        {
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null)
            {
                outputString.append(currentLine + "\n");
            }
        }
        catch (IOException ioe)
        {
            Log.e(TAG, "Error reading content: " + ioe);
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException ioe)
            {
                Log.e(TAG, "Unable to close reader: " + ioe);
            }
        }

        return outputString.toString();
    }

    /**
     * Parse through the object gathering the information we can about the
     * movie.
     * 
     * @param movie
     * @param movieObject
     * @return
     */
    private Movie gatherInfoAboutMovie(Movie movie, JSONObject movieObject)
    {
        // Get the movies MPAA rating.
        try
        {
            String mpaaRating = movieObject.getString("mpaa_rating");
            movie.setRated(Rating.getRatingByTitle(mpaaRating));
        }
        catch (JSONException je)
        {
            // Unable to get rating so setting to unrated
            movie.setRated(Rating.UNRATED);
        }
        catch (IllegalArgumentException iae)
        {
            // the mpaaRating returned is unknown. Default.
            movie.setRated(Rating.UNRATED);
        }
        catch (NullPointerException npe)
        {
            // the mpaaRating returned is unknown. Default.
            movie.setRated(Rating.UNRATED);
        }

        // Get the movies run time.
        try
        {
            movie.setRunTime((short) movieObject.getInt("runtime"));
        }
        catch (JSONException je)
        {
            // Unable to find runtime so set the default of 0.
            movie.setRunTime((short) 0);
        }

        // Get the movies genre
        Genre selectedGenre = null;
        try
        {
            // A movie might have multiple genres but for this application we
            // will only store one.
            JSONArray genres = movieObject.getJSONArray("genres");

            for (int index = 0; index < genres.length(); index++)
            {
                try
                {
                    // Just grab the first genre that we match.
                    selectedGenre =
                            Genre.getGenreByTitle(genres.getString(index));
                    if (selectedGenre != null)
                    {
                        break;
                    }
                }
                catch (Exception unknown)
                {
                    // Our Genre enumeration isn't perfect. It doesn't store all
                    // variations of the genre's that are out there. For now
                    // ignore any exceptions other than the JSONException that
                    // we get.
                }
            }

            // See if we matched any of the genre's
            if (selectedGenre == null)
            {
                selectedGenre = Genre.UNKNOWN;
            }
        }
        catch (JSONException je)
        {
            // Error reading the json object returned. Set the default
            // to unknown.
            selectedGenre = Genre.UNKNOWN;
        }
        movie.setGenre(selectedGenre);

        return movie;
    }
}
