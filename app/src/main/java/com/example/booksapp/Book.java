package com.example.booksapp;

import java.util.List;

public class Book {
    private String title;
    private List<String> authors;
    private String image; //TODO:meybe change to url object

    public Book(String title, List<String> author, String image) {
        this.title = title;
        this.authors = author;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public  List<String> getAuthor() {
        return authors;
    }
}
