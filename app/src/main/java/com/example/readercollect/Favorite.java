package com.example.readercollect;

public class Favorite {
    private String BookName, ImageUri;

    public Favorite() {
    }

    public Favorite(String bookName, String imageUri) {
        BookName = bookName;
        ImageUri = imageUri;
    }

    public String getBookName() {
        return BookName;
    }

    public String getImageUri() {
        return ImageUri;
    }
}
