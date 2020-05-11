package com.conestoga.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.conestoga.database.UsersDao;
import com.conestoga.interfaces.FireStoreUserInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FireStoreUserInterface {

    static GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    Button loginBtn;
    Button registerBtn;
    private int RC_SIGN_IN = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        loginBtn = findViewById(R.id.logInBtn);
        loginBtn.setOnClickListener(this);

        registerBtn = findViewById(R.id.btnRegister);
        registerBtn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.google_signIn).setOnClickListener(this);
        final TextView emailTv = findViewById(R.id.email);
        final TextView passTv = findViewById(R.id.password);

        emailTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String emailTxt = emailTv.getText().toString();
                String passTxt = passTv.getText().toString();
                if(emailTxt.equals("Enter Your Email"))
                {
                    emailTv.setText("");
                }
                if(passTxt.equals(""))
                {
                    passTv.setText("password");
                }
            }
        });

        passTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String passTxt = passTv.getText().toString();
                String emailTxt = emailTv.getText().toString();
                if(passTxt.equals("password"))
                {
                    passTv.setText("");
                }
                if(emailTxt.equals(""))
                {
                    emailTv.setText("Enter Your Email");
                }
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onStart() {

        super.onStart();

        checkIfIntExistsAndDisMessage();
        checkIfUserExistAndForward();
    }

    public void checkIfUserExistAndForward() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            forwardActivity();
        }
    }

    public void checkIfIntExistsAndDisMessage() {
        Intent intent = getIntent();
        if (intent != null) {
            String message = intent.getStringExtra("message");
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            setIntent(null);
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.google_signIn) {
            signIn();
        } else if (id == R.id.logInBtn) {
            firebaseLogin();
        } else if (id == R.id.btnRegister) {
            firebaseRegister();
        }
    }

    private void firebaseLogin() {
        TextView emailTv = findViewById(R.id.email);
        TextView passTv = findViewById(R.id.password);

        String emailText = emailTv.getText().toString();
        String passText = passTv.getText().toString();

        if(emailText.equals("") || passText.equals("") || emailText.equals("Enter Your Email") || passText.equals("password"))
        {
            Toast.makeText(getApplicationContext(),"Enter proper email and password",Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailText, passText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkIfUserExistsAndCommitToDB();
                } else {
                    Log.w("LogIn Fail", "signInWithEmail:failure", task.getException());
                    String message = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void firebaseRegister() {
        TextView emailTv = findViewById(R.id.email);
        TextView passTv = findViewById(R.id.password);

        String emailText = emailTv.getText().toString();
        String passText = passTv.getText().toString();

        if(emailText.equals("") || passText.equals("") || emailText.equals("Enter Your Email") || passText.equals("password"))
        {
            Toast.makeText(getApplicationContext(),"Enter proper email and password",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(emailText, passText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkIfUserExistsAndCommitToDB();
                } else {
                    Log.w("LogIn Fail", "signInWithEmail:failure", task.getException());
                    String message = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseUser user = mAuth.getCurrentUser();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            checkIfUserExistsAndCommitToDB();
                        } else {
                            Log.w("LogIn Fail", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void forwardActivity() {
        Intent productIntent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(productIntent);
    }

    public void checkIfUserExistsAndCommitToDB() {
        UsersDao.checkuserExists(this);
    }

    @Override
    public void userCreated() {
        forwardActivity();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @Override
    public void isUserExists(boolean isExists) {
        if (!isExists) {
            FirebaseUser user = mAuth.getCurrentUser();
            UsersDao.storeUserData(this);
        } else {
            userCreated();
        }
    }
}
