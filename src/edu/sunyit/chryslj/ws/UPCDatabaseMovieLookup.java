package edu.sunyit.chryslj.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import edu.sunyit.chryslj.R;

public class UPCDatabaseMovieLookup implements MovieLookup
{
    private static final String TAG = UPCDatabaseMovieLookup.class
            .getSimpleName();
    private String rpc_key = "";
    private String rpc_url = "";

    public UPCDatabaseMovieLookup(Resources resources)
    {
        try
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
        catch (NotFoundException nfe)
        {

        }
        catch (IOException ioe)
        {

        }
    }

    @Override
    public String lookupMovieByBarcode(String barcode)
    {
        try
        {
            Map<String, String> params = new HashMap<String, String>();
            params.put("rpc_key", rpc_key);
            params.put("upc", barcode);

            XMLRPCClient client = new XMLRPCClient(rpc_url);
            @SuppressWarnings("rawtypes")
            HashMap result = (HashMap) client.call("lookup", params);

            for (Object key : result.keySet())
            {
                Log.d(TAG, "Key: " + key + " Value: " + result.get(key));
            }

            String resultSize = result.get("size").toString();
            String resultDesc = result.get("description").toString();

        }
        catch (NullPointerException nl)
        {

        }
        catch (XMLRPCException e)
        {
        }

        return null;
    }

    @Override
    public String lookupMovieByTitle(String title)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
