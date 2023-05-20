package com.example.readercollect;

public class Category {
    String CategoryName, CategoryDate;
    int MaxItems;

    public Category() {
    }

    public Category(String categoryName, String categoryDate, int maxItems) {
        CategoryName = categoryName;
        CategoryDate = categoryDate;
        MaxItems = maxItems;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryDate() {
        return CategoryDate;
    }

    public void setCategoryDate(String categoryDate) {
        CategoryDate = categoryDate;
    }

    public int getMaxItems() {
        return MaxItems;
    }

    public void setMaxItems(int maxItems) {
        MaxItems = maxItems;
    }
}
