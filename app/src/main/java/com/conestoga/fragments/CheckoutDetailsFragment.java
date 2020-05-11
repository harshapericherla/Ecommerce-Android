package com.conestoga.fragments;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.activities.MenuActivity;
import com.conestoga.activities.R;
import com.conestoga.adapters.CheckoutAdapter;
import com.conestoga.database.CartDao;
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.models.CartModel;
import com.conestoga.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class CheckoutDetailsFragment extends Fragment implements FireStoreCartInterface {

    private RecyclerView checkoutRecyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<CartModel> cartModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.checkout,container,false);
        MenuActivity menuActivity = (MenuActivity) getActivity();
        menuActivity.checkPageItem(1);

        this.cartModel = new ArrayList<CartModel>();
        checkoutAdapter = new CheckoutAdapter(this.cartModel,this);

        CartDao.getCart(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        checkoutRecyclerView = view.findViewById(R.id.chRecyclerView);
        checkoutRecyclerView.setLayoutManager(mLayoutManager);
        checkoutRecyclerView.setItemAnimator(new DefaultItemAnimator());
        checkoutRecyclerView.setAdapter(checkoutAdapter);


        Button button = view.findViewById(R.id.chPayment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartModel.get(0).getTotalProducts() > 0)
                {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    CardCheckoutFragment newFragment = CardCheckoutFragment.newInstance(cartModel.get(0));
                    ft.replace(R.id.fragment_container, newFragment, "checkoutFragment").addToBackStack("cardFragment");
                    ft.commit();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Add a Product to Cart to checkout",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void cartUpdated(CartModel cartModel) {

         MenuActivity menuActivity = (MenuActivity) getActivity();
         this.cartModel.add(cartModel);
         checkoutAdapter.notifyDataSetChanged();
         setTotalQty(cartModel);
         menuActivity.updateCart(""+cartModel.getTotalProducts());
    }

    public void setTotalQty(CartModel cartModel)
    {
        View view = getView();
        double totalCount = 0;
        TextView totalPrice = view.findViewById(R.id.chTotalValue);
        List<ProductModel> productModels = cartModel.getProductModels();
        for(ProductModel productModel : productModels)
        {
            totalCount += productModel.getCount() * productModel.getPrice();
        }
        totalPrice.setText("$"+String.valueOf(totalCount));
    }

}