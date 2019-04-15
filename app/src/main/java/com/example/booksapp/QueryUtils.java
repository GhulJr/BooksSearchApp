package com.example.booksapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Create URL object from String
     * @param url
     * @return URL object
     */
    public static URL createUrl(String url) {
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Failed when creating url.");
        }
        finally {
            return u;
        }
    }
    /**
     * Extract texts from jsonResponse
     * @param jsonResponse
     * @return list of books
     */
    public static List<Book> extractSearchedBooks(String jsonResponse) {
        List<Book> books = new ArrayList<>();
        try {//Create objects
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray items = root.optJSONArray("items");
            //For every single item extract data
            for(int i = 0; i < items.length(); ++i) {
                //Volume info for every item
                JSONObject obj = items.getJSONObject(i).optJSONObject("volumeInfo");
                //Title
                String title = obj.optString("title");
                //Extract authors if exist, otherwise set to "Unknown"
                JSONArray arrayAuthors = extractJSONArray(obj, "authors");
                List<String> authors = new ArrayList<>(); //TODO: use some different than List (if it provides optimalisation
                if(arrayAuthors!=null){
                    for(int j = 0; j < arrayAuthors.length();++j) {
                        String s = arrayAuthors.optString(j);
                        authors.add(s);
                    }
                }
                else {
                    authors.add("Unknown");
                }
                //Create book instance with extracted data
                books.add(new Book(title, authors));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Failed to extract JSONObject");
        }
        //Return list
        return books;
    }
    /**
     * Extract images from jsonResponse
     * @param jsonResponse
     * @return list of bitmaps
     */
    public static List<Bitmap> extractImages(String jsonResponse) {
        List<Bitmap> images = new ArrayList<>();
        try {//Create objects
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray items = root.optJSONArray("items");
            //Extracting images if exist, add null otherwise
            for(int i = 0; i < items.length();++i){
               images.add(extractSingleImage(items.getJSONObject(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return images;
        }
    }
    //TODO
    public static Bitmap extractSingleImage(JSONObject jsonObject) throws IOException {
        JSONObject obj = jsonObject.optJSONObject("volumeInfo");
        JSONObject jsonImage = QueryUtils.extractJSONObject(obj, "imageLinks");
        if (jsonImage != null) {
            String imageHTTP = QueryUtils.extractStringFromJSONObject(jsonImage, "smallThumbnail");
            InputStream in = new URL(imageHTTP).openStream();
            return BitmapFactory.decodeStream(in);
        }
        return null;
    }

    /**
     * Extract jsonResponse from given URL object
     * @param url
     * @return jsonResponse as String
     */
    public static String makeHttpRequest(URL url) {
        String jsonResponse = "";
        //If given url doesn't exist, return empty response
        if(url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try { //Try to establish connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(/*In millis*/10000);
            urlConnection.setConnectTimeout(/*in millis*/15000);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to open URL connection");
        }
        finally { //Clearing connection and stream
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Problem with closing inputStream",e);
                }
            }
        }
        return jsonResponse;
    }
    /**
     * Read data from InputStream
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    /**
     * Extract value of key from obj
     * @param obj
     * @param key
     * @return extracted value
     */
    public static String extractStringFromJSONObject(JSONObject obj, String key) {
        if(obj.has(key)) return obj.optString(key);
        return null;
    }
    /**
     * Extract JSONObjcet of key from obj
     * @param obj
     * @param key
     * @return extracted JSONObject
     */
    public static JSONObject extractJSONObject(JSONObject obj, String key) {
        if(obj.has(key)) return obj.optJSONObject(key);
        return null;
    }
    /**
     * Extract array of key from obj
     * @param obj
     * @param key
     * @return extracted JSONArray
     */
    public static JSONArray extractJSONArray(JSONObject obj, String key) {
        if(obj.has(key)){
            JSONArray array = obj.optJSONArray(key);
            return  array;
        }
        return null;
    }

}
