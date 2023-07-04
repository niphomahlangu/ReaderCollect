package com.example.readercollect;

public class Book {
    String BookName, ImageUri, Date, Status;

    public Book() {
    }

    public Book(String bookName, String imageUri, String date, String status) {
        BookName = bookName;
        ImageUri = imageUri;
        Date = date;
        Status = status;
    }

    public String getBookName() {
        return BookName;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public String getDate() {
        return Date;
    }

    public String getStatus() {
        return Status;
    }
}
