package com.example.android.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String WEBTITLE = "webTitle";
    private static final String SECTIONNAME = "sectionName";
    private static final String JSON_KEY_TAGS = "tags";
    private static final String FIELDS = "fields";
    private static final String THUMBNAIL = "thumbnail";
    private static final String WEBPUBLICATIONDATE = "webPublicationDate";
    private static final String WEBURL = "webUrl";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the guardian data set and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> newsList = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}s
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */

    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding News to
        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string

            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject baseJsonResults = baseJsonResponse.getJSONObject(RESPONSE);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or News1).
            JSONArray NewsArray = baseJsonResults.getJSONArray(RESULTS);

            // For each  news in the NewsArray, create an {@link News} object
            for (int i = 0; i < NewsArray.length(); i++) {
                JSONObject currentNews = NewsArray.getJSONObject(i);
                String title = currentNews.getString(WEBTITLE);
                String sectionName = currentNews.getString(SECTIONNAME);
                String url = currentNews.getString(WEBURL);
                // For a given news, if it contains the key called "fields", extract JSONObject
                // associated with the key "fields"
                // If there is the key called "thumbnail", extract the value for the key called "thumbnail"
                JSONObject fieldsObject = currentNews.getJSONObject(FIELDS);
                String thumbnailUrl = fieldsObject.getString(THUMBNAIL);
                Bitmap thumbnail = fetchingImage(thumbnailUrl);

                // This is the time format from guardian JSON "2017-10-29T06:00:20Z"
                // will be changed to 29-10-2017 format
                String date = currentNews.getString(WEBPUBLICATIONDATE);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    Date newDate = format.parse(date);
                    format = new SimpleDateFormat("dd-MM-yyyy" + "\n" + "HH:mm:ss");
                    date = format.format(newDate);
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Problem with parsing the date format");
                }

                String author = null;
                if (currentNews.has(JSON_KEY_TAGS)) {
                    // Extract the JSONArray associated with the key called "tags"
                    JSONArray tagsArray = currentNews.getJSONArray(JSON_KEY_TAGS);
                    if (tagsArray.length() != 0) {
                        // Extract the first JSONObject in the tagsArray
                        JSONObject firstTagsItem = tagsArray.getJSONObject(0);
                        // Extract the value for the key called "webTitle"
                        author = firstTagsItem.getString(WEBTITLE);
                    }
                }

                News news = new News(title, sectionName, author, "Date:" + "\n" + date, url, thumbnail);
                newsList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing JSON results", e);
        }

        // Return the list of news
        return newsList;
    }

    public static Bitmap fetchingImage(String url) {
        URL mUrl = createUrl(url);
        Bitmap mBitmap = null;
        try {
            mBitmap = makeHTTPConnection(mUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Making connection for image", e);
        }
        return mBitmap;
    }

    /**
     * Making a HTTP connection for thumbnails
     */
    public static Bitmap makeHTTPConnection(URL url) throws IOException {

        Bitmap mBitmap = null;

        //Creating Http Connection object and inputstream object
        HttpURLConnection urlConnection;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2500);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == urlConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                BufferedInputStream bInputStream = new BufferedInputStream(inputStream);
                mBitmap = BitmapFactory.decodeStream(bInputStream);
                return mBitmap;
            } else {
                Log.e("LOG_TAG", "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e("LOG_TAG", "Problem retrieving results.", e);
        }

        return mBitmap;
    }
}