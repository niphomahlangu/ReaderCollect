package com.example.readercollect;

public class Book {
    String BookName, ImageUri;

    public Book() {
    }

    public Book(String bookName, String imageUri) {
        BookName = bookName;
        ImageUri = imageUri;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }
}
