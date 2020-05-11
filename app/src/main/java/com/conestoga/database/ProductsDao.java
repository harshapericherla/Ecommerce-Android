package com.conestoga.database;

import androidx.annotation.NonNull;

import com.conestoga.interfaces.FireStoreCategoryInterface;
import com.conestoga.interfaces.FireStoreProductInterface;
import com.conestoga.models.CategoryModel;
import com.conestoga.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductsDao {

     public static void getProductModels(final FireStoreProductInterface fireStoreInterface)
     {
         FirebaseFirestore db = FirebaseFirestore.getInstance();

         final List<ProductModel> productList = new ArrayList<ProductModel>();

         db.collection("products")
                 .get()
                 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if (task.isSuccessful()) {
                             for (QueryDocumentSnapshot document : task.getResult()) {

                                 String  description =  document.getData().get("description").toString();
                                 String  name   =  document.getData().get("name").toString();
                                 double  price   =  Double.parseDouble(document.getData().get("price").toString());
                                 String  subDesc   =  document.getData().get("subDescription").toString();
                                 List<String> imageUrls = (List<String>)document.getData().get("imageUrls");
                                 String id = document.getId();

                                 ProductModel model = new ProductModel(id,name,subDesc,description,price,imageUrls);
                                 productList.add(model);
                             }
                             fireStoreInterface.getProductList(productList);
                         }
                     }
                 });

     }

    public static void getCategoryModels(final FireStoreCategoryInterface fireStoreInterface)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final List<CategoryModel> categoryList = new ArrayList<CategoryModel>();

        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String  name   =  document.getData().get("name").toString();
                                String id = document.getId();

                                CategoryModel model = new CategoryModel(id,name);
                                categoryList.add(model);
                            }
                        }
                        fireStoreInterface.getCategoryList(categoryList);
                    }
                });

    }


    public static void getProductModels(final FireStoreProductInterface fireStoreInterface,String categoryId)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final List<ProductModel> productList = new ArrayList<ProductModel>();

        db.collection("products").whereEqualTo("category_id",categoryId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String  description =  document.getData().get("description").toString();
                                String  name   =  document.getData().get("name").toString();
                                double  price   =  Double.parseDouble(document.getData().get("price").toString());
                                String  subDesc   =  document.getData().get("subDescription").toString();
                                List<String> imageUrls = (List<String>)document.getData().get("imageUrls");
                                String id = document.getId();

                                ProductModel model = new ProductModel(id,name,subDesc,description,price,imageUrls);
                                productList.add(model);
                            }
                            fireStoreInterface.getProductList(productList);
                        }
                    }
                });

    }
}
