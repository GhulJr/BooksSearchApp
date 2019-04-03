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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout linearLayout;

        public MyViewHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
        }
    }

    public BooksAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_item, viewGroup, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        TextView author = myViewHolder.linearLayout.findViewById(R.id.author);
        String authors = formatAuthors(books.get(i).getAuthor());
        author.setText(authors);
        TextView title = myViewHolder.linearLayout.findViewById(R.id.title);
        title.setText(books.get(i).getTitle());
        ImageView image = myViewHolder.linearLayout.findViewById(R.id.my_image);
        image.setImageBitmap(books.get(i).getImage());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    private String formatAuthors( List<String> authors) {
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < authors.size(); ++i) {
            if(i!=0) buffer.append(", ");
            buffer.append(authors.get(i));
        }
        return  buffer.toString();
    }
}
