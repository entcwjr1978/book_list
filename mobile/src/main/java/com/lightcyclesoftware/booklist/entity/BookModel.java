package com.lightcyclesoftware.booklist.entity;

public class BookModel {
    private String title;
    private String author;
    private String imageURL;

    public String getTitle() {
        return title;
    }

    public BookModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public BookModel setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getImageURL() {
        return imageURL;
    }

    public BookModel setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }
}
