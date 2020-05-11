package com.conestoga.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conestoga.common.Constants;
import com.conestoga.database.CartDao;
import com.conestoga.fragments.CheckoutDetailsFragment;
import com.conestoga.fragments.ContactUsFragment;
import com.conestoga.fragments.OrderDetailsFragment;
import com.conestoga.fragments.ProductDetailsFragment;
import com.conestoga.fragments.ProductFragment;
import com.conestoga.interfaces.FireStoreCartInterface;
import com.conestoga.models.CartModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.stripe.model.Order;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FireStoreCartInterface {

    private DrawerLayout drawerLayout;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setActionBar();
        setTitle();

        Toast.makeText(MenuActivity.this, "login successful", Toast.LENGTH_LONG).show();
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_products);
        }
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    private Menu getMenu() {
        return this.menu;
    }

    public void setActionBar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.setMenu(menu);
        initializeCart();
        CartDao.getCart(this);
        return true;
    }

    public void initializeCart() {
        MenuItem item = getMenu().findItem(R.id.miCompose);
        item.setActionView(R.layout.shopping_cart);
        RelativeLayout relativeLayout = (RelativeLayout) item.getActionView();

        TextView textView = (TextView) relativeLayout.findViewById(R.id.actionbar_notifcation_textview);
        ImageView imageView = relativeLayout.findViewById(R.id.actionbar_ImageView);

        textView.setText("0");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCheckout();
            }
        });
    }

    public void navigateToCheckout()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        CheckoutDetailsFragment newFragment = new CheckoutDetailsFragment();
        ft.replace(R.id.fragment_container, newFragment).addToBackStack("previousScreen");
        ft.commit();
    }

    public void updateCart(String count) {

        MenuItem item = getMenu().findItem(R.id.miCompose);
        item.setActionView(R.layout.shopping_cart);
        RelativeLayout relativeLayout = (RelativeLayout) item.getActionView();

        TextView textView = (TextView) relativeLayout.findViewById(R.id.actionbar_notifcation_textview);
        ImageView imageView = relativeLayout.findViewById(R.id.actionbar_ImageView);
        if (count == null || count.equals("")) {
            count = "0";
        }
        textView.setText(count);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCheckout();
            }
        });
    }

    public void checkPageItem(int pageNumber)
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(pageNumber).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        OrderDetailsFragment newFragment = new OrderDetailsFragment();

        switch (item.getItemId()) {
            case R.id.nav_products:
                ProductFragment productFragment = new ProductFragment();
                ft.replace(R.id.fragment_container, productFragment, "productFragment").addToBackStack("mainMenu");
                ft.commit();
                break;
            case R.id.nav_checkout:
                CheckoutDetailsFragment checkoutDetailsFragment = new CheckoutDetailsFragment();
                ft.replace(R.id.fragment_container, checkoutDetailsFragment, "checkoutFragment").addToBackStack("mainMenu");
                ft.commit();
                break;
            case R.id.nav_order:
                OrderDetailsFragment orderDetailsFragment = new OrderDetailsFragment();
                ft.replace(R.id.fragment_container, orderDetailsFragment, "orderFragment").addToBackStack("mainMenu");
                ft.commit();
                break;
            case R.id.nav_contactus:
                ContactUsFragment contactUsFragment = new ContactUsFragment();
                ft.replace(R.id.fragment_container, contactUsFragment, "contactUsFragment").addToBackStack("mainMenu");
                ft.commit();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onStart() {

        super.onStart();
    }

    public void setTitle() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            TextView titleView = headerView.findViewById(R.id.titleTxt);
            String userName = user.getDisplayName();
            if(userName == null || userName.equals(""))
            {
                userName = user.getEmail();
            }
            titleView.setText(userName);

            ImageView imageView = headerView.findViewById(R.id.imgProfile);
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : Constants.PHOTO_AVATAR;
            Picasso.get().load(photoUrl).into(imageView);
        }
    }

    public void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        MainActivity.mGoogleSignInClient.signOut().addOnCompleteListener(MenuActivity.this, new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                    intent.putExtra("message", "logged out successfully");
                    startActivity(intent);
                }


            }
        });


    }

    @Override
    public void cartUpdated(CartModel cartModel) {
        updateCart(String.valueOf(cartModel.getTotalProducts()));
    }
}
