package com.conestoga.models;

import java.util.List;

public class CheckoutModel {

    private List<ProductModel> products;
    private double total;

    public List<ProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductModel> products) {
        this.products = products;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
