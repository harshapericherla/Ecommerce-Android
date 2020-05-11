package com.conestoga.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.conestoga.common.OperationType;
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.interfaces.FireStoreOrderInterface;
import com.conestoga.models.CartModel;
import com.conestoga.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartDao {

    public static void addProductToCart(final ProductModel model, final FireStoreCartInterface fireStoreCartInterface)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        db.collection("cart").whereEqualTo("userId",mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful())
                {
                    QuerySnapshot querySnapshot = task.getResult();

                    if(!querySnapshot.isEmpty())
                    {
                        List<DocumentSnapshot> queryDocuments = querySnapshot.getDocuments();
                        updateCartRecord(model,fireStoreCartInterface,queryDocuments.get(0));

                    }
                    else
                    {
                        createNewCartRecord(model,fireStoreCartInterface);
                    }
                }
            }
        });
    }

    public static void createNewCartRecord(ProductModel model, final FireStoreCartInterface fireStoreCartInterface)
    {
          Map<String,Object> cartMap = new HashMap<>();
          FirebaseFirestore db = FirebaseFirestore.getInstance();
          final FirebaseAuth mAuth = FirebaseAuth.getInstance();

          List<ProductModel> products = new ArrayList<ProductModel>();
          model.setCount(1);
          products.add(model);

          cartMap.put("userId",mAuth.getUid());
          cartMap.put("productModels",products);
          cartMap.put("totalProducts",products.size());


        db.collection("cart").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    DocumentReference documentReference = task.getResult();
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            CartModel cartModel = new CartModel(documentSnapshot,mAuth.getUid());
                            fireStoreCartInterface.cartUpdated(cartModel);
                        }
                    });
                }
                else
                {
                    Log.i("Result","not successful");
                }
            }
        });
    }

    public static void updateCartRecord(ProductModel model, final FireStoreCartInterface fireStoreCartInterface, final CartModel cartModel, OperationType operationType)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        List<ProductModel> productModels = cartModel.getProductModels();
        int totalCount = 0;

        switch (operationType)
        {
            case Add:
                for(int i =0;i<productModels.size();i++)
                {
                    ProductModel productModel = (ProductModel) productModels.get(i);
                    if(productModel.getId().equals(model.getId()))
                    {
                        int count = productModel.getCount() + 1;
                        productModel.setCount(count);
                        break;
                    }
                }
                totalCount = cartModel.getTotalProducts() + 1;
                break;
            case Minus:
                boolean isRemove = true;
                ProductModel modelRemove = null;
                for(int i =0;i<productModels.size();i++)
                {
                    ProductModel productModel = (ProductModel) productModels.get(i);
                    if(productModel.getId().equals(model.getId()))
                    {
                        int count = productModel.getCount() - 1;
                        if(count == 0)
                        {
                            isRemove = true;
                            modelRemove = productModel;
                            break;
                        }
                        productModel.setCount(count);
                        break;
                    }
                }
                if(isRemove)
                {
                    productModels.remove(modelRemove);
                }
                totalCount = cartModel.getTotalProducts() - 1;
                break;
            case Delete:
                ProductModel removeModel = null;
                for(int i =0;i<productModels.size();i++)
                {
                    ProductModel productModel = (ProductModel) productModels.get(i);
                    if(productModel.getId().equals(model.getId()))
                    {
                        removeModel = productModel;
                        break;
                    }
                }
                productModels.remove(removeModel);
                totalCount = cartModel.getTotalProducts() - removeModel.getCount();
                break;
        }
        cartModel.setTotalProducts(totalCount);

        Map<String,Object> updateMap = new HashMap<String, Object>();
        updateMap.put("totalProducts",cartModel.getTotalProducts());
        updateMap.put("productModels",cartModel.getProductModels());

        db.collection("cart").document(cartModel.getCartId()).update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fireStoreCartInterface.cartUpdated(cartModel);
            }
        });
    }

    public static void insertOrderRecords(final FireStoreCartInterface fireStoreCartInterface, final CartModel cartModel)
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final List<ProductModel> productModels = cartModel.getProductModels();

        cartModel.setTotalProducts(0);
        cartModel.setProductModels(new ArrayList<ProductModel>());

        Map<String,Object> updateMap = new HashMap<String, Object>();
        updateMap.put("totalProducts",cartModel.getTotalProducts());
        updateMap.put("productModels",cartModel.getProductModels());

        db.collection("cart").document(cartModel.getCartId()).update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                HashMap<String,Object> orderMap = new HashMap<String, Object>();
                orderMap.put("userId",mAuth.getUid());
                orderMap.put("productModels",productModels);
                orderMap.put("totalProducts",productModels.size());

                db.collection("order").add(orderMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful())
                        {
                            DocumentReference documentReference = task.getResult();
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    fireStoreCartInterface.cartUpdated(cartModel);
                                }
                            });
                        }
                        else
                        {
                            Log.i("Result","not successful");
                        }
                    }
                });
            }
        });
    }

    public static void getOrderRecords(final FireStoreOrderInterface fireStoreOrderInterface)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db.collection("order").whereEqualTo("userId",mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful())
                {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<CartModel> orderCartModels = new ArrayList<CartModel>();
                    if(!querySnapshot.isEmpty())
                    {
                        List<DocumentSnapshot> queryDocuments = querySnapshot.getDocuments();
                        for(int i=0;i<queryDocuments.size();i++) {
                            DocumentSnapshot documentSnapshot = queryDocuments.get(i);
                            HashMap<String, Object> documentMap = (HashMap) documentSnapshot.getData();
                            CartModel cartModel = new CartModel(documentSnapshot, mAuth.getUid());
                            orderCartModels.add(cartModel);
                        }
                    }
                    fireStoreOrderInterface.cartUpdated(orderCartModels);
                }
            }
        });
    }


    public static void updateCartRecord(ProductModel model, final FireStoreCartInterface fireStoreCartInterface, DocumentSnapshot documentSnapshot)
    {
        Map<String,Object> cartMap = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        HashMap<String,Object> documentMap = (HashMap)documentSnapshot.getData();
        final CartModel cartModel = new CartModel(documentSnapshot,mAuth.getUid());

        List<ProductModel> productModels = cartModel.getProductModels();
        boolean productExists = false;

        for(int i =0;i<productModels.size();i++)
        {
            ProductModel productModel = (ProductModel) productModels.get(i);
            if(productModel.getId().equals(model.getId()))
            {
                int count = productModel.getCount() + 1;
                productModel.setCount(count);
                productExists = true;
                break;
            }
        }

        if(!productExists)
        {
            model.setCount(1);
            productModels.add(model);
        }
        cartModel.setTotalProducts(cartModel.getTotalProducts() + 1);

        Map<String,Object> updateMap = new HashMap<String, Object>();
        updateMap.put("totalProducts",cartModel.getTotalProducts());
        updateMap.put("productModels",cartModel.getProductModels());

        db.collection("cart").document(cartModel.getCartId()).update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fireStoreCartInterface.cartUpdated(cartModel);
            }
        });
    }

    public static void getCart(final FireStoreCartInterface fireStoreCartInterface)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db.collection("cart").whereEqualTo("userId",mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful())
                {
                    QuerySnapshot querySnapshot = task.getResult();

                    if(!querySnapshot.isEmpty())
                    {
                        List<DocumentSnapshot> queryDocuments = querySnapshot.getDocuments();
                        DocumentSnapshot documentSnapshot = queryDocuments.get(0);
                        HashMap<String,Object> documentMap = (HashMap)documentSnapshot.getData();
                        final CartModel cartModel = new CartModel(documentSnapshot,mAuth.getUid());
                        fireStoreCartInterface.cartUpdated(cartModel);
                    }
                }
            }
        });
    }
}
