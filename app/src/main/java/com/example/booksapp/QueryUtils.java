package com.example.booksapp;

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

    public static List<Book> extractSearchedBooks(String jsonResponse) {
       List<Book> books = new ArrayList<>();


        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray items = root.optJSONArray("items");

            for(int i = 0; i < items.length(); ++i) {
                JSONObject obj = items.getJSONObject(i).optJSONObject("volumeInfo"); //TODO: pictures are stored somewhere else
                String title = obj.optString("title");
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

                books.add(new Book(title, authors,null));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Failed to extract JSONObject");
        }

        return books;
    }

    //TODO: Finish this.
    public static List<Book> extractBookInfo(URL url) {
        List<Book> books = new ArrayList<>();


        return books;
    }


    public static String makeHttpRequest(URL url) {
        String jsonResponse = "";

        if(url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
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

    public static String extractStringFromJSONObject(JSONObject obj, String key) {
        if(obj.has(key)) return obj.optString(key);
        return null; //TODO: return string resource
    }

    public static JSONObject extractJSONObject(JSONObject obj, String key) {
        if(obj.has(key)) return obj.optJSONObject(key);
        return null; //TODO: return string resource
    }

    private static JSONArray extractJSONArray(JSONObject obj, String key) {
        if(obj.has(key)){
            JSONArray array = obj.optJSONArray(key);
            return  array;
        }
        return null;
    }

}
