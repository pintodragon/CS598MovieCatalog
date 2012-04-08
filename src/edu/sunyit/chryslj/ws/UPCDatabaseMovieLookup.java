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

    @Override
    public Movie lookupMovieByBarcode(String barcode)
    {
        Movie movie = null;

        try
        {
            Map<String, String> params = new HashMap<String, String>();
            params.put("rpc_key", rpc_key);
            params.put("upc", barcode);

            XMLRPCClient client = new XMLRPCClient(rpc_url);
            @SuppressWarnings("rawtypes")
            HashMap result = (HashMap) client.call("lookup", params);

            movie = new Movie();
            movie.setTitle(result.get("description").toString());
            String formatStr = result.get("size").toString();

            // Default to DVD
            MediaFormat mediaFormat = MediaFormat.DVD;
            if (formatStr.contains("bd"))
            {
                mediaFormat = MediaFormat.BLU_RAY;
            }
            else if (formatStr.contains("vhs"))
            {
                mediaFormat = MediaFormat.VHS;
            }

            movie.setFormat(mediaFormat);

        }
        catch (NullPointerException nl)
        {
            movie = null;
            Log.d(TAG, "NullPointer encountered: " + nl);
        }
        catch (XMLRPCException e)
        {
            movie = null;
            Log.d(TAG, "XMLRPC exception: " + e);
        }

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
