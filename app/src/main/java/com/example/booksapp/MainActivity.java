package com.example.booksapp;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
//TODO: If possible, change all final field (objects used inside internal classes) to use setters.
public class MainActivity extends FragmentActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        editText = findViewById(R.id.editText);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //Setting observer(UI changes)


        final BooksViewModel model = ViewModelProviders.of(this).get(BooksViewModel.class);
        model.getBooks().observeForever(new Observer<List<Book>>() {
            @Override
            public void onChanged(@Nullable List<Book> books) {
                if (mAdapter == null) {
                    mAdapter = new BooksAdapter(books); //TODO:Try edit current adapter rather than creating new one
                    recyclerView.setAdapter(mAdapter);
                }
               mAdapter.notifyDataSetChanged();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mAdapter = null;
                    model.searchWebForBooks(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }
}
