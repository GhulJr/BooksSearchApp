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
                jsonResponse = QueryUtils.makeHttpRequest(url);
                return QueryUtils.extractSearchedBooks(jsonResponse);
            }


            @Override
            protected void onPostExecute(final List<Book> books) {
                BooksViewModel.this.books.setValue(books);
                //TODO: there might be two problems with UI: observer sees only changes done by UI thread or minor change in single List item doesn't affect observer or something else (if the problem is that the observer works only once when data is load
                //TODO: maybe it would not be bad idea to start ExecutorService, but using WebServiceProxy combination witch runOnUiThread!!!!!!! might work as well
                //TODO: learn about InputStream/OutputStream!!!!!!!!!!!!!!!!!
                //TODO: the solution might also be some weird combination of FutureTask, overriding its run method and passing another, force it to make on UiThread
                ImageUpdater updater = new ImageUpdater();
                updater.setBooksList(books);
                updater.execute();
            }

        }.execute(this.stringURL);



    }
    private class ImageUpdater extends AsyncTask<Void, Void, List<Bitmap>> { //TODO: it should be interrupted when searching for another element, change to one image loading at the time

        private List<Book> booksList; //TODO: it is not necessary to hold it, maybe extracting it from LiveData is enough


        public void setBooksList(List <Book> books) { //TODO: make appropriate constructor
            this.booksList = books;

        }

        @Override
        protected List<Bitmap> doInBackground(Void... voids) {
            List<Bitmap> image = new ArrayList<>();
            try { //TODO: put it in QueryUtils
                JSONObject root = new JSONObject(jsonResponse);
                JSONArray items = root.optJSONArray("items");
                for(int i = 0; i < items.length();++i){
                    JSONObject obj = items.getJSONObject(i).optJSONObject("volumeInfo");
                    JSONObject jsonImage = QueryUtils.extractJSONObject(obj, "imageLinks");
                    if(jsonImage!=null) {
                        String imageHTTP = QueryUtils.extractStringFromJSONObject(jsonImage,"smallThumbnail");
                        InputStream in = new URL(imageHTTP).openStream();
                        image.add(BitmapFactory.decodeStream(in));
                    }
                    else{
                        image.add(null);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                return image;
            }
        }

        @Override
        protected void onPostExecute(List<Bitmap> images) {
           for(int i = 0; i < booksList.size(); ++i) {
               booksList.get(i).setImage(images.get(i));
           }
           BooksViewModel.this.books.setValue(booksList); //TODO: why it is important to put it here
        }
    }

}

