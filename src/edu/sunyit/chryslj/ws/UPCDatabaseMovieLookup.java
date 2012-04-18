package edu.sunyit.chryslj.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.content.res.Resources;
import android.util.Log;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.enums.MediaFormat;

public class UPCDatabaseMovieLookup implements MovieLookup
{
    private static final String TAG = UPCDatabaseMovieLookup.class
            .getSimpleName();
    private String rpc_key = "";
    private String rpc_url = "";

    public UPCDatabaseMovieLookup(Resources resources) throws IOException
    {
        InputStream rawPropertiesFile =
                resources.openRawResource(R.raw.upcdatabase);
        Properties properties = new Properties();
        properties.load(rawPropertiesFile);
        rpc_key = properties.getProperty("RPC_KEY", "");
        rpc_url =
                properties.getProperty("RPC_URL",
                        "http://www.upcdatabase.com/xmlrpc");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Movie lookupMovieByBarcode(String barcode)
    {
        Movie movie = null;
        HashMap<String, String> result = null;

        try
        {
            Map<String, String> params = new HashMap<String, String>();
            params.put("rpc_key", rpc_key);
            params.put("upc", barcode);

            XMLRPCClient client = new XMLRPCClient(
                    rpc_url);
            result = (HashMap<String, String>) client.call("lookup", params);
        }
        catch (XMLRPCException xe)
        {
            // The look up failed and we already have a null result so nothing
            // to do here.
        }

        // If the XML RPC call worked then continue to get information about the
        // movie.
        if (result != null)
        {
            try
            {
                // The UPC Database stores title information in the description
                // field.
                movie = new Movie();
                movie.setTitle(result.get("description").toString());
                Log.d(TAG, "Got the title: " + movie.getTitle());
            }
            catch (NullPointerException npe)
            {
                movie = null;
                Log.e(TAG, "NullPointer encountered: " + npe);
            }

            // If we were able to get a title then continue. If not we return
            // null.
            if (movie != null)
            {
                try
                {
                    String formatStr =
                            result.get("size").toString().toLowerCase();
                    Log.d(TAG, "Size string: " + formatStr);

                    // Default to DVD
                    MediaFormat mediaFormat = MediaFormat.DVD;
                    if (formatStr.contains("bd") || formatStr.contains("blu"))
                    {
                        mediaFormat = MediaFormat.BLU_RAY;
                    }
                    else if (formatStr.contains("vhs"))
                    {
                        mediaFormat = MediaFormat.VHS;
                    }

                    movie.setFormat(mediaFormat);
                }
                catch (NullPointerException npe)
                {
                    // Sometimes the UPC Database doesn't always have
                    // information about the format of the movie. Set the
                    // default to DVD.
                    Log.d(TAG, "Default dvd");
                    movie.setFormat(MediaFormat.DVD);
                }
            }
        }

        return movie;
    }

    @Override
    public Movie lookupMovieByTitle(String title)
    {
        // Not used by this class.
        return null;
    }

    @Override
    public Movie gatherMoreInformation(Movie movie)
    {
        // Not used by this class.
        return null;
    }

}
