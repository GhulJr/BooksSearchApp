package com.example.booksapp;

import android.graphics.Bitmap;

import java.util.List;

public class Book {
    private String title;
    private List<String> authors;
    private Bitmap image;
    /**
     * Constructors
     */
    public Book(String title, List<String> authors) {
        this.title = title;
        this.authors = authors;
        this.image = null;
    }

    public Book(String title, List<String> author, Bitmap image) {
        this.title = title;
        this.authors = author;
        this.image = image;
    }
    /**
     * Getters
     */
    public String getTitle() {
        return title;
    }

    public  List<String> getAuthor() {
        return authors;
    }

    public Bitmap getImage() {
        return image;
    }
    /**
     * Setters
     */
    public void setImage(Bitmap bitmap) {
        image = bitmap;
    }
}
