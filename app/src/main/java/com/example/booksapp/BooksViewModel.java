package com.example.booksapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
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

    //Create a data and returns it, so we can set observer
    public LiveData<List<Book>> getBooks() {
        if (books == null) {
            books = new MutableLiveData<>();
        }
        return books;
    }
    //Is searching web for books, then extract them
    public void searchExtract(String stringURL) {

        //Replace every space with plus, so it can be put directly in URL
        stringURL.replace(" ", "+");
        this.stringURL = "https://www.googleapis.com/books/v1/volumes?q="
                +stringURL+"&maxResults=20&key=AIzaSyAGkJnpI9rwV1YDzxr87ljoQieV50YkoBo";

        //Create AsyncTask that will extract data and execute it
        new AsyncTask<String, Void, List<Book>>() {

            @Override
            //Create string for json, extract data and returns it
            protected List<Book> doInBackground(String... stringURL) {
                URL url = QueryUtils.createUrl(stringURL[0]); //TODO: add checker if URL string is correct
                jsonResponse = QueryUtils.makeHttpRequest(url);
                return QueryUtils.extractSearchedBooks(jsonResponse);
            }

            @Override
            //Pass books to our LiveData, then creates
            //and starts ImageUpdater to download and set books images
            protected void onPostExecute(List<Book> books) {
                BooksViewModel.this.books.setValue(books);
                ImageUpdater updater = new ImageUpdater(books);
                updater.execute();
            }

        }.execute(this.stringURL);
    }

    //TODO: make it public class
    //Inner AsyncTask for downloading images when books data is set
    private class ImageUpdater extends AsyncTask<Void, Void, List<Bitmap>> {

        private List<Book> booksList;

        //Constructor
        public ImageUpdater(List<Book> booksList) {
            this.booksList = booksList;
        }

        //Setter
        public void setBooksList(List <Book> books) { //TODO: make appropriate constructor
            this.booksList = books;

        }

        @Override
        //Extract images from saved json string
        protected List<Bitmap> doInBackground(Void... voids) {
            return QueryUtils.extractImages(jsonResponse);
        }

        @Override
        //Set extracted images to books
        protected void onPostExecute(List<Bitmap> images) {
           for(int i = 0; i < booksList.size(); ++i) {
               booksList.get(i).setImage(images.get(i));
           }
           BooksViewModel.this.books.setValue(booksList); //TODO: why it is important to put it here
        }
    }

}

