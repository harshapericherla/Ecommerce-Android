package com.conestoga.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.activities.MenuActivity;
import com.conestoga.activities.R;
import com.conestoga.adapters.CategoryAdapter;
import com.conestoga.interfaces.FireStoreCategoryInterface;
import com.conestoga.interfaces.FireStoreProductInterface;
import com.conestoga.adapters.ProductAdapter;
import com.conestoga.database.ProductsDao;
import com.conestoga.models.CategoryModel;
import com.conestoga.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment implements FireStoreProductInterface, FireStoreCategoryInterface {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private RecyclerView categoryRecyclerView;
    private List<ProductModel> productList;
    private List<CategoryModel> categoryModelList;
    EditText searchTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.products,container,false);
        MenuActivity menuActivity = (MenuActivity) getActivity();
        menuActivity.checkPageItem(0);

        productRecyclerView = view.findViewById(R.id.productRecView);
        this.productList = new ArrayList<ProductModel>();
        productAdapter = new ProductAdapter(this, productList);
        ProductsDao.getProductModels(this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),2);
        productRecyclerView.setLayoutManager(mLayoutManager);
        productRecyclerView.setItemAnimator(new DefaultItemAnimator());
        productRecyclerView.setAdapter(productAdapter);

        categoryRecyclerView = view.findViewById(R.id.categoryRecView);
        this.categoryModelList = new ArrayList<CategoryModel>();
        categoryAdapter = new CategoryAdapter(this,categoryModelList);
        ProductsDao.getCategoryModels(this);

        RecyclerView.LayoutManager cLayoutManager = new GridLayoutManager(getContext(),3);
        categoryRecyclerView.setLayoutManager(cLayoutManager);
        categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        categoryRecyclerView.setAdapter(categoryAdapter);

        searchTxt = view.findViewById(R.id.searchTxt);
        Button searchButton = view.findViewById(R.id.searchBtn);
        Button resetButton = view.findViewById(R.id.resetBtn);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  String searchText = searchTxt.getText().toString();
                  filterSearchList(searchText);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFields();
            }
        });

        return view;
    }

    public void filterSearchList(String searchText)
    {
         if(!searchText.isEmpty() || !searchText.equals(""))
         {
             searchText = searchText.toLowerCase();
             List<ProductModel> lProductModels = new ArrayList<ProductModel>();
             for(int i = 0;i<productList.size();i++)
             {
                 ProductModel model = productList.get(i);
                 String name = model.getName();
                 String desc = model.getDescription();
                 String subdesc = model.getSubDescription();
                 if(name.toLowerCase().contains(searchText) || desc.toLowerCase().contains(searchText) || subdesc.toLowerCase().contains(searchText))
                 {
                     lProductModels.add(model);
                 }
             }

             productList.clear();
             getProductList(lProductModels);
         }
         else
         {
             resetFields();
         }

    }

    private void resetFields()
    {
        searchTxt.setText("");
        productList.clear();
        ProductsDao.getProductModels(ProductFragment.this);
    }

    @Override
    public void getProductList(List<ProductModel> productList) {

        this.productList.addAll(productList);
        productAdapter.notifyDataSetChanged();
    }

    public void addFilteredList(List<ProductModel> productList)
    {
          this.productList.clear();
          this.productList.addAll(productList);
          productAdapter.notifyDataSetChanged();
    }

    @Override
    public void getCategoryList(List<CategoryModel> categoryList) {
        this.categoryModelList.addAll(categoryList);
        categoryAdapter.notifyDataSetChanged();
    }
}
