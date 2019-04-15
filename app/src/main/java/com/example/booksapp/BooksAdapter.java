package com.example.booksapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {
    private List<Book> books;
    private OnBookListener onBookListener;
    /**
     * Custom ViewHolder class
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout linearLayout;
        private OnBookListener onBookListener;

        //Constructor setting layout
        public MyViewHolder(LinearLayout v, OnBookListener onBookListener) {
            super(v);
            linearLayout = v;
            this.onBookListener = onBookListener;

            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onBookListener.onBookClick(getAdapterPosition());
        }
    }
    /**
     * TODO: add some other way to change data (creating new adapter is not a good solution)
     * Setting books in adapter
     * @param books
     */
    public BooksAdapter(List<Book> books, OnBookListener onBookListener) {
        this.books = books;
        this.onBookListener = onBookListener;
    }

    @NonNull @Override
    /**
     * Create and return new MyViewHolder instance
     */
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //Create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_item, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v, BooksAdapter.this.onBookListener);
        return vh;
    }

    @Override
    /**
     * Setting whole data to specific views
     */
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        //Display authors
        TextView author = myViewHolder.linearLayout.findViewById(R.id.author);
        String authors = formatAuthors(books.get(i).getAuthor());
        author.setText(authors);
        //Display title
        TextView title = myViewHolder.linearLayout.findViewById(R.id.title);
        title.setText(books.get(i).getTitle());
        //Display image
        ImageView image = myViewHolder.linearLayout.findViewById(R.id.my_image);
        image.setImageBitmap(books.get(i).getImage());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
    /**
     * Used to create String of authors
     * @param authors
     * @return formatted String
     */
    private String formatAuthors( List<String> authors) {
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < authors.size(); ++i) {
            if(i!=0) buffer.append(", ");
            buffer.append(authors.get(i));
        }
        return  buffer.toString();
    }

    public interface OnBookListener {
        void onBookClick(int position);
    }

}
