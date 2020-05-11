package com.conestoga.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.activities.R;
import com.conestoga.common.OperationType;
import com.conestoga.database.CartDao;
import com.conestoga.fragments.CheckoutDetailsFragment;
import com.conestoga.fragments.OrderDetailsFragment;
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.models.CartModel;
import com.conestoga.models.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.CheckoutItemHolder> {

    private List<CartModel> cartModels;
    private OrderDetailsFragment checkoutFragment;
    private List<ProductModel> productModels;

    public OrderAdapter(List<CartModel> cartModelLt, OrderDetailsFragment checkoutFragment)
    {
        this.cartModels = cartModelLt;
        this.checkoutFragment = checkoutFragment;
        this.productModels = new ArrayList<ProductModel>();
    }
    @NonNull
    @Override
    public CheckoutItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new CheckoutItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemHolder holder, int position) {

         if(productModels.size() == 0)
         {
             for(int i =0;i<cartModels.size();i++)
             {
                 CartModel cartModel = cartModels.get(i);
                 List<ProductModel> lModels = cartModel.getProductModels();
                 for(int j=0;j<lModels.size();j++)
                 {
                     ProductModel lProductModel = lModels.get(j);
                     lProductModel.setOrderId(cartModel.getCartId());
                     productModels.add(lProductModel);
                 }
             }
         }

        if(cartModels != null && cartModels.size() > 0)
        {
            ProductModel product = productModels.get(position);

            holder.title.setText(product.getName());
            holder.description.setText(product.getSubDescription());
            holder.price.setText("$" + String.valueOf(product.getPrice() * product.getCount()));
            holder.count.setText(String.valueOf(product.getCount()));
            holder.productModel = product;
            holder.orderId.setText(product.getOrderId());
            Picasso.get().load(product.getImageUrls().get(0)).into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {

        int sum = 0;
        for(int i = 0;i<cartModels.size();i++)
        {
             int productModelsSize = 0;
             CartModel cartModel = cartModels.get(i);
             List<ProductModel> productModels = cartModel.getProductModels();
             if(productModels != null)
             {
                 productModelsSize = productModels.size();
             }
             sum += productModelsSize;
        }
        return sum;
    }

    public class CheckoutItemHolder extends RecyclerView.ViewHolder{

        ProductModel productModel;
        CartModel cartModel;
        TextView title;
        TextView description;
        TextView price;
        TextView count;
        TextView orderId;
        ImageView imageView;

        public CheckoutItemHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chItTitle);
            description = itemView.findViewById(R.id.chItDesc);
            price = itemView.findViewById(R.id.chItPrice);
            imageView = itemView.findViewById(R.id.chItImageView);
            count = itemView.findViewById(R.id.chItCount);
            orderId = itemView.findViewById(R.id.orderIdValue);
        }

    }
}