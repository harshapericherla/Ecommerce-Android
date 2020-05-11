package com.conestoga.database;

import androidx.annotation.NonNull;

import com.conestoga.interfaces.FireStoreUserInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UsersDao {


    public static void storeUserData(final FireStoreUserInterface fireStoreUserInterface)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> user = new HashMap<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        user.put("userName",firebaseUser.getDisplayName());
        user.put("email",firebaseUser.getEmail());

        db.collection("users").document(mAuth.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                 fireStoreUserInterface.userCreated();
            }
        });
    }

    public static void checkuserExists(final FireStoreUserInterface fireStoreUserInterface)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        db.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    boolean result = false;
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists())
                    {
                        result = true;
                    }
                    fireStoreUserInterface.isUserExists(result);
                }

            }
        });
    }

}
