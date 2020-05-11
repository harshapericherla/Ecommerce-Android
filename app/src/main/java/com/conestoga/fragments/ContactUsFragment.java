package com.conestoga.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.conestoga.activities.MenuActivity;
import com.conestoga.activities.R;

public class ContactUsFragment extends Fragment {
    private EditText mEditTextMessage;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.contactus,container,false);
         mEditTextMessage= view.findViewById(R.id.edit_text_message);
         ((MenuActivity)getActivity()).checkPageItem(3);
         Button buttonSend = view.findViewById(R.id.button_send);

         buttonSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 sendMail();
             }
         });

         return view;
     }

    private void sendMail(){
        String recipientList = "ecommerce@email.com";
        String[] recipients = recipientList.split(",");
        String message = mEditTextMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,recipients);
        intent.putExtra(Intent.EXTRA_TEXT,message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose Email Client"));
    }
}
