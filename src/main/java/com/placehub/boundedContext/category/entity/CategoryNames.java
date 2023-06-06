package com.placehub.boundedContext.category.entity;

public class CategoryNames {
    private String bigCategoryName;
    private String midCategoryName;
    private String smallCategoryName;

    public CategoryNames(String bigCategoryName, String midCategoryName, String smallCategoryName) {
        this.bigCategoryName = bigCategoryName;
        this.midCategoryName = midCategoryName;
        this.smallCategoryName = smallCategoryName;
    }

    public String getBigCategoryName() {
        return bigCategoryName;
    }

    public String getMidCategoryName() {
        return midCategoryName;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }
}