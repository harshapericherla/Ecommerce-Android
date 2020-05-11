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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.activities.MenuActivity;
import com.conestoga.activities.R;
import com.conestoga.adapters.CheckoutAdapter;
import com.conestoga.adapters.OrderAdapter;
import com.conestoga.database.CartDao;
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.interfaces.FireStoreOrderInterface;
import com.conestoga.models.CartModel;
import com.conestoga.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsFragment extends Fragment implements FireStoreOrderInterface {

    private RecyclerView checkoutRecyclerView;
    private OrderAdapter checkoutAdapter;
    private List<CartModel> cartModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.order,container,false);
        MenuActivity menuActivity = (MenuActivity) getActivity();
        menuActivity.checkPageItem(2);

        this.cartModels = new ArrayList<CartModel>();
        checkoutAdapter = new OrderAdapter(this.cartModels,this);

        CartDao.getOrderRecords(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        checkoutRecyclerView = view.findViewById(R.id.chRecyclerView);
        checkoutRecyclerView.setLayoutManager(mLayoutManager);
        checkoutRecyclerView.setItemAnimator(new DefaultItemAnimator());
        checkoutRecyclerView.setAdapter(checkoutAdapter);

        return view;
    }

    @Override
    public void cartUpdated(List<CartModel> cartModels) {

         MenuActivity menuActivity = (MenuActivity) getActivity();

         for(int i = 0;i<cartModels.size();i++)
         {
             this.cartModels.add(cartModels.get(i));
         }
         checkoutAdapter.notifyDataSetChanged();
    }

}