package com.example.readercollect;

public class Book {
    String BookName, ImageUri, Date;

    public Book() {
    }

    public Book(String bookName, String imageUri, String date) {
        BookName = bookName;
        ImageUri = imageUri;
        Date = date;
    }

    public String getBookName() {
        return BookName;
    }

    /*public void setBookName(String bookName) {
        BookName = bookName;
    }*/

    public String getImageUri() {
        return ImageUri;
    }

    /*public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }*/

    public String getDate() {
        return Date;
    }
}
