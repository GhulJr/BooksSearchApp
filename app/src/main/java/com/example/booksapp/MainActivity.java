package com.example.booksapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.List;
//TODO: If possible, change all final field (objects used inside internal classes) to use setters.
public class MainActivity extends FragmentActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView editText;
    private BooksViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * Create RecyclerView
         */
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        editText = findViewById(R.id.editText);
        /**
         *  Use a linear layout manager
         */
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        /**
         * Setting observer(UI changes)
         */
        model = ViewModelProviders.of(this).get(BooksViewModel.class);
        model.getBooks().observeForever(new Observer<List<Book>>() {
            @Override
            public void onChanged(@Nullable List<Book> books) {
                if (mAdapter == null) {
                    mAdapter = new BooksAdapter(books, new BooksAdapter.OnBookListener() {
                        @Override
                        public void onBookClick(int position) {
                            model.getBooks().getValue().get(position);
                            Intent intent = new Intent(MainActivity.this, BookDetails.class);
                            Bitmap image = model.getBooks().getValue().get(position).getImage();
                            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                            byte[] byteArray = bStream.toByteArray();
                            intent.putExtra("image", byteArray);
                            startActivity(intent);
                        }
                    }); //TODO:Try edit current adapter rather than creating new one
                    recyclerView.setAdapter(mAdapter);
                }
               mAdapter.notifyDataSetChanged();
            }
        });
        /**
         * Enable searching for books when searchButton is clicked
         */
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mAdapter = null;
                    AsyncTask task = model.getImageUpdater();
                    if(task != null) task.cancel(true);
                    model.searchExtract(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

}
