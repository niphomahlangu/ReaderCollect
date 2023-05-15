package com.example.amore;

public class Booking {
    private String EquipmentName, ImageUri, Date;

    public Booking(String equipmentName, String imageUri, String date) {
        EquipmentName = equipmentName;
        ImageUri = imageUri;
        Date = date;
    }

    public Booking() {
    }

    public String getEquipmentName() {
        return EquipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        EquipmentName = equipmentName;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
