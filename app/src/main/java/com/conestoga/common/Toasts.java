package com.conestoga.common;

import android.content.Context;
import android.widget.Toast;

import com.conestoga.models.ProductModel;

public class Toasts {

    public static void displayProductAdded(Context context, ProductModel model)
    {
        String toastMessage = "Product "+model.getName()+" added to the cart";
        Toast.makeText(context,toastMessage,Toast.LENGTH_SHORT).show();
    }
}
