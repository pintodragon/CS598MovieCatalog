package edu.sunyit.chryslj.ws;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Followed examples on the Amazon webpage for how to sign a request to the
 * amazon web services.
 * 
 * @author Justin Chrysler
 * 
 */
public class AmazonSignedRequestHelper
{
    private static final String SHA_ALGORITHM = "HmacSHA256";
    private static final String REQUEST_URI = "/onca/xml";
    private static final String REQUEST_METHOD = "GET";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private String endpoint;
    private String accessKey;

    private Mac mac = null;

    public AmazonSignedRequestHelper(String endpoint, String accessKey,
                                     String secretKey)
                                                      throws IllegalArgumentException,
                                                      UnsupportedEncodingException,
                                                      NoSuchAlgorithmException,
                                                      InvalidKeyException
    {
        if (null == endpoint || endpoint.length() == 0)
        {
            throw new IllegalArgumentException("endpoint is null or empty");
        }

        if (null == accessKey || accessKey.length() == 0)
        {
            throw new IllegalArgumentException("accessKey is null or empty");
        }

        if (null == secretKey || secretKey.length() == 0)
        {
            throw new IllegalArgumentException("secretKey is null or empty");
        }

        this.endpoint = endpoint;
        this.accessKey = accessKey;
        byte[] secretKeyBytes = secretKey.getBytes("UTF-8");
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(secretKeyBytes, SHA_ALGORITHM);
        mac = Mac.getInstance(SHA_ALGORITHM);
        mac.init(secretKeySpec);
    }

    public String getSignedRequest(Map<String, String> requestParams)
    {
        String signedRequest = "http://" + endpoint + REQUEST_URI + "?";

        requestParams.put("AWSAccessKeyId", accessKey);
        requestParams.put("Timestamp", getTimeStamp());

        // The parameters need to be processed in lexicographical order, so
        // we'll use a TreeMap implementation for that.
        SortedMap<String, String> sortedRequestParams =
                new TreeMap<String, String>(requestParams);
        String normalizeQueryString =
                normalizeRequestParameters(sortedRequestParams);

        String stringToSign =
                REQUEST_METHOD + "\n" + endpoint + "\n" + REQUEST_URI + "\n" +
                        normalizeQueryString;

        String signature = urlEncodeString(computerHMAC(stringToSign));

        signedRequest =
                signedRequest + normalizeQueryString + "&Signature=" +
                        signature;

        return signedRequest;
    }

    private String getTimeStamp()
    {
        String timeStamp = "";

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        timeStamp = dateFormat.format(calendar.getTime());

        return timeStamp;
    }

    private String normalizeRequestParameters(
            SortedMap<String, String> sortedRequestParams)
    {
        StringBuilder normalizeBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> paramIterator =
                sortedRequestParams.entrySet().iterator();

        while (paramIterator.hasNext())
        {
            Map.Entry<String, String> paramEntry = paramIterator.next();
            normalizeBuilder.append(urlEncodeString(paramEntry.getKey()));
            normalizeBuilder.append("=");
            normalizeBuilder.append(urlEncodeString(paramEntry.getValue()));
            if (paramIterator.hasNext())
            {
                normalizeBuilder.append("&");
            }
        }

        return normalizeBuilder.toString();
    }

    private String urlEncodeString(String rawString)
    {
        String encodedString = rawString;

        try
        {
            encodedString = URLEncoder.encode(rawString, "UTF-8");
            encodedString.replace("+", "%20");
            encodedString.replace("*", "%2A");
            encodedString.replace("%7E", "~");
        }
        catch (UnsupportedEncodingException uee)
        {
            // Not able to encode so just use the raw string.
            encodedString = rawString;
        }

        return encodedString;
    }

    private String computerHMAC(String rawString)
    {
        Base64 encoder = new Base64();
        byte[] hmacBytes = mac.doFinal(rawString.getBytes());
        return new String(encoder.encode(hmacBytes));
    }
}
