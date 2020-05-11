package com.conestoga.interfaces;

import com.conestoga.models.ProductModel;

import java.util.List;

public interface FireStoreProductInterface {

    public void getProductList(List<ProductModel> productList);

}
