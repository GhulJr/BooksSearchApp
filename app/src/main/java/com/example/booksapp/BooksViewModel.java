package com.example.booksapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;

public final class BooksViewModel extends ViewModel {
    private MutableLiveData<List<Book>> books;
    private String stringURL;
    private String jsonResponse;
    private AsyncTask booksUpdater;
    private ImageUpdater imageUpdater;

    /**
     * Get image updater class
     * @return
     */
    public AsyncTask getImageUpdater() {
        return imageUpdater;
    }
    /**
     * Create a data and returns it, so we can set observer
     * @return books wrapped by LiveData
     */
    public LiveData<List<Book>> getBooks() { //TODO: try change it to list<> type, maybe it will be more efficient (no need of setting new LiveData)
        if (books == null) {
            books = new MutableLiveData<>();
        }
        return books;
    }
    /**
     * Is searching web for books, then extract them
     * @param stringURL
     */
    public void searchExtract(String stringURL) {
        //Replace every space with plus, so it can be put directly in URL
        stringURL.replace(" ", "+");
        this.stringURL = "https://www.googleapis.com/books/v1/volumes?q="
                +stringURL+"&maxResults=20&key=AIzaSyAGkJnpI9rwV1YDzxr87ljoQieV50YkoBo";

        //Create AsyncTask that will extract data and execute it
        booksUpdater = new AsyncTask<String, Void, List<Book>>() {

            @Override
            //Create string for json, extract data and returns it
            protected List<Book> doInBackground(String... stringURL) {
                URL url = QueryUtils.createUrl(stringURL[0]); //TODO: add checker if URL string is correct
                jsonResponse = QueryUtils.makeHttpRequest(url);
                return QueryUtils.extractSearchedBooks(jsonResponse);
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            //Pass books to our LiveData, then creates
            //and starts ImageUpdater to download and set books images
            protected void onPostExecute(List<Book> books) {
                BooksViewModel.this.books.setValue(books);
                imageUpdater = new ImageUpdater(books);
                imageUpdater.execute();
            }

        }.execute(this.stringURL);
    }
    /**
     * TODO: make it public class
     * Inner AsyncTask for downloading images when books data is set
     */
    private class ImageUpdater extends AsyncTask<Void, Integer, List<Bitmap>> { //TODO: make it public and more flexible

        private List<Book> booksList;
        private List<Bitmap> images;

        //Constructor
        public ImageUpdater(List<Book> booksList) {
            this.booksList = booksList;
        }

        @Override
        //Extract images from saved json string
        protected List<Bitmap> doInBackground(Void... voids) {
           images = new ArrayList<>();
            try {//Create objects
                JSONObject root = new JSONObject(jsonResponse);
                JSONArray items = root.optJSONArray("items");
                //Extracting images if exist, add null otherwise
                for(int i = 0; i < items.length(); ++i){
                    if(isCancelled()) break;
                    Bitmap bitmap = QueryUtils.extractSingleImage(items.getJSONObject(i));
                    images.add(bitmap);
                    publishProgress(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                return images;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            booksList.get(values[0]).setImage(images.get(values[0]));
            BooksViewModel.this.books.setValue(booksList);
        }

        @Override
        //Set extracted images to books
        protected void onPostExecute(List<Bitmap> images) {
          /* for(int i = 0; i < booksList.size(); ++i) {
               booksList.get(i).setImage(images.get(i));
           }*/
           BooksViewModel.this.books.setValue(booksList); //TODO: why it is important to put it here
        }
    }
}

