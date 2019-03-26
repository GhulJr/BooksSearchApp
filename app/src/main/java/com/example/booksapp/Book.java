package com.example.booksapp;

public class Book {
    private String title;
    private String author;
    private String image; //TODO:meybe change to url object

    public Book(String title, String author, String image) {
        this.title = title;
        this.author = author;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
}
