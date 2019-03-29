package com.example.booksapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.URL;
import java.util.List;

public final class BooksViewModel extends ViewModel {

    private MutableLiveData<List<Book>> books;
    private String stringURL;

    public LiveData<List<Book>> getBooks() {
        if (books == null) {
            books = new MutableLiveData<List<Book>>();
        }
        return books;
    }

    public void searchWebForBooks(String stringURL) { //TODO: Use regular expression for spliting for different texts (or there was something different - check in book).

        stringURL.replace(" ", "+");
        this.stringURL = "https://www.googleapis.com/books/v1/volumes?q="+stringURL+"&maxResults=20&key=AIzaSyAGkJnpI9rwV1YDzxr87ljoQieV50YkoBo";
        //TODO: It's bad solution, however it is ok for now. Better change in future.
        new AsyncTask<String, Void, List<Book>>() {

            @Override
            protected List<Book> doInBackground(String... stringURL) {
                URL url = QueryUtils.createUrl(stringURL[0]); //TODO: Add checker if stringURL[0] exist
                String obj = QueryUtils.makeHttpRequest(url);
                return QueryUtils.extractSearchedBooks(obj);
            }


            @Override
            protected void onPostExecute(List<Book> books) {
                BooksViewModel.this.books.setValue(books);

            }

        }.execute(this.stringURL);
    }

}

