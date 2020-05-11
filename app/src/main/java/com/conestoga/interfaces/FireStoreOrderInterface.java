package com.conestoga.interfaces;

import com.conestoga.models.CartModel;

import java.util.List;

public interface FireStoreOrderInterface {
    public void cartUpdated(List<CartModel> cartModels);
}
