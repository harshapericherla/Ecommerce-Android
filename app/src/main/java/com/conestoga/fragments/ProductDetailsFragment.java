package com.conestoga.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.conestoga.activities.MenuActivity;
import com.conestoga.activities.R;
import com.conestoga.adapters.ProductDetailsViewPageAdapter;
import com.conestoga.common.Toasts;
import com.conestoga.database.CartDao;
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.models.CartModel;
import com.conestoga.models.ProductModel;

import me.relex.circleindicator.CircleIndicator;

public class ProductDetailsFragment extends Fragment implements FireStoreCartInterface {

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private ProductDetailsViewPageAdapter viewPageAdapter;
    private TextView titleView;
    private TextView descView;
    private TextView subDescView;
    private TextView priceView;
    private Button addToCartBtn;
    private Button buyNowBtn;
    private ProductModel productModel;
    boolean isBuyNowClicked = false;

    public static ProductDetailsFragment newInstance(ProductModel productModel) {

        Bundle args = new Bundle();
        args.putParcelable("productModel",productModel);
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.product_details,container,false);
        productModel = (ProductModel) getArguments().get("productModel");

        titleView = view.findViewById(R.id.title);
        descView = view.findViewById(R.id.desc);
        subDescView = view.findViewById(R.id.sub_desc);
        priceView = view.findViewById(R.id.price);
        addToCartBtn = view.findViewById(R.id.add_to_cart_btn);
        buyNowBtn = view.findViewById(R.id.buy_now_btn);

        titleView.setText(productModel.getName());
        descView.setText(productModel.getDescription());
        subDescView.setText(productModel.getSubDescription());
        priceView.setText("$"+Double.valueOf(productModel.getPrice()).toString());
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartDao.addProductToCart(productModel,ProductDetailsFragment.this);
            }
        });

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBuyNowClicked = true;
                CartDao.addProductToCart(productModel,ProductDetailsFragment.this);
            }
        });

        viewPageAdapter = new ProductDetailsViewPageAdapter(getContext(),productModel.getImageUrls());
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPageAdapter);
        circleIndicator = view.findViewById(R.id.circle);
        circleIndicator.setViewPager(viewPager);
        return view;
    }

    @Override
    public void cartUpdated(CartModel cartModel) {
        MenuActivity menuActivity = (MenuActivity) getActivity();
        menuActivity.updateCart(String.valueOf(cartModel.getTotalProducts()));
        if(isBuyNowClicked)
        {
            menuActivity.navigateToCheckout();
        }
        else
        {
            Toasts.displayProductAdded(menuActivity.getApplicationContext(),productModel);
        }
    }
}
