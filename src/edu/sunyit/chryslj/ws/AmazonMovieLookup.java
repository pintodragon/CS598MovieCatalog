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
import android.util.Log;
import edu.sunyit.chryslj.R;
import edu.sunyit.chryslj.movie.Movie;
import edu.sunyit.chryslj.movie.enums.MediaFormat;

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

    public AmazonMovieLookup(Resources resources) throws IOException,
                                                 InvalidKeyException,
                                                 IllegalArgumentException,
                                                 NoSuchAlgorithmException
    {
        InputStream rawPropertiesFile =
                resources.openRawResource(R.raw.awskeys);
        Properties properties = new Properties();
        properties.load(rawPropertiesFile);
        access_key = properties.getProperty(ACCESS_KEY_ID, "");
        secret_key = properties.getProperty(SECRET_ACCESS_KEY, "");
        signingHelper =
                new AmazonSignedRequestHelper(ENDPOINT, access_key, secret_key);
    }

    @Override
    public Movie lookupMovieByBarcode(String barcode)
    {
        Movie movie = null;

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
            Node xmlNode = incomingDoc.getElementsByTagName("Title").item(0);

            movie = new Movie();
            movie.setTitle(xmlNode.getTextContent());

            xmlNode = incomingDoc.getElementsByTagName("ProductGroup").item(0);

            String formatStr = xmlNode.getTextContent().toLowerCase();

            // Default to DVD
            MediaFormat mediaFormat = MediaFormat.DVD;
            if (formatStr.contains("blu"))
            {
                mediaFormat = MediaFormat.BLU_RAY;
            }
            else if (formatStr.contains("vhs"))
            {
                mediaFormat = MediaFormat.VHS;
            }

            movie.setFormat(mediaFormat);
        }
        catch (ParserConfigurationException pce)
        {
            movie = null;
            Log.d(TAG, "Parser exception: " + pce);
        }
        catch (SAXException saxe)
        {
            movie = null;
            Log.d(TAG, "SAX exception: " + saxe);
        }
        catch (IOException ioe)
        {
            movie = null;
            Log.d(TAG, "IO exception: " + ioe);
        }

        Log.d(TAG, "Title is: " + movie.getTitle());

        return movie;
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
