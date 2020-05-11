package com.conestoga.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryModel implements Parcelable {

    private String categoryId;
    private String categoryName;

    public CategoryModel()
    {

    }

    public  CategoryModel(String categoryId,String categoryName)
    {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    protected CategoryModel(Parcel in) {
        categoryId = in.readString();
        categoryName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoryId);
        dest.writeString(categoryName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
