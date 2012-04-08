package edu.sunyit.chryslj.ws;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import edu.sunyit.chryslj.R;

public class AmazonMovieLookup implements MovieLookup
{
    private static final String TAG = AmazonMovieLookup.class.getSimpleName();
    private static final String ACCESS_KEY_ID = "ACCESS_KEY_ID";
    private static final String SECRET_ACCESS_KEY = "SECRET_ACCESS_KEY";

    // Endpoint to use for amazon transactions. Using the US endpoint by
    // default. In a release application this should be configured
    // programatically based on the local of the phone.
    private static final String ENDPOINT = "ecs.amazonaws.com";

    private String access_key = "";
    private String secret_key = "";

    private AmazonSignedRequestHelper signingHelper;

    public AmazonMovieLookup(Resources resources)
    {
        try
        {
            InputStream rawPropertiesFile =
                    resources.openRawResource(R.raw.awskeys);
            Properties properties = new Properties();
            properties.load(rawPropertiesFile);
            access_key = properties.getProperty(ACCESS_KEY_ID, "");
            secret_key = properties.getProperty(SECRET_ACCESS_KEY, "");
            signingHelper =
                    new AmazonSignedRequestHelper(ENDPOINT, access_key,
                            secret_key);
        }
        catch (NotFoundException nfe)
        {

        }
        catch (IOException ioe)
        {

        }
        catch (InvalidKeyException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String lookupMovieByBarcode(String barcode)
    {
        String movieTitle = "";

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("Service", "AWSECommerceService");
        requestParams.put("Version", "2009-03-31");
        requestParams.put("Operation", "ItemLookup");
        requestParams.put("ItemId", barcode);
        requestParams.put("IdType", "UPC");
        requestParams.put("SearchIndex", "All");
        requestParams.put("ResponseGroup", "Small");
        requestParams.put("AssociateTag", "Pintotesttag");

        String requestUrl = signingHelper.getSignedRequest(requestParams);
        Log.d(TAG, "Request is: " + requestUrl);

        DocumentBuilder docBuilder;
        try
        {
            docBuilder =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document incomingDoc = docBuilder.parse(requestUrl);
            Node titleNode = incomingDoc.getElementsByTagName("Title").item(0);
            movieTitle = titleNode.getTextContent();
        }
        catch (ParserConfigurationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "Title is: " + movieTitle);

        return movieTitle;
    }

    @Override
    public String lookupMovieByTitle(String title)
    {
        // TODO Auto-generated method stub

        return null;
    }
}
