package com.conestoga.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartModel implements Parcelable {

    private String cartId;
    private String userId;
    private List<ProductModel> productModels;
    private int totalProducts;

    public CartModel()
    {

    }
    public CartModel(DocumentSnapshot documentSnapshot, String userId)
    {

        Map<String,Object> documentMap =  documentSnapshot.getData();
        this.totalProducts = ((Long)documentMap.get("totalProducts")).intValue();
        this.userId = userId;
        this.cartId = documentSnapshot.getId();

        List<ProductModel> productModels = new ArrayList<ProductModel>();
        List<HashMap<String,Object>> modelMapLt = (List<HashMap<String,Object>>)documentMap.get("productModels");
        for(HashMap<String,Object> mp : modelMapLt)
        {

            int count = ((Long)mp.get("count")).intValue();
            String description = (String)mp.get("description");
            String id = (String)mp.get("id");
            List<String> imageUrls = (List)mp.get("imageUrls");
            String name = (String)mp.get("name");
            double price = ((double)mp.get("price"));
            String subDescription = (String)mp.get("subDescription");

            ProductModel productModel = new ProductModel(id,name,subDescription,description,price,imageUrls,count);
            productModels.add(productModel);
        }
        this.productModels = productModels;
    }

    protected boolean isBooleanOrEmpty(Boolean value)
    {
        if(value == null)
        {
            return false;
        }
        return value;
    }

    protected CartModel(Parcel in) {
        cartId = in.readString();
        userId = in.readString();
        productModels = in.createTypedArrayList(ProductModel.CREATOR);
        totalProducts = in.readInt();
    }

    public static final Creator<CartModel> CREATOR = new Creator<CartModel>() {
        @Override
        public CartModel createFromParcel(Parcel in) {
            return new CartModel(in);
        }

        @Override
        public CartModel[] newArray(int size) {
            return new CartModel[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ProductModel> getProductModels() {
        return productModels;
    }

    public void setProductModels(List<ProductModel> productModels) {
        this.productModels = productModels;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cartId);
        parcel.writeString(userId);
        parcel.writeTypedList(productModels);
        parcel.writeInt(totalProducts);
    }
}
