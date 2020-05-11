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
import com.conestoga.fragments.CheckoutDetailsFragment;
import com.conestoga.fragments.ProductDetailsFragment;
import com.conestoga.fragments.ProductFragment;
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.models.CartModel;
import com.conestoga.models.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductItemHolder> {

    private List<ProductModel> productList;
    private ProductFragment productActivity;

    public ProductAdapter(ProductFragment productActivity, List<ProductModel> productList) {
        this.productActivity = productActivity;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductItemHolder((MenuActivity) this.productActivity.getActivity(),itemView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemHolder holder, int position) {

        ProductModel product = productList.get(position);
        holder.title.setText(product.getName());
        holder.description.setText(product.getSubDescription());
        holder.price.setText("$" + String.valueOf(product.getPrice()));
        holder.productModel = product;
        Picasso.get().load(product.getImageUrls().get(0)).into(holder.imageView);

    }
    public class ProductItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, FireStoreCartInterface {

        TextView title;
        TextView description;
        TextView price;
        ImageView imageView;
        ProductModel productModel;
        Button addToCartBtn;
        MenuActivity menuActivity;

        public ProductItemHolder(MenuActivity activity,View view) {
            super(view);
            title = view.findViewById(R.id.txtTitle);
            description = view.findViewById(R.id.txtDesc);
            price = view.findViewById(R.id.txtPrice);
            imageView = view.findViewById(R.id.imageViewProduct);
            addToCartBtn = view.findViewById(R.id.btnAddtoCart);
            this.menuActivity = activity;

            view.setClickable(true);
            view.setOnClickListener(this);

            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CartDao.addProductToCart(productModel,ProductItemHolder.this);
                }
            });
        }

        @Override
        public void onClick(View view) {

            FragmentTransaction ft = productActivity.getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ProductDetailsFragment newFragment = ProductDetailsFragment.newInstance(productModel);
            ft.replace(R.id.fragment_container, newFragment, "detailFragment").addToBackStack("productFragment");
            ft.commit();
        }

        @Override
        public void cartUpdated(CartModel cartModel) {
              menuActivity.updateCart(String.valueOf(cartModel.getTotalProducts()));
              Toasts.displayProductAdded(menuActivity.getApplicationContext(),productModel);
        }
    }

}
