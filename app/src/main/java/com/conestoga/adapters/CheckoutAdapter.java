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
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.models.CartModel;
import com.conestoga.models.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutItemHolder> {

    private List<CartModel> cartModelLt;
    private CheckoutDetailsFragment checkoutFragment;

    public CheckoutAdapter(List<CartModel> cartModelLt, CheckoutDetailsFragment checkoutFragment)
    {
        this.cartModelLt = cartModelLt;
        this.checkoutFragment = checkoutFragment;
    }
    @NonNull
    @Override
    public CheckoutItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
        return new CheckoutItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemHolder holder, int position) {

         CartModel cartModel = cartModelLt.get(0);
         if(cartModel != null && cartModel.getProductModels().size() > 0)
         {
             List<ProductModel> productModels = cartModel.getProductModels();
             ProductModel product = productModels.get(position);

             holder.title.setText(product.getName());
             holder.description.setText(product.getSubDescription());
             holder.price.setText("$" + String.valueOf(product.getPrice() * product.getCount()));
             holder.count.setText(String.valueOf(product.getCount()));
             holder.productModel = product;
             holder.cartModel = cartModel;
             Picasso.get().load(product.getImageUrls().get(0)).into(holder.imageView);
         }
    }

    @Override
    public int getItemCount() {

        if(cartModelLt.size() > 0)
        {
            CartModel cartModel = cartModelLt.get(0);
            return cartModel.getProductModels().size();
        }
        return 0;
    }

    public class CheckoutItemHolder extends RecyclerView.ViewHolder implements FireStoreCartInterface{

        ProductModel productModel;
        CartModel cartModel;
        TextView title;
        TextView description;
        TextView price;
        TextView count;
        ImageView imageView;
        Button plusBtn;
        Button minusBtn;
        Button removeBtn;

        public CheckoutItemHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chItTitle);
            description = itemView.findViewById(R.id.chItDesc);
            price = itemView.findViewById(R.id.chItPrice);
            imageView = itemView.findViewById(R.id.chItImageView);
            count = itemView.findViewById(R.id.chItCount);
            plusBtn = itemView.findViewById(R.id.plusBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            removeBtn = itemView.findViewById(R.id.removeBtn);

            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CartDao.updateCartRecord(productModel,CheckoutItemHolder.this,cartModel, OperationType.Add);
                }
            });

            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CartDao.updateCartRecord(productModel,CheckoutItemHolder.this,cartModel, OperationType.Minus);
                }
            });

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CartDao.updateCartRecord(productModel,CheckoutItemHolder.this,cartModel, OperationType.Delete);
                }
            });

        }

        @Override
        public void cartUpdated(CartModel cartModel) {
            checkoutFragment.cartUpdated(cartModel);
        }
    }
}