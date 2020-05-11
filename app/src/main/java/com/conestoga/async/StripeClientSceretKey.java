package com.conestoga.async;

import android.os.AsyncTask;

import com.conestoga.interfaces.StripeClientSecretKeyInterface;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;

public class StripeClientSceretKey extends AsyncTask<Void,Void,String> {

    private long totalAmount;
    private StripeClientSecretKeyInterface stripeClientSecretKeyInterface;
    public StripeClientSceretKey(StripeClientSecretKeyInterface stripeClientSecretKeyInterface,double totalAmount)
    {
        this.totalAmount = (long)totalAmount;
        this.stripeClientSecretKeyInterface = stripeClientSecretKeyInterface;
    }
    @Override
    protected String doInBackground(Void... voids) {

        String clientSecret = "";
        com.stripe.Stripe.apiKey = "sk_test_Zv7l8s5NgL3lxBXGesD127IK00Hw5Ne4Sv";
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(totalAmount*100)
                .setCurrency("usd")
                .build();
        try {
            PaymentIntent intent = PaymentIntent.create(params);
            clientSecret = intent.getClientSecret();
        }catch (StripeException e) {
            e.printStackTrace();
        }
        return clientSecret;
    }

    protected void onPostExecute(String key) {
         this.stripeClientSecretKeyInterface.clientSecretKey(key);
    }
}
