package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return an {@link Earthquake} object to represent a single earthquake.
     */

    public static List<Earthquake> fetchEarthquakeDetails(String requestUrl){

        // Code to test whether spinner is working
        /*
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        // Create URL object
        URL url=createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse=null;
        try{
            jsonResponse=makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Extract relevant fields from the JSON response and create an {@link Event} object
        // Return the {@link Event}
        return extractEarthquakes(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try{
            url = new URL(requestUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection conn = null;
        InputStream in =null;
        try{
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if(conn.getResponseCode() == 200){
                in = conn.getInputStream();
                jsonResponse = readFromStream(in);
            }else {
                Log.e(LOG_TAG, "Error response code: " + conn.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }finally {
            if(conn!=null){
                conn.disconnect();
            }
            if(in!=null){
                in.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        if(in !=null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            String line = reader.readLine();
            while (line != null){
                out.append(line);
                line = reader.readLine();
            }
        }
        return out.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Earthquake> extractEarthquakes(String earthquakeJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parses the response given by the SAMPLE_JSON_RESPONSE string and
            // builds up a list of Earthquake objects with the corresponding data.
            JSONObject root =new JSONObject(earthquakeJSON);
            JSONArray features = root.getJSONArray("features");

            if(features.length()>0) {
                for (int i = 0; i < features.length(); i++) {
                    JSONObject features_objects = features.getJSONObject(i);
                    JSONObject properties = features_objects.getJSONObject("properties");
                    double mag = properties.getDouble("mag");
                    String place = properties.getString("place");
                    Long timeInMilliSeconds = properties.getLong("time");
                    String url = properties.getString("url");
                    earthquakes.add(new Earthquake(mag, place, timeInMilliSeconds, url));
                }
                return earthquakes;
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return null;
    }

}