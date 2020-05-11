package com.conestoga.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.activities.MenuActivity;
import com.conestoga.activities.R;
import com.conestoga.common.Toasts;
import com.conestoga.database.CartDao;
import com.conestoga.database.ProductsDao;
import com.conestoga.fragments.ProductDetailsFragment;
import com.conestoga.fragments.ProductFragment;
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.interfaces.FireStoreProductInterface;
import com.conestoga.models.CartModel;
import com.conestoga.models.CategoryModel;
import com.conestoga.models.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryItemHolder> {

    private List<CategoryModel> categoryList;
    private ProductFragment productFragment;

    public CategoryAdapter(ProductFragment fragment, List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
        this.productFragment = fragment;
    }

    @NonNull
    @Override
    public CategoryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryItemHolder((MenuActivity) this.productFragment.getActivity(),itemView,productFragment);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryItemHolder holder, int position) {

          CategoryModel categoryModel = categoryList.get(position);
          String categoryName = categoryModel.getCategoryName();
          holder.categoryName.setText(categoryName);
          holder.categoryModel = categoryModel;
    }
    public class CategoryItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, FireStoreProductInterface {


        MenuActivity menuActivity;
        CategoryModel categoryModel;
        TextView categoryName;
        ProductFragment fragment;

        public CategoryItemHolder(MenuActivity activity,View view,ProductFragment fragment) {
            super(view);

            this.menuActivity = activity;
            this.categoryName = view.findViewById(R.id.categoryName);
            this.fragment = fragment;

            view.setClickable(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ProductsDao.getProductModels(this,categoryModel.getCategoryId());
        }

        @Override
        public void getProductList(List<ProductModel> productList) {
              this.fragment.addFilteredList(productList);
        }
    }

}
