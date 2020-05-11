package com.conestoga.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.conestoga.activities.MenuActivity;
import com.conestoga.activities.R;
import com.conestoga.async.StripeClientSceretKey;
import com.conestoga.common.OperationType;
import com.conestoga.database.CartDao;
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.interfaces.StripeClientSecretKeyInterface;
import com.conestoga.models.CartModel;
import com.conestoga.models.ProductModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

public class CardCheckoutFragment extends Fragment implements StripeClientSecretKeyInterface, FireStoreCartInterface {

    private String stripeClientSecret;
    private Stripe stripe;
    private MenuActivity menuActivity;
    private CartModel cartModel;


    public static CardCheckoutFragment newInstance(CartModel cartModel) {

        Bundle args = new Bundle();
        args.putParcelable("cartModel",cartModel);
        CardCheckoutFragment fragment = new CardCheckoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.card_details, container, false);
        menuActivity = (MenuActivity) getActivity();
        menuActivity.checkPageItem(1);
        cartModel = (CartModel) getArguments().get("cartModel");

        final double totalAmount = getTotalAmount();
        TextView textView = view.findViewById(R.id.amntValue);
        textView.setText("$"+String.valueOf(totalAmount));

        callStripeToGetKey(totalAmount);
        Button payButton = view.findViewById(R.id.payButton);
        final CardInputWidget cardInputWidget = view.findViewById(R.id.cardInputWidget);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalAmount <= 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Add Items to cart",Toast.LENGTH_LONG).show();
                }
                else if (stripeClientSecret.length() > 0) {
                    PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                    if (params != null) {
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams.
                                createWithPaymentMethodCreateParams(params, stripeClientSecret);
                        final Context context = getActivity().getApplicationContext();
                        PaymentConfiguration.init(context,stripeClientSecret);
                        stripe = new Stripe(context,"pk_test_UohHLDgFYC5LQmINvWH9LQ3X00xTfKg4EY");

                        stripe.confirmPayment(getActivity(), confirmParams);

                    }
                }
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }


    public void callStripeToGetKey(double totalAmount)
    {
        StripeClientSceretKey stripeClientSceretKey = new StripeClientSceretKey(this,totalAmount);
        stripeClientSceretKey.execute();
    }

    @Override
    public void clientSecretKey(String secretKey) {
         this.stripeClientSecret = secretKey;
    }

    @Override
    public void cartUpdated(CartModel cartModel) {

        this.menuActivity.updateCart(String.valueOf(cartModel.getTotalProducts()));

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        OrderDetailsFragment newFragment = new OrderDetailsFragment();
        ft.replace(R.id.fragment_container, newFragment, "cardFragment").addToBackStack("orderDetailsFragment");
        ft.commit();
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<CardCheckoutFragment> cardCheckoutRef;

        PaymentResultCallback(@NonNull CardCheckoutFragment cardCheckoutFragment) {
            cardCheckoutRef = new WeakReference<>(cardCheckoutFragment);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CardCheckoutFragment fragment = cardCheckoutRef.get();
            if (fragment == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                fragment.displayAlert(
                        "Payment completed",
                        gson.toJson(paymentIntent),
                        true
                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed
                fragment.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),
                        false
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CardCheckoutFragment fragment = cardCheckoutRef.get();
            if (fragment == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            fragment.displayAlert("Error", e.toString(), false);
        }
    }

    public double getTotalAmount()
    {
        double totalCount = 0;
        List<ProductModel> productModels = cartModel.getProductModels();
        for(ProductModel productModel : productModels)
        {
            totalCount += productModel.getCount() * productModel.getPrice();
        }
        return totalCount;
    }

    private void displayAlert(String error, String toString, boolean success) {
         if(success)
         {
             Toast.makeText(getActivity().getApplicationContext(),"Transaction successful",Toast.LENGTH_SHORT).show();
             CartDao.insertOrderRecords(this,cartModel);
         }
         else
         {
             Toast.makeText(getActivity().getApplicationContext(),"Transaction has been failed",Toast.LENGTH_SHORT).show();
         }
    }
}