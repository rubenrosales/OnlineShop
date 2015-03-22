package com.example.lemon.onlineshop;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lemon.onlineshop.Library.SessionManager;

import java.util.HashMap;

/**
 * Created by Andrey on 3/18/2015.
 * Home page activity for user
 */
public class HomeActivity extends Activity {
    SessionManager session;
    Button btnCart;
    Button btnCheckout;
    Button btnLogout;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Create session class instance
        session = new SessionManager(getApplicationContext());
        // Find all buttons
        btnCart = (Button) findViewById(R.id.btnCart);
        btnCheckout = (Button) findViewById(R.id.btnCheckout);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        // Find textview
        TextView lblWelcome = (TextView) findViewById(R.id.tvWelcome);
        // Check if user logged in
        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        String email = user.get(SessionManager.KEY_EMAIL);
        lblWelcome.setText("Welcome " + email);
        // Cart button listner
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // Logout button listner
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the session data and
                // redirect user to MainActivity
                session.logoutUser();
            }
        });
        // Checkout button listner
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
}
