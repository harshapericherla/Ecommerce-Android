package com.conestoga.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.List;

public class ProductModel implements Parcelable {

    private String id;
    private String name;
    private String subDescription;
    private String description;
    private double price;
    private List<String> imageUrls;
    private int count;
    private String orderId;

    public ProductModel()
    {

    }

    public ProductModel(String id, String name, String subDescription, String description, double price,  List<String> imageUrls) {
        this.id = id;
        this.name = name;
        this.subDescription = subDescription;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
    }

    public ProductModel(String id, String name, String subDescription, String description, double price,  List<String> imageUrls,int count) {
        this.id = id;
        this.name = name;
        this.subDescription = subDescription;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
        this.count = count;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected ProductModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        subDescription = in.readString();
        description = in.readString();
        price = in.readDouble();
        imageUrls = in.createStringArrayList();
        count = in.readInt();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(subDescription);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeStringList(imageUrls);
        dest.writeInt(count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public void setSubDescription(String subDescription) {
        this.subDescription = subDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
