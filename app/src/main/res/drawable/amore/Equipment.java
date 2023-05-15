package com.example.amore;

public class Equipment {
    private String EquipmentName;
    private String ImageUrl;

    public Equipment(String equipmentName, String imageUrl) {
        EquipmentName = equipmentName;
        ImageUrl = imageUrl;
    }

    public Equipment() {
    }

    public String getEquipmentName() {
        return EquipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        EquipmentName = equipmentName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
